/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package org.madsonic.service;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.ObjectUtils;

import org.madsonic.Logger;
import org.madsonic.dao.AlbumDao;
import org.madsonic.dao.ArtistDao;
import org.madsonic.dao.MediaFileDao;
import org.madsonic.dao.MusicFolderStatisticsDao;
import org.madsonic.domain.Album;
import org.madsonic.domain.Artist;
import org.madsonic.domain.Genres;
import org.madsonic.domain.MediaFile;
import org.madsonic.domain.MediaFile.MediaType;
import org.madsonic.domain.MediaLibraryStatistics;
import org.madsonic.domain.MusicFolder;
import org.madsonic.domain.MusicFolderStatistics;
import org.madsonic.util.FileUtil;

import org.apache.commons.lang.StringUtils;

/**
 * Provides services for scanning the music library.
 *
 * @author Sindre Mehus
 */
public class MediaScannerService {

    private static final int INDEX_VERSION = 16;
    private static final Logger LOG = Logger.getLogger(MediaScannerService.class);

    private MediaLibraryStatistics statistics;
    private MusicFolderStatistics folderStatistics;

    private boolean scanning;
    private Timer timer;
    private SettingsService settingsService;
    private SearchService searchService;
    private PlaylistService playlistService;
    private MediaFileService mediaFileService;
    private MediaFileDao mediaFileDao;
    private ArtistDao artistDao;
    private AlbumDao albumDao;
    
    private MusicFolderStatisticsDao musicFolderStatisticsDao;
    
    private int scanCount;

    public void init() {
        deleteOldIndexFiles();
        statistics = settingsService.getMediaLibraryStatistics();
        schedule();
    }

    /**
     * Schedule background execution of media library scanning.
     */
    public synchronized void schedule() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer(true);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                scanLibrary();
            }
        };

        long daysBetween = settingsService.getIndexCreationInterval();
        int hour = settingsService.getIndexCreationHour();

        if (daysBetween == -1) {
            LOG.info("Automatic media scanning disabled.");
            return;
        }

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        if (cal.getTime().before(now)) {
            cal.add(Calendar.DATE, 1);
        }

        Date firstTime = cal.getTime();
        long period = daysBetween * 24L * 3600L * 1000L;
        timer.schedule(task, firstTime, period);

        LOG.info("Automatic library scan: every " + daysBetween + " day(s), NEXT: " + firstTime);

        // In addition, create index immediately if it doesn't exist on disk.
        if (settingsService.getLastScanned() == null) {
            LOG.info("Media library never scanned. Doing it now.");
            scanLibrary();
        }
    }

    /**
     * Returns whether the media library is currently being scanned.
     */
    public synchronized boolean isScanning() {
        return scanning;
    }

    /**
     * Returns the number of files scanned so far.
     */
    public int getScanCount() {
        return scanCount;
    }

    public void backupAllPlaylists() {
		try {
			playlistService.backupAllPlaylists();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * Scans the media library.
     * The scanning is done asynchronously, i.e., this method returns immediately.
     */
    public synchronized void scanLibrary() {
        if (isScanning()) {
            return;
        }
        scanning = true;

        Thread thread = new Thread("MediaLibraryScanner") {
            @Override
            public void run() {

                doScanLibrary();
                
//                //TODO: rework
//                List<Artist> allArtists = artistDao.getAllArtists();
//                LOG.info("## ArtistCount: " + allArtists.size());
//                
//                lastFMService.test(allArtists);                

                playlistService.importPlaylists();
                playlistService.updatePlaylistStatistics();
            }
        };

        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    private void doScanLibrary() {
        LOG.info("Starting to scan media library.");

        try {
            Date lastScanned = new Date();

            scanCount = 0;
            artistDao.reset();
            statistics.reset();

            mediaFileService.setMemoryCacheEnabled(false);
            searchService.startIndexing();

            mediaFileService.clearMemoryCache();

            Genres genres = new Genres();
            
            // Maps from genre name to song count.
            // Map<String, Integer> genreCount = new HashMap<String, Integer>();            
            
            // Recurse through all files on disk.
            for (MusicFolder musicFolder : settingsService.getAllMusicFolders()) {
            	
	            // Maps from artist name to album count.
	            Map<String, Integer> albumCount = new HashMap<String, Integer>();

            	folderStatistics = new MusicFolderStatistics() ;
            	folderStatistics.setMusicFolderId(musicFolder.getId());
                MediaFile root = mediaFileService.getMediaFile(musicFolder.getPath(), false);
                scanFile(root, musicFolder, lastScanned, albumCount, genres);
                
                folderStatistics.setAlbumCount(albumCount.size());
                musicFolderStatisticsDao.updateMusicFolderStats(folderStatistics);

	              for (Integer albums : albumCount.values()) {
		            	  folderStatistics.incrementAlbums(albums);
		          }
                statistics.incrementAlbumArtists(albumCount.size());
                statistics.incrementAlbums(folderStatistics.getAlbumCount());
                
            }
            LOG.info("Scanned media library with " + scanCount + " entries.");

            LOG.info("Marking non-present files.");
            mediaFileDao.markNonPresent(lastScanned);
            
            LOG.info("Marking non-present artists.");
            artistDao.markNonPresent(lastScanned);
            
            LOG.info("Marking non-present albums.");
            albumDao.markNonPresent(lastScanned);

            // Update statistics
            statistics.incrementArtists(artistDao.getAllArtist());
            statistics.setGenreCount(mediaFileService.getGenres(false).size());
            
            // Update genres
            mediaFileDao.updateGenres(genres.getGenres());

            settingsService.setMediaLibraryStatistics(statistics);
            settingsService.setLastScanned(lastScanned);
            settingsService.save(false);
            LOG.info("Completed media library scan.");

        } catch (Throwable x) {
            LOG.error("Failed to scan media library.", x);
            LOG.error("ERROR " + x.getMessage().toString(), x);
            x.printStackTrace();

        } finally {
            mediaFileService.setMemoryCacheEnabled(true);
            searchService.stopIndexing();
            scanning = false;
        }
    }

    private void scanFile(MediaFile file, MusicFolder musicFolder, Date lastScanned,
                          Map<String, Integer> albumCount, Genres genres) {
        scanCount++;
        if (scanCount % 500 == 0) {
            LOG.info("Scanned media library with " + scanCount + " entries.");
        }

        searchService.index(file);

        // Update the root folder if it has changed.
        if (!musicFolder.getPath().getPath().equals(file.getFolder())) {
            file.setFolder(musicFolder.getPath().getPath());
            mediaFileDao.createOrUpdateMediaFile(file);
        }

        if (file.getMediaType() == MediaType.AUDIOBOOK ) {
        	statistics.incrementAudiobook(1);
        	folderStatistics.incrementAudiobook(1);
        	if (file.getFileSize() != null) {        	
        	folderStatistics.incrementAudiobookSize(file.getFileSize());
        	}
        }
        
        if (file.getMediaType() == MediaType.VIDEO) {
        	statistics.incrementVideo(1);        	
        	folderStatistics.incrementVideo(1);   
        	if (file.getFileSize() != null) {
        	folderStatistics.incrementVideoSize(file.getFileSize());
        	}
        }
        
        if (file.getMediaType() == MediaType.PODCAST) {
        	statistics.incrementPodcast(1);        	
        	folderStatistics.incrementPodcast(1);    
        	if (file.getFileSize() != null) {
        	folderStatistics.incrementPodcastSize(file.getFileSize());
        	}
        }

        if (file.getMediaType() == MediaType.ALBUM) {
    	   updateGenres(file, genres);
         }
        
        if (file.getMediaType() == MediaType.ARTIST) {
           mediaFileDao.createOrUpdateMediaFile(file);
       	   updateGenres(file, genres);
        }
        
        if (file.isDirectory()) {
            for (MediaFile child : mediaFileService.getChildrenOf(file, true, false, false, false)) {
                scanFile(child, musicFolder, lastScanned, albumCount, genres );
            }
            for (MediaFile child : mediaFileService.getChildrenOf(file, false, true, false, false)) {
                scanFile(child, musicFolder, lastScanned, albumCount, genres);
            }
        } else {
            
            boolean isVariousArtists = false;
            if (file.getAlbumArtist() != null) {
                if (file.getAlbumArtist().toLowerCase().contains("various") ){
                	isVariousArtists = true;
            	}
            }
            
            try {
                updateAlbum(file, lastScanned, albumCount, isVariousArtists);

	        } catch (Throwable x) {
	            LOG.error("Failed to update Album for file: " + file.getPath(), x);
	        }            
            
            try {
                updateArtist(file, lastScanned, albumCount, isVariousArtists);
	        } catch (Throwable x) {
	            LOG.error("Failed to update Artist for file: " + file.getPath(), x);
	        }            
			
            try {
            	updateGenres(file, genres);
	        } catch (Throwable x) {
	            LOG.error("Failed to update Genre for file: " + file.getPath(), x);
	        }            
			
			
            statistics.incrementSongs(1);
            folderStatistics.incrementSongs(1);            
            if (file.getFileSize() == null) { 
//	            LOG.error("##Failed to update filesize: " + file.getPath(), null);
            } else {
            	folderStatistics.incrementSongsSize(file.getFileSize());
            }
        }

        mediaFileDao.markPresent(file.getPath(), lastScanned);
        artistDao.markPresent(file.getAlbumArtist(), lastScanned);

        if (file.getDurationSeconds() != null) {
            statistics.incrementTotalDurationInSeconds(file.getDurationSeconds());
        }
        if (file.getFileSize() != null) {
            statistics.incrementTotalLengthInBytes(file.getFileSize());
          //  folderStatistics.incrementSongsSize(file.getFileSize());            
        }
    }

    private void updateGenres(MediaFile file, Genres genres) {
        String genre = file.getGenre();
        if (genre == null) {
            return;
        }
        if (file.isAlbum()) {
            genres.incrementAlbumCount(genre);
        }
        
        if (file.isSingleArtist()) {
            genres.incrementArtistCount(genre);
        }
        
        else if (file.isAudio()) {
            genres.incrementSongCount(genre);
        }
    }
    
    @Deprecated
    private void updateGenre(MediaFile file, Map<String, Integer> genreCount) {
        String genre = file.getGenre();
        if (genre == null) {
            return;
        }
        Integer count = genreCount.get(file.getGenre());
        if (count == null) {
            genreCount.put(genre, 1);
        } else {
            genreCount.put(genre, count + 1);
        }
    }

    private void updateAlbum(MediaFile file, Date lastScanned, Map<String, Integer> albumCount, boolean isVariousArtists) {
        if (file.getAlbumName() == null && file.getAlbumSetName() == null || file.getArtist() == null || file.getParentPath() == null || !file.isAudio()) {
            return;
        }
        MediaFile parent = mediaFileService.getMediaFile(file.getParentPath());

        Album album = albumDao.getAlbumSetForFile(file);
        
        if (album == null) {
        	album = albumDao.getAlbumSetForFile(parent);
        }
        if (album == null) {
            album = new Album();
             
            album.setPath(file.getParentPath());
            album.setName(file.getAlbumSetName());
            album.setNameid3(file.getAlbumName());

            album.setArtist(StringUtils.isBlank(file.getAlbumArtist()) ? file.getArtist() : file.getAlbumArtist());

			if (isVariousArtists) {
			    album.setArtist("Various Artists");
			    album.setAlbumartist("Various Artists");
			}
			    
            album.setCreated(file.getChanged());
            
            if (file.getAlbumSetName() == null) {
            	
            	if (parent.getAlbumSetName() != null) {
                    album.setSetName(parent.getAlbumSetName());
                    album.setName(parent.getAlbumSetName());
            	}
            }
            else
            {
                album.setSetName(file.getAlbumSetName());
            }
        }
        
        if (album.getCoverArtPath() == null) {
            parent = mediaFileService.getParentOf(file);
            if (parent != null) {
                album.setCoverArtPath(parent.getCoverArtPath());
            }
        }
		
        if (album.getYear() <1) {
			if (file.getYear() != null) {
				album.setYear(file.getYear());
			}
        }
		
        if (album.getGenre() == null) {
            album.setGenre(file.getGenre());
        }

        boolean firstEncounter = !lastScanned.equals(album.getLastScanned());
        if (firstEncounter) {
            album.setDurationSeconds(0);
            album.setSongCount(0);
            Integer n = albumCount.get(file.getArtist());
            albumCount.put(file.getArtist(), n == null ? 1 : n + 1);
        }
        
        if (file.getDurationSeconds() != null) {
            album.setDurationSeconds(album.getDurationSeconds() + file.getDurationSeconds());
        }
        if (file.isAudio()) {
            album.setSongCount(album.getSongCount() + 1);
        }

        album.setLastScanned(lastScanned);
        album.setPresent(true);
        
        if ( file.getAlbumName().toLowerCase().contains( parent.getAlbumName().toLowerCase())) {
            album.setMediaFileId(parent.getId());
        } else {
            album.setMediaFileId(file.getId());
        }
        
        album.setGenre(file.getGenre());
         
        if (file.getYear() != null) {
        	album.setYear(file.getYear());
        }
        
        albumDao.createOrUpdateAlbum(album);
        if (firstEncounter) {
            searchService.index(album);
        }

        // Update the file's album artist, if necessary.
        if (!ObjectUtils.equals(album.getArtist(), file.getAlbumArtist())) {
            file.setAlbumArtist(album.getArtist());
             mediaFileDao.createOrUpdateMediaFile(file);
        }
    }

    private void updateArtist(MediaFile file, Date lastScanned, Map<String, Integer> albumCount, boolean isVariousArtists) {
        if (file.getAlbumArtist() == null || file.getArtist() == null || !file.isAudio()) {
            return;
        }

        Artist artist = artistDao.getArtist(file.getArtist());
        
        if (artist == null) {
            artist = new Artist();
            artist.setName(file.getArtist());
        }
        
        if (artist.getGenre() != file.getGenre()) {
            artist.setGenre(file.getGenre());
        }
        
//        if (artist.getCoverArtPath() == null) {
//            MediaFile parent = mediaFileService.getParentOf(file);
//            MediaFile grandparent = mediaFileService.getParentOf(parent);
//            if (grandparent != null) {
//                artist.setCoverArtPath(grandparent.getCoverArtPath());
//            } else
//            {
//                if (parent != null) {
//                    artist.setCoverArtPath(parent.getCoverArtPath());
//                }	
//            }
  //      }
        boolean firstEncounter = !lastScanned.equals(artist.getLastScanned());

        Integer n = albumCount.get(artist.getName());
        artist.setAlbumCount(n == null ? 1 : n);

//        if (artist.getAlbumCount() == 0) 
//        { artist.setAlbumCount(1); }
        
        artist.incrementPlay(file.getPlayCount());
        
        artist.incrementSongs(1);

        MediaFile parent = mediaFileService.getParentOf(file);	
        MediaFile parentOfParent = mediaFileService.getParentOf(parent);	
        
        if (parent.isSingleArtist()) { //|| parent.isAlbumSet()){
        	artist.setArtistFolder (parent.getAlbumArtist());	
        	artist.setCoverArtPath(parent.getCoverArtPath());
        }
        else
        {
        if (parentOfParent.isSingleArtist()) { // || parentOfParent.isAlbumSet()){
        	artist.setArtistFolder (parentOfParent.getAlbumName());	
        	artist.setCoverArtPath(parentOfParent.getCoverArtPath());
        }
        }
//      artist.setArtistFolder(file.getPath());

        artist.setLastScanned(lastScanned);
        artist.setPresent(true);
        try{
            artistDao.createOrUpdateArtist(artist);
	    } catch (Exception x) {
	        LOG.error("Failed to createOrUpdateArtist: " + file.getPath(), x);
	    }

        if (firstEncounter) {
            searchService.index(artist);
        }
    }

    /**
     * Returns media library statistics, including the number of artists, albums and songs.
     *
     * @return Media library statistics.
     */
    public MediaLibraryStatistics getStatistics() {
        return statistics;
    }

    /**
     * Deletes old versions of the index file.
     */
    private void deleteOldIndexFiles() {
        for (int i = 2; i < INDEX_VERSION; i++) {
            File file = getIndexFile(i);
            try {
                if (FileUtil.exists(file)) {
                    if (file.delete()) {
                        LOG.info("Deleted old index file: " + file.getPath());
                    }
                }
            } catch (Exception x) {
                LOG.warn("Failed to delete old index file: " + file.getPath(), x);
            }
        }
    }

    /**
     * Returns the index file for the given index version.
     *
     * @param version The index version.
     * @return The index file for the given index version.
     */
    private File getIndexFile(int version) {
        File home = SettingsService.getMadsonicHome();
        return new File(home, "subsonic" + version + ".index");
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setLastFMService(LastFMService lastFMService) {
    }    
    
    public void setMediaFileDao(MediaFileDao mediaFileDao) {
        this.mediaFileDao = mediaFileDao;
    }

    public void setMusicFolderStatisticsDao(MusicFolderStatisticsDao musicFolderStatisticsDao) {
        this.musicFolderStatisticsDao = musicFolderStatisticsDao;
    }

    public void setArtistDao(ArtistDao artistDao) {
        this.artistDao = artistDao;
    }

    public void setAlbumDao(AlbumDao albumDao) {
        this.albumDao = albumDao;
    }
    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }
}
