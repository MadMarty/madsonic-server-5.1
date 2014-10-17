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

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.madsonic.Logger;
import org.madsonic.dao.AlbumDao;
import org.madsonic.dao.ArtistDao;
import org.madsonic.dao.MediaFileDao;
import org.madsonic.dao.MusicFolderDao;
import org.madsonic.domain.Album;
import org.madsonic.domain.Artist;
import org.madsonic.domain.Genre;
import org.madsonic.domain.MediaFile;
import org.madsonic.domain.MediaFileComparator;
import org.madsonic.domain.MediaFile.MediaType;
import org.madsonic.domain.MusicFolder;
import org.madsonic.service.metadata.JaudiotaggerParser;
import org.madsonic.service.metadata.MetaData;
import org.madsonic.service.metadata.MetaDataParser;
import org.madsonic.service.metadata.MetaDataParserFactory;
import org.madsonic.util.FileUtil;
import org.madsonic.util.StringUtil;
import org.madsonic.util.UrlFile;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.madsonic.domain.MediaFile.MediaType.*;

/**
 * Provides services for instantiating and caching media files and cover art.
 *
 * @author Sindre Mehus
 */
public class MediaFileService {
 
    private static final Logger LOG = Logger.getLogger(MediaFileService.class);

    private Ehcache mediaFileMemoryCache;

    private SecurityService securityService;
    private SettingsService settingsService;
    private MediaFileDao mediaFileDao;
    private MusicFolderDao musicFolderDao;
    private AlbumDao albumDao;
    private ArtistDao artistDao;
    private MetaDataParserFactory metaDataParserFactory;
    private boolean memoryCacheEnabled = true;

    
    /**
     * Returns a media file instance for the given file.  If possible, a cached value is returned.
     *
     * @param file A file on the local file system.
     * @return A media file instance, or null if not found.
     * @throws SecurityException If access is denied to the given file.
     */
    public MediaFile getMediaFile(File file) {
        return getMediaFile(file, settingsService.isFastCacheEnabled());
    }

    /**
     * Returns a media file instance for the given file.  If possible, a cached value is returned.
     *
     * @param file A file on the local file system.
     * @return A media file instance, or null if not found.
     * @throws SecurityException If access is denied to the given file.
     */
    public MediaFile getMediaFile(File file, boolean useFastCache) {

        // Look in fast memory cache first.
        MediaFile result = getFromMemoryCache(file);
        if (result != null) {
            return result;
        }

        if (!securityService.isReadAllowed(file)) {
            throw new SecurityException("Access denied to file " + file);
        }

        // Secondly, look in database.
        result = mediaFileDao.getMediaFile(file.getPath());
        if (result != null) {
            result = checkLastModified(result, useFastCache);
            putInMemoryCache(file, result);
            return result;
        }

        if (!FileUtil.exists(file)) {
            return null;
        }
        // Not found in database, must read from disk.
        result = createMediaFile(file);

        // Put in cache and database.
        putInMemoryCache(file, result);
        mediaFileDao.createOrUpdateMediaFile(result);

        return result;
    }
    
    
    public void createOrUpdateMediaFile(MediaFile mediaFile) {
        mediaFileDao.createOrUpdateMediaFile(mediaFile);
    }
	
    public Artist getArtistforName(String artistName){
    		if (artistName == null) {
    		return null;
    	}
    	
    	Artist a = new Artist();
    	a = artistDao.getArtist(artistName);
    	if (a == null) {
        	a = artistDao.getArtistFolder(artistName);
    	}
    	return a;
    }

    public Integer getIdForTrack(String Artist, String TrackName){
    	return mediaFileDao.getIdForTrack(Artist, TrackName);
    }
    
    public Integer getIdsForAlbums(String albumName){
    	return mediaFileDao.getIdsForAlbums(albumName);
    }
    
    public Integer getIdsForAlbums(String artistName, String albumName){
    	return mediaFileDao.getIdsForAlbums(artistName, albumName);
    }
    
    public Integer getIDfromArtistname(String artistName){
    	return mediaFileDao.getIDfromArtistname(artistName);
    }
    
    @Deprecated
    public MediaFile getMediaFile(String artist, String alternate, String title){
    	return mediaFileDao.getMediaFile(artist, alternate, title);
    }
    
    @Deprecated
    public MediaFile getMediaFileTitle(String title){
    	return mediaFileDao.getMediaFileTitle(title);
    }
    
    public List<Artist> getAllArtists(){
    	return artistDao.getAllArtists();
    }
	
    ///////////////////////////////////////////////////
    private MediaFile checkLastModified(MediaFile mediaFile, boolean useFastCache) {
//      if (useFastCache || (mediaFile.getVersion() >= MediaFileDao.VERSION && mediaFile.getChanged().getTime() >= FileUtil.lastModified(mediaFile.getFile()))) {
		if (useFastCache || mediaFile.getChanged().getTime() >= FileUtil.lastModified(mediaFile.getFile())) {
            return mediaFile;
        }
        mediaFile = createMediaFile(mediaFile.getFile());
        mediaFileDao.createOrUpdateMediaFile(mediaFile);
        return  mediaFile;
    }
    ///////////////////////////////////////////////////

    /**
     * Returns a media file instance for the given path name. If possible, a cached value is returned.
     *
     * @param pathName A path name for a file on the local file system.
     * @return A media file instance.
     * @throws SecurityException If access is denied to the given file.
     */
    public MediaFile getMediaFile(String pathName) {
        return getMediaFile(new File(pathName));
    }

    // TODO: Optimize with memory caching.
    public MediaFile getMediaFile(int id, int user_group_id) {
        MediaFile mediaFile = mediaFileDao.getMediaFile(id);
        if (mediaFile == null) {
            return null;
        }

        if (securityService.isAccessAllowed(mediaFile.getFile(), user_group_id) == false ) {
        	return null;
        }else
        {
	        if (!securityService.isReadAllowed(mediaFile.getFile(), user_group_id )) {
	            throw new SecurityException("Access denied to file " + mediaFile);
	        }
        }

        return checkLastModified(mediaFile, settingsService.isFastCacheEnabled());
    }

    public MediaFile getMediaFile(int id) {
        MediaFile mediaFile = mediaFileDao.getMediaFile(id);
        if (mediaFile == null) {
            return null;
        }

        if (!mediaFile.getPath().startsWith("http")) {
            if (!securityService.isReadAllowed(mediaFile.getFile())) {
                throw new SecurityException("Access denied to file " + mediaFile);
            }
        }
        return checkLastModified(mediaFile, settingsService.isFastCacheEnabled());
    }    
    
    public MediaFile getParentOf(MediaFile mediaFile) {
        if (mediaFile.getParentPath() == null) {
            return null;
        }
        return getMediaFile(mediaFile.getParentPath());
    }

    public List<MediaFile> getChildrenOf(String parentPath, boolean includeFiles, boolean includeDirectories, boolean sort) {
        return getChildrenOf(new File(parentPath), includeFiles, includeDirectories, sort);
    }

	public List<MediaFile> getChildrenOf(List<MediaFile> children, boolean includeFiles,
			boolean includeDirectories, boolean sort) {
		
        List<MediaFile> result = new ArrayList<MediaFile>();
        for (MediaFile child : children) {

            if (child.isDirectory() && includeDirectories) {
                result.add(child);
            }
            if (child.isFile() && includeFiles) {
                result.add(child);
            }
        }
		return result;
		
	}    
    
    public List<MediaFile> getChildrenOf(File parent, boolean includeFiles, boolean includeDirectories, boolean sort) {
        return getChildrenOf(getMediaFile(parent), includeFiles, includeDirectories, sort);
    }

    /**
     * Returns all media files that are children of a given media file.
     *
     * @param includeFiles       Whether files should be included in the result.
     * @param includeDirectories Whether directories should be included in the result.
     * @param sort               Whether to sort files in the same directory.
     * @return All children media files.
     */
    public List<MediaFile> getChildrenOf(MediaFile parent, boolean includeFiles, boolean includeDirectories, boolean sort) {
        return getChildrenOf(parent, includeFiles, includeDirectories, sort, settingsService.isFastCacheEnabled());
    }

    
    /**
     * Returns all media files that are children of a given media file.
     *
     * @param includeFiles       Whether files should be included in the result.
     * @param includeDirectories Whether directories should be included in the result.
     * @param sort               Whether to sort files in the same directory.
     * @return All children media files.
     */
    public List<MediaFile> getChildrenOf(MediaFile parent, boolean includeFiles, boolean includeDirectories, boolean sort, boolean useFastCache) {

        if (!parent.isDirectory()) {
            return Collections.emptyList();
        }

        // Make sure children are stored and up-to-date in the database.
        if (parent != null && !useFastCache) {
    		updateChildren(parent);
        }

        List<MediaFile> result = new ArrayList<MediaFile>();
        for (MediaFile child : mediaFileDao.getChildrenOf(parent.getPath())) {
            child = checkLastModified(child, useFastCache);
            if (child.isDirectory() && includeDirectories) {
            	settingsService.getNewaddedTimespan();
            	
            	if (isNewAdded(child, settingsService.getNewaddedTimespan())) {
                	child.setNewAdded(true);
            	}
            	
                result.add(child);
            }
            if (child.isFile() && includeFiles) {
                result.add(child);
            }
        }

        if (sort) {
            Comparator<MediaFile> comparator = new MediaFileComparator(settingsService.isSortAlbumsByFolder(), settingsService.isSortFilesByFilename());
            // Note: Intentionally not using Collections.sort() since it can be problematic on Java 7.
            // http://www.oracle.com/technetwork/java/javase/compatibility-417013.html#jdk7
            Set<MediaFile> set = new TreeSet<MediaFile>(comparator);
            set.addAll(result);
            result = new ArrayList<MediaFile>(set);
        }

        return result;
    }

    public List<MediaFile> getChildrenSorted(List<MediaFile> result, boolean sort) {

    	if (sort) {
            Comparator<MediaFile> comparator = new MediaFileComparator(settingsService.isSortAlbumsByFolder(), settingsService.isSortFilesByFilename());
            Set<MediaFile> set = new TreeSet<MediaFile>(comparator);
            set.addAll(result);
            result = new ArrayList<MediaFile>(set);
        }

        return result;
    }
    
    public List<MediaFile> getTopTracks(String artist, int userGroupId) {
       return mediaFileDao.getTopTracks(artist, userGroupId);
    }
    
    /**
     * Returns whether the given file is the root of a media folder.
     *
     * @see MusicFolder
     */
    public boolean isRoot(MediaFile mediaFile) {
        for (MusicFolder musicFolder : settingsService.getAllMusicFolders(false, true)) {
            if (mediaFile.getPath().equals(musicFolder.getPath().getPath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all genres in the music collection.
     *
     * @return Sorted list of genres.
     */
    public List<String> getGenres(int user_group_id) {
        return mediaFileDao.getGenres(user_group_id);
    }
    
    public List<Genre> getGenres(boolean sortByAlbum) {
        return mediaFileDao.getGenres(sortByAlbum);
    }
    
    public List<String> getGenres() {
        return mediaFileDao.getGenre();
    }    

    public List<Genre> getGenreSongList() {
        return mediaFileDao.getGenreSongList();
    }     
  
    public List<Genre> getGenreArtistList() {
        return mediaFileDao.getGenreArtistList();
    }    
    
    public List<Genre> getGenreAlbumList() {
        return mediaFileDao.getGenreAlbumList();
    }    
    
    
    public List<String> getArtistGenres() {
        return mediaFileDao.getArtistGenres();
    }    

    public int getGenreSongCount() {
        return mediaFileDao.getGenreSongCount();
    }
    
    public int getGenreArtistCount() {
        return mediaFileDao.getGenreArtistCount();
    }   
    
    public int getGenreAlbumCount() {
        return mediaFileDao.getGenreAlbumCount();
    }   
     
    
    public List<String> getArtistGenresforFolder(List<MusicFolder> musicFoldersToUse, int userGroupId) {

    	List<String> tempList = new ArrayList<String>();
    	for (MusicFolder mf : musicFoldersToUse) {
    		tempList.addAll(mediaFileDao.getArtistGenresforFolder(mf.getId(), userGroupId));
    	}

    	// capitalize and filter out duplicates
    	HashSet<String> hs = new HashSet<String>();

    	for (String genre : tempList) {
    		genre = WordUtils.capitalize(genre);
    		hs.add(genre); 
    	}
    	tempList.clear();
    	tempList.addAll(hs);  
    	Collections.sort(tempList);

    	return tempList;
    }    
    
    
//    public List<String> getLowerGenres() {
//        return mediaFileDao.getLowerGenres();
//    }    
  
    
    public List<String> getMoods() {
        return mediaFileDao.getMoods();
    }       

    public List<String> getLowerMoods() {
        return mediaFileDao.getLowerMoods();
    }        
    
    /**
     * Returns the most frequently played albums.
     *
     * @param offset Number of albums to skip.
     * @param count  Maximum number of albums to return.
     * @return The most frequently played albums.
     */
    public List<MediaFile> getMostFrequentlyPlayedAlbums(int offset, int count, int user_group_id) {
        return mediaFileDao.getMostFrequentlyPlayedAlbums(offset, count, user_group_id);
    }

    /**
     * Returns the most recently played albums.
     *
     * @param offset Number of albums to skip.
     * @param count  Maximum number of albums to return.
     * @return The most recently played albums.
     */
    public List<MediaFile> getMostRecentlyPlayedAlbums(int offset, int count, int user_group_id) {
        return mediaFileDao.getMostRecentlyPlayedAlbums(offset, count, user_group_id);
    }

    /**
     * Returns the most recently added albums.
     *
     * @param offset Number of albums to skip.
     * @param count  Maximum number of albums to return.
     * @return The most recently added albums.
     */
    public List<MediaFile> getNewestAlbums(int offset, int count, int user_group_id) {
        return mediaFileDao.getNewestAlbums(offset, count, user_group_id);
    }

    public List<MediaFile> getNewestAlbums(MusicFolder musicFolder, int offset, int count, int user_group_id) {
        return mediaFileDao.getNewestAlbums(musicFolder, offset, count, user_group_id);
    }
    
    /**
     * Returns the most recently starred albums.
     *
     * @param offset   Number of albums to skip.
     * @param count    Maximum number of albums to return.
     * @param username Returns albums starred by this user.
     * @return The most recently starred albums for this user.
     */
    public List<MediaFile> getStarredAlbums(int offset, int count, String username) {
        return mediaFileDao.getStarredAlbums(offset, count, username);
    }

    public List<MediaFile> getArtists(int offset, int count, String username, int user_group_id) {
        return mediaFileDao.getArtists(offset, count, username, user_group_id);
    }    
    
    
    public List<MediaFile> getStarredArtists(int offset, int count, String username) {
        return mediaFileDao.getStarredArtists(offset, count, username);
    }    
    
    /**
     * Returns albums in alphabetial order.
     *
     *
     * @param offset Number of albums to skip.
     * @param count  Maximum number of albums to return.
     * @param byArtist Whether to sort by artist name
     * @return Albums in alphabetical order.
     */
    public List<MediaFile> getAlphabetialAlbums(int offset, int count, boolean byArtist, int user_group_id) {
        return mediaFileDao.getAlphabetialAlbums(offset, count, byArtist, user_group_id);
    }

    /**
     * Returns albums within a year range.
     *
     * @param offset Number of albums to skip.
     * @param count  Maximum number of albums to return.
     * @param fromYear The first year in the range.
     * @param toYear The last year in the range.
     * @return Albums in the year range.
     */
    public List<MediaFile> getAlbumsByYear(int offset, int count, int fromYear, int toYear, int user_group_id ) {
       return mediaFileDao.getAlbumsByYear(offset, count, fromYear, toYear, user_group_id);
    }


    /**
     * Returns albums in a genre.
     *
     * @param offset Number of albums to skip.
     * @param count  Maximum number of albums to return.
     * @param genre The genre name.
     * @return Albums in the genre.
     */
    public List<MediaFile> getAlbumsByGenre(int offset, int count, String genre, int user_group_id) {
        return mediaFileDao.getAlbumsByGenre(offset, count, genre, user_group_id);
    }
    
    public List<MediaFile> getArtistsByGenre(int offset, int count, String genre, int user_group_id) {
        return mediaFileDao.getArtistsByGenre(genre, offset, count, user_group_id);
    }
    
    public Date getMediaFileStarredDate(int id, String username) {
        return mediaFileDao.getMediaFileStarredDate(id, username);
    }

    public void populateStarredDate(List<MediaFile> mediaFiles, String username) {
        for (MediaFile mediaFile : mediaFiles) {
            populateStarredDate(mediaFile, username);
        }
    }

    public void populateStarredDate(MediaFile mediaFile, String username) {
        Date starredDate = mediaFileDao.getMediaFileStarredDate(mediaFile.getId(), username);
        mediaFile.setStarredDate(starredDate);
    }

    private void updateChildren(MediaFile parent) {

        // Check timestamps.
        if (parent.getChildrenLastUpdated().getTime() >= parent.getChanged().getTime()) {
            return;
        }

        List<MediaFile> storedChildren = mediaFileDao.getChildrenOf(parent.getPath());
        Map<String, MediaFile> storedChildrenMap = new HashMap<String, MediaFile>();
        for (MediaFile child : storedChildren) {
            storedChildrenMap.put(child.getPath(), child);
        }

        List<File> children = filterMediaFiles(FileUtil.listFiles(parent.getFile()));
        for (File child : children) {
            if (storedChildrenMap.remove(child.getPath()) == null) {
                // Add children that are not already stored.
                mediaFileDao.createOrUpdateMediaFile(createMediaFile(child));
            }
        }

        // Delete children that no longer exist on disk.
        for (String path : storedChildrenMap.keySet()) {
            mediaFileDao.deleteMediaFile(path);
        }

        // Update timestamp in parent.
        parent.setChildrenLastUpdated(parent.getChanged());
        parent.setPresent(true);
        mediaFileDao.createOrUpdateMediaFile(parent);
    }

    public List<File> filterMediaFiles(File[] candidates) {
        List<File> result = new ArrayList<File>();
        for (File candidate : candidates) {
            String suffix = FilenameUtils.getExtension(candidate.getName()).toLowerCase();
            try {
				if (!isExcluded(candidate) && (FileUtil.isDirectory(candidate) || isAudioFile(suffix) || isVideoFile(suffix))) {
				    result.add(candidate);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
        }
        return result;
    }

    private boolean isAudioFile(String suffix) {
        for (String s : settingsService.getMusicFileTypesAsArray()) {
            if (suffix.equals(s.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean isVideoFile(String suffix) {
        for (String s : settingsService.getVideoFileTypesAsArray()) {
            if (suffix.equals(s.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    

 
    private Set<String> excludes;

    /**
     * Returns whether the given file is excluded, i.e., whether it is listed in 'madsonic_exclude.txt' in
     * the current directory.
     *
     * @param file The child file in question.
     * @return Whether the child file is excluded.
     */	 
	private boolean isExcluded(File file) throws IOException {

        if (file.getName().startsWith(".") || file.getName().startsWith("@eaDir") || file.getName().toLowerCase().equals("thumbs.db") || file.getName().toLowerCase().equals("extrafanart") ) {
            return true;
        }

        File excludeFile = new File(file.getParentFile().getPath(), "madsonic_exclude.txt");
        if (excludeFile.exists()) {
            excludes = new HashSet<String>();
		
    		LOG.debug("## excludeFile found: " + excludeFile);
            String[] lines = StringUtil.readLines(new FileInputStream(excludeFile));
            for (String line : lines) {
                excludes.add(line.toLowerCase());
            }
			return excludes.contains(file.getName().toLowerCase());		
        }
        
    return false;    
    }

    private MediaFile createMediaFile(File file) {

    	MediaFile existingFile = mediaFileDao.getMediaFile(file.getPath());
        MediaFile mediaFile = new MediaFile();
        Date lastModified = new Date(FileUtil.lastModified(file));
        mediaFile.setPath(file.getPath());
        mediaFile.setFolder(securityService.getRootFolderForFile(file));
        mediaFile.setParentPath(file.getParent());
        mediaFile.setChanged(lastModified);
        mediaFile.setLastScanned(new Date());
        mediaFile.setPlayCount(existingFile == null ? 0 : existingFile.getPlayCount());
        mediaFile.setLastPlayed(existingFile == null ? null : existingFile.getLastPlayed());
        mediaFile.setComment(existingFile == null ? null : existingFile.getComment());
        mediaFile.setChildrenLastUpdated(new Date(0));
        mediaFile.setCreated(lastModified);
        
//		mediaFile.setMediaType(ALBUM);
		mediaFile.setMediaType(DIRECTORY);
		
		
        // ######### Read Comment.txt file ####
    	String comment = null;
		comment = checkForCommentFile(file);
		if (checkForCommentFile(file) != null) {
		    mediaFile.setComment(comment);
		}
		
	    Boolean folderParsing = settingsService.isFolderParsingEnabled();
	    Boolean albumSetParsing = settingsService.isAlbumSetParsingEnabled();
	    
        // #### Scan Foldernamer for Year #####
        if (StringUtil.truncatedYear(file.getName()) != null && StringUtil.truncatedYear(file.getName()) > 10){
            mediaFile.setYear(StringUtil.truncatedYear(file.getName()));
        }		

        //TODO:SCAN ALBUMS
    	   try{
	   			if (isRoot(mediaFile)) { mediaFile.setMediaType(DIRECTORY); }
    	   }
    	   catch (Exception x) {
			LOG.error("Failed to get parent", x);
    	   }
    	   
        mediaFile.setPresent(true);
        
		//  ################# Look for cover art. ##################
		try {
			FilenameFilter PictureFilter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith("png"))  	 { return true; }
				else if (lowercaseName.endsWith("jpg"))  { return true; }
				else if (lowercaseName.endsWith("jpeg")) { return true; }
				else if (lowercaseName.endsWith("gif"))  { return true; }
				else if (lowercaseName.endsWith("bmp"))  { return true; }
				else { return false;}
				}};
			File[] Albumchildren = FileUtil.listFiles(file, PictureFilter, true);
			File coverArt = findCoverArt(Albumchildren);
			if (coverArt != null) {
				mediaFile.setCoverArtPath(coverArt.getPath());
			}
		} catch (IOException x) {
			LOG.error("Failed to find cover art for DIRECTORY ", x);
		}
		
		//  ################# MEDIA_TYPE MUSIC #####################
        if (file.isFile()) {

            MetaDataParser parser = metaDataParserFactory.getParser(file);
            if (parser != null) {
                MetaData metaData = parser.getMetaData(file);
                mediaFile.setArtist(metaData.getArtist());
                mediaFile.setAlbumArtist(metaData.getAlbumArtist());
                mediaFile.setAlbumName(metaData.getAlbumName());
                mediaFile.setTitle(metaData.getTitle());
                mediaFile.setDiscNumber(metaData.getDiscNumber());
                mediaFile.setTrackNumber(metaData.getTrackNumber());
                mediaFile.setGenre(metaData.getGenre());
                mediaFile.setMood(metaData.getMood());
                mediaFile.setYear(metaData.getYear());
                mediaFile.setDurationSeconds(metaData.getDurationSeconds());
                mediaFile.setBitRate(metaData.getBitRate());
                mediaFile.setVariableBitRate(metaData.getVariableBitRate());
                mediaFile.setHeight(metaData.getHeight());
                mediaFile.setWidth(metaData.getWidth());
            }
            String format = StringUtils.trimToNull(StringUtils.lowerCase(FilenameUtils.getExtension(mediaFile.getPath())));
            mediaFile.setFormat(format);
            mediaFile.setFileSize(FileUtil.length(file));
            mediaFile.setMediaType(getMediaType(mediaFile));
            
    		
            // #### Scan for YouTube url #####
            if (file.getPath().toLowerCase().endsWith("url")) {
        		mediaFile.setMediaType(URL);
        		
              	 UrlFile urlFile = null;
              	 String YoutubeId = null;
              	 
				try {
					urlFile = new UrlFile(file.getPath());
					YoutubeId = StringUtil.getYoutubeVideoId(urlFile.getString("InternetShortcut", "URL", null));
					
				} catch (IOException e) {
				}   
        		mediaFile.setData(YoutubeId);
        		mediaFile.setDurationSeconds(0);
            }

        } else {
            // ############## Is this an album? ########################
        	if (!isRoot(mediaFile)) {
                File[] children = FileUtil.listFiles(file);
                File firstChild = null;
                for (File child : filterMediaFiles(children)) {
                    if (FileUtil.isFile(child)) {
                        firstChild = child;
                        break;
                    }
                }

                if (firstChild != null) {
                    mediaFile.setMediaType(ALBUM);
                    
                	Integer FOLDER_TYPE = musicFolderDao.getMusicFolder(mediaFile.getFolder()).getType();
                	switch (FOLDER_TYPE) {
                	case 4: mediaFile.setMediaType(ALBUM); break;
                	case 6: mediaFile.setMediaType(VIDEOSET); break;
                	case 7: mediaFile.setMediaType(VIDEOSET); break;
                	default : break;
            		}


                    	// ######### Read Comment.txt file ####
						comment = checkForCommentFile(file);
						if (checkForCommentFile(file) != null && mediaFile.getComment() == null) {
						    mediaFile.setComment(comment);
						}
                    
                    // ######### Guess artist/album name and year. #######
                    MetaDataParser parser = metaDataParserFactory.getParser(firstChild);
                    if (parser != null) {
                        MetaData metaData = parser.getMetaData(firstChild);
                        
//                      mediaFile.setArtist(metaData.getArtist());
                        mediaFile.setArtist(StringUtils.isBlank(metaData.getAlbumArtist()) ? metaData.getArtist() : metaData.getAlbumArtist());
                        
                        // ########## SET Genre & Yeas for album #########
                        mediaFile.setGenre(metaData.getGenre());
                        if (metaData.getYear() != null)
                        {	mediaFile.setYear(metaData.getYear());                        	
							mediaFile.setYear(metaData.getYear());
                        }

                        
						// ########## BETTER Albumset Detection #########	

                       	String _albumName;
                    	String _albumSetName;
                        
                        if (albumSetParsing) {
                        	
						String AlbumSetName = parser.guessAlbum(firstChild, mediaFile.getArtist());
                        if (folderParsing) { 
                        	AlbumSetName = StringUtil.truncateYear(AlbumSetName); 
                        	}
                        
						String parentAlbumSetName = parser.guessArtist(firstChild);
						
			//			mediaFile.setArtist(parser.guessArtist(mediaFile.getFile()));
                        String searchAlbum = searchforAlbumSetName(AlbumSetName, AlbumSetName);
                        
                        if ( AlbumSetName == searchAlbum ) {

                        	_albumName = metaData.getAlbumName();
                        	_albumSetName = AlbumSetName;
                        	
                            if (folderParsing) { 
                            	_albumName = StringUtil.truncateYear(_albumName); 
                            	_albumSetName = StringUtil.truncateYear(_albumSetName); 
                            }

                            mediaFile.setAlbumName(_albumName);
                            mediaFile.setAlbumSetName(_albumSetName);
                        }
                        else {
                        	
                        	_albumName = metaData.getAlbumName();
                        	_albumSetName = metaData.getAlbumName();
                        	
                            if (folderParsing) { 
                            	_albumName = StringUtil.truncateYear(_albumName); 
                            	_albumSetName = searchforAlbumSetName(StringUtil.truncateYear(AlbumSetName), StringUtil.truncateYear(parentAlbumSetName));
                            }
                            
                            else {
                            	_albumSetName = searchforAlbumSetName(AlbumSetName, parentAlbumSetName);
                            }
                        }
                        
                        } else {
                        	
                        	_albumName = metaData.getAlbumName();
                        	_albumSetName = metaData.getAlbumName();
                        }
                        	
                        	mediaFile.setAlbumName(_albumName);
                            mediaFile.setAlbumSetName(_albumSetName);
							LOG.debug("## MediaType ALBUMSET: " + _albumSetName);
                        }

                    
                    // ####### Look for cover art. ########
                    try {
                        File coverArt = findCoverArt(children);
                        if (coverArt != null) {
                            mediaFile.setCoverArtPath(coverArt.getPath());
                        }
                    } catch (IOException x) {
                        LOG.error("Failed to find cover art.", x);
                    }

                } else {


					// ##### Look for child type  #######
					File firstAudioChild = null;
					File firstFolderChild = null;
					
                    for (File child : filterMediaFiles(children)) {
					
                        if (FileUtil.isDirectory(child)) {
                            firstFolderChild = child;
                        }
                        if (FileUtil.isFile(child)) {
                            firstAudioChild = child;
                            break;
                        }
                    }

                    if (firstFolderChild != null) {
                        File[] firstAlbumChild = FileUtil.listFiles(firstFolderChild);
                        for (File child : filterMediaFiles(firstAlbumChild)) {
                            if (FileUtil.isFile(child)) {
	    	                    MetaDataParser ChildParser = metaDataParserFactory.getParser(child);
	    	                    if (ChildParser != null) {
	    	                        MetaData metaDataChild = ChildParser.getMetaData(child);
	    	                        mediaFile.setGenre(metaDataChild.getGenre());
	    	                        if (metaDataChild.getYear() != null)
	    	                        {	mediaFile.setYear(metaDataChild.getYear()); }
	    	                    }
                                break;
                            }
                            else if (FileUtil.isDirectory(child)) {
                                for (File subchild : filterMediaFiles(FileUtil.listFiles(child))) {
                                    if (FileUtil.isFile(subchild)) {
        	    	                    MetaDataParser ChildParser = metaDataParserFactory.getParser(subchild);
        	    	                    if (ChildParser != null) {
        	    	                        MetaData metaDataChild = ChildParser.getMetaData(subchild);
        	    	                        
        	    	                        if (metaDataChild.getGenre() != null)

        	    	                        {	
    	    	                        	if (mediaFile.getGenre() != metaDataChild.getGenre())
        	    	                        	mediaFile.setGenre(metaDataChild.getGenre()); 
        	    	                        }
        	    	                        
        	    	                        if (metaDataChild.getYear() != null)
        	    	                        {	mediaFile.setYear(metaDataChild.getYear()); }
        	    	                    }
                                        break;
                                    }
                            	
                                }
                            }
                            
                        }
					}
                    if (firstAudioChild != null) {
	                    MetaDataParser ChildParser = metaDataParserFactory.getParser(firstAudioChild);
	                    if (ChildParser != null) {
	                        MetaData metaDataChild = ChildParser.getMetaData(firstAudioChild);
							mediaFile.setGenre(metaDataChild.getGenre());
	                        if (metaDataChild.getYear() != null)
	                        {	mediaFile.setYear(metaDataChild.getYear()); }
	                    }
                    }
                    
				// ######## ALBUMSET ###############
                  if (firstAudioChild == null) {
              		mediaFile.setMediaType(ALBUMSET);
              		
                	Integer FOLDER_TYPE = musicFolderDao.getMusicFolder(mediaFile.getFolder()).getType();
                	switch (FOLDER_TYPE) {
                	case 4: mediaFile.setMediaType(ALBUM); break;
                	case 6: mediaFile.setMediaType(VIDEOSET); break;
                	case 7: mediaFile.setMediaType(VIDEOSET); break;
                	default : break;
            		}
              		
              		String _artist = new File(file.getParent()).getName();
              		String _albumName = file.getName();
              		String _albumSetName = file.getName();
              		
              	    if (folderParsing) {
              	    	_artist = StringUtil.truncateYear(_artist);
              	    	_albumName = StringUtil.truncateYear(_albumName);
              	    	_albumSetName = StringUtil.truncateYear(_albumSetName);
              	    }
              		
              		
  			        mediaFile.setArtist(_artist);
                    mediaFile.setAlbumName(_albumName);
                    mediaFile.setAlbumSetName(_albumSetName);
//  				LOG.info("## MediaType ALBUMSET: " + _albumSetName));
                  }     
				  else
				  {              		
				    String _artist = file.getName();
              	    if (folderParsing) {
              	    	_artist = StringUtil.truncateYear(_artist);
              	    }
                    mediaFile.setArtist(_artist);
				  }
                  
                  if (mediaFile.getArtist() != null) {
                  if (mediaFile.getParentPath().contains(mediaFile.getArtist()))
                  {
                		mediaFile.setMediaType(ALBUMSET);
    				    String _artist = new File(file.getParent()).getName();
                  	    if (folderParsing) {
                  	    	_artist = StringUtil.truncateYear(_artist);
                  	    }
                  		mediaFile.setArtist(_artist);
//						LOG.info("## MediaType ALBUMSET: " + _artist );
                  }


				// ######## ARTIST ###############
	              if (mediaFile.getCoverArtPath() == null) {
	                		mediaFile.setMediaType(ARTIST);
	    				    String _artist = file.getName();
	                  	    if (folderParsing) {
	                  	    	_artist = StringUtil.truncateYear(_artist);
	                  	    }
	                  		mediaFile.setArtist(_artist);
							LOG.debug("## MediaType ARTIST: " + _artist );
	              }  
                }

				// ######## ARTIST ###############
                  if (mediaFile.getCoverArtPath() != null) {
                  	String lowercaseName = (mediaFile.getCoverArtPath().toLowerCase());
                  	if (lowercaseName.contains("artist.")) {
                		mediaFile.setMediaType(ARTIST);
    				    String _artist = file.getName();
                  	    if (folderParsing) {
                  	    	_artist = StringUtil.truncateYear(_artist);
                  	    }
                  		mediaFile.setArtist(_artist);
						LOG.debug("## MediaType ARTIST: " + _artist );
                  	}
                  }                	

  				// ######## VIDEOSET ###############
                  if (mediaFile.getCoverArtPath() != null) {
                  	String lowercaseName = (mediaFile.getCoverArtPath().toLowerCase());
                  	if (lowercaseName.contains("video.") || lowercaseName.contains("sesson") ) {
                		mediaFile.setMediaType(VIDEOSET);
						LOG.debug("## MediaType VIDEOSET: " + file.getName() );
                  	}
                  }                    
                  
              	// ################## Look for Artist flag ###############
				setMediaTypeFlag (mediaFile, file, "MULTI.TAG",  MULTIARTIST);
				setMediaTypeFlag (mediaFile, file, "ARTIST.TAG", ARTIST); 
				setMediaTypeFlag (mediaFile, file, "DIR.TAG",    DIRECTORY);
				setMediaTypeFlag (mediaFile, file, "SET.TAG",    ALBUMSET);
				setMediaTypeFlag (mediaFile, file, "ALBUM.TAG",  ALBUM); 
				setMediaTypeFlag (mediaFile, file, "VIDEO.TAG",  VIDEOSET);
                }
            }
        }
        return mediaFile;
    }

    
    private boolean isNewAdded(MediaFile mediafile, String TimeSpan){
    	
        try {  
    	
    	Date LastSystemScan = null;
    	Integer timespan = 0;

    	if (TimeSpan.contains("OneHour"))    { timespan = 1;}; 
    	if (TimeSpan.contains("OneDay"))     { timespan = 2;}; 
    	if (TimeSpan.contains("OneWeek"))    { timespan = 3;}; 
    	if (TimeSpan.contains("OneMonth"))   { timespan = 4;}; 
    	if (TimeSpan.contains("TwoMonth"))   { timespan = 5;}; 
    	if (TimeSpan.contains("ThreeMonth")) { timespan = 6;}; 
    	if (TimeSpan.contains("SixMonth"))   { timespan = 7;}; 
    	if (TimeSpan.contains("OneYear"))    { timespan = 8;}; 
    	
    	switch(timespan)
        {
        case 1:
    		LastSystemScan = new Date(System.currentTimeMillis() - 3600 * 1000); break;
        case 2:
    		LastSystemScan = new Date(System.currentTimeMillis() -   24 * 3600 * 1000); break;
        case 3:
    		LastSystemScan = new Date(System.currentTimeMillis() -   7L * 24 * 3600 * 1000); break;
        case 4:
    		LastSystemScan = new Date(System.currentTimeMillis() -  30L * 24 * 3600 * 1000); break;
        case 5:
    		LastSystemScan = new Date(System.currentTimeMillis() -  60L * 24 * 3600 * 1000); break;
        case 6:
    		LastSystemScan = new Date(System.currentTimeMillis() -  90L * 24 * 3600 * 1000); break;
        case 7:
    		LastSystemScan = new Date(System.currentTimeMillis() - 182L * 24 * 3600 * 1000); break;
        case 8:
    		LastSystemScan = new Date(System.currentTimeMillis() - 365L * 24 * 3600 * 1000); break;
        default:
        	 
        }
		
//		Date LastSystemScan = ThreeMonth;
		Date LastMediaScan = mediafile.getCreated();

    	Calendar calLastSystemScan = Calendar.getInstance();
    	Calendar calLastMediaScan = Calendar.getInstance();
    	
    	calLastMediaScan.setTime(LastMediaScan);		
    	calLastSystemScan.setTime(LastSystemScan);
    	
    	if(calLastSystemScan.before(calLastMediaScan)) {
			return true;
    	}
    	return false;

//		System.out.println("Current date(" + new SimpleDateFormat("yyyy.MM.dd hh:mm").format(calLastSystemScan.getTime()) + ")");
//		System.out.println("Scandat date(" + new SimpleDateFormat("yyyy.MM.dd hh:mm").format(calLastMediaScan.getTime()) + ")");
//		
//	    long milliseconds1 = calLastSystemScan.getTimeInMillis();
//		long milliseconds2 = calLastMediaScan.getTimeInMillis();
//		long diff = milliseconds2 - milliseconds1;
//		
//		long diffDays = diff / (24 * 60 * 60 * 1000);
//		long diffHours = diff / (60 * 60 * 1000);
//		
//		System.out.println("Time in days: " + diffDays  + " days.");
//		System.out.println("Time in hours: " + diffHours  + " hours.");
		
    	
        } catch (Exception x) {
            LOG.error("Failed to get TimeSpan.", x);
        }
		return false;    	
        	    	
    	
	    }
    
    
    /**
     * Set MediaType if File TAG Flag is found
     */
	private boolean setMediaTypeFlag(MediaFile mediaFile, File file, final String flagname, MediaType mediaType){
						
	FilenameFilter FlagFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			String lowercaseName = name.toLowerCase();
			if (lowercaseName.contains(flagname.toLowerCase())) {
				return true;  }
		 else { return false; }
		}};
	File[] FlagChildren = FileUtil.listFiles(file, FlagFilter, true);
    for (File candidate : FlagChildren) {
		if (candidate.isFile()) {
            mediaFile.setMediaType(mediaType);
			LOG.debug("## Found FileTag " + mediaType + ": " + file.getName() );
			return true;
		}}
		return false;
	}
    
    /**
     * Returns a converted Albumsetname for the given parent folder.
     */
    private String searchforAlbumSetName(String albumname, String parentalbumname ) {
	    int i = 1;
		boolean AlbumSetFound;
	    while(i<16) {
		    String[] strArray = {"CD"+i,"CD "+i,
								 "DISC"+i,"DISC "+i,
								 "DISK"+i,"DISK "+i,
								 "TEIL"+i,"TEIL "+i,
								 "PART"+i,"PART "+i};
		    AlbumSetFound = false;

			for (String searchTerm : strArray) {
		    	if (albumname.toLowerCase().contains(searchTerm.toLowerCase())) { 
		    		AlbumSetFound = true; } 
			}
			if (AlbumSetFound == true) {
				parentalbumname = parentalbumname + " - Disk"+i;
	            }
	    i++;
	    }
		return parentalbumname;   
    }
      
 private String checkForCommentFile(File file) {

        File commentFile = new File(file.getPath(), "comment.txt");
        if (commentFile.exists()) {
    		LOG.info("## CommentFile found: " + commentFile);
    	    Path path = Paths.get(commentFile.getPath());
    	    List<String> lines = null;
    	    String listString = "";

				try {
					lines = Files.readAllLines(path, StandardCharsets.UTF_8);
				} catch (IOException e) {
				LOG.warn("## error reading commentfile: " + commentFile);
//				e.printStackTrace();
				}
				
				if (lines == null){
					try {
						lines = Files.readAllLines(path, StandardCharsets.ISO_8859_1);
					} catch (IOException e) {
						LOG.warn("## error reading commentfile: " + commentFile);
//						e.printStackTrace();
					}
				}
				
	    	    for (String s : lines)
	    	    { 
	    	    	s = s.replace("’", "'");
	    	    	listString += s + " \\\\"; }
	    	    
		return listString;
    	  }
		return null;    	
    }
    
    private MediaFile.MediaType getMediaType(MediaFile mediaFile) {
        String path = mediaFile.getPath().toLowerCase();
        if (path.toLowerCase().endsWith("url")) {
            return VIDEO;
        }
        if (isVideoFile(mediaFile.getFormat())) {
            return VIDEO;
        }        
        String genre = StringUtils.trimToEmpty(mediaFile.getGenre()).toLowerCase();
        if (path.contains("podcast") || genre.contains("podcast")) {
            return PODCAST;
        }
        if (path.contains("audiobook") || genre.contains("audiobook") || path.contains("audio book") || genre.contains("audio book")) {
            return AUDIOBOOK;
        }
        return MUSIC;
    }
    
    @Deprecated // refreshMediaFileRank ->  refreshMediaFile
    public void refreshMediaFileRank(MediaFile mediaFile) {
        mediaFileDao.createOrUpdateMediaFile(mediaFile);
        mediaFileMemoryCache.remove(mediaFile.getFile());
        mediaFileMemoryCache.put(new Element(mediaFile.getFile(), mediaFile ));
    }
    
    public void refreshMediaFile(MediaFile mediaFile) {
    	MediaFile newMediaFile = new MediaFile(); 
        newMediaFile = createMediaFile(mediaFile.getFile());
        newMediaFile.setRank(mediaFile.getRank());
        mediaFileDao.createOrUpdateMediaFile(newMediaFile);
        mediaFileMemoryCache.remove(newMediaFile.getFile());
    }

    private void putInMemoryCache(File file, MediaFile mediaFile) {
        if (memoryCacheEnabled) {
            mediaFileMemoryCache.put(new Element(file, mediaFile));
        }
    }

    private MediaFile getFromMemoryCache(File file) {
        if (!memoryCacheEnabled) {
            return null;
        }
        Element element = mediaFileMemoryCache.get(file);
        return element == null ? null : (MediaFile) element.getObjectValue();
    }

    public void setMemoryCacheEnabled(boolean memoryCacheEnabled) {
        this.memoryCacheEnabled = memoryCacheEnabled;
        if (!memoryCacheEnabled) {
            mediaFileMemoryCache.removeAll();
        }
    }

    /**
     * Returns a cover art image for the given media file.
     */
    public File getCoverArt(MediaFile mediaFile) {
        if (mediaFile.getCoverArtFile() != null) {
            return mediaFile.getCoverArtFile();
        }
        MediaFile parent = getParentOf(mediaFile);
        return parent == null ? null : parent.getCoverArtFile();
    }

    /**
     * Finds a cover art image for the given directory, by looking for it on the disk.
     */
    private File findCoverArt(File[] candidates) throws IOException {
        for (String mask : settingsService.getCoverArtFileTypesAsArray()) {
            for (File candidate : candidates) {
                if (candidate.isFile() && candidate.getName().toUpperCase().endsWith(mask.toUpperCase()) && !candidate.getName().startsWith(".")) {
                    return candidate;
                }
            }
        }

        // Look for embedded images in audiofiles. (Only check first audio file encountered).
        JaudiotaggerParser parser = new JaudiotaggerParser();
        for (File candidate : candidates) {
            if (parser.isApplicable(candidate)) {
                if (parser.isImageAvailable(getMediaFile(candidate))) {
                    return candidate;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setMediaFileMemoryCache(Ehcache mediaFileMemoryCache) {
        this.mediaFileMemoryCache = mediaFileMemoryCache;
    }

    public void setMediaFileDao(MediaFileDao mediaFileDao) {
        this.mediaFileDao = mediaFileDao;
    }

    public void setMusicFolderDao(MusicFolderDao musicFolderDao) {
        this.musicFolderDao = musicFolderDao;
    }
    
    
    /**
     * Returns all media files that are children, grand-children etc of a given media file.
     * Directories are not included in the result.
     *
     * @param sort Whether to sort files in the same directory.
     * @return All descendant music files.
     */
    public List<MediaFile> getDescendantsOf(MediaFile ancestor, boolean sort) {

        if (ancestor.isFile()) {
            return Arrays.asList(ancestor);
        }

        List<MediaFile> result = new ArrayList<MediaFile>();

        for (MediaFile child : getChildrenOf(ancestor, true, true, sort)) {
            if (child.isDirectory()) {
                result.addAll(getDescendantsOf(child, sort));
            } else {
                result.add(child);
            }
        }
        return result;
    }

    public void setMetaDataParserFactory(MetaDataParserFactory metaDataParserFactory) {
        this.metaDataParserFactory = metaDataParserFactory;
    }

    public void updateMediaFile(MediaFile mediaFile) {
        mediaFileDao.createOrUpdateMediaFile(mediaFile);
    }

    /**
     * Increments the play count and last played date for the given media file 
     */
    public void updateStatisticsUser(String username, MediaFile mediaFile) {
    	mediaFileDao.setPlayCountForUser(username, mediaFile);
    }
    
    /**
     * Increments the play count and last played date for the given media file and its
     * directory and album.
     */
    public void incrementPlayCount(MediaFile file) {
        Date now = new Date();
        file.setLastPlayed(now);
        file.setPlayCount(file.getPlayCount() + 1);
        updateMediaFile(file);

        MediaFile parent = getParentOf(file);
        if (!isRoot(parent)) {
            parent.setLastPlayed(now);
            parent.setPlayCount(parent.getPlayCount() + 1);
            updateMediaFile(parent);
        }

        Album album = albumDao.getAlbum(file.getAlbumArtist(), parent.getAlbumSetName());
        
        if (album != null) {
            album.setLastPlayed(now);
            album.setPlayCount(album.getPlayCount() + 1);
            albumDao.createOrUpdateAlbum(album);
        }
    }
	
	
    public void clearMemoryCache() {
        mediaFileMemoryCache.removeAll();
    }
	
	
    public List<Album> getAlbumsForArtist(String artist) {
    	return albumDao.getAlbumsForArtist(artist);
    }
    
    public List<Album> getAllAlbums() {
    	return albumDao.getAllAlbums();
    }
    
    public void setAlbumDao(AlbumDao albumDao) {
        this.albumDao = albumDao;
    }
    
    public void setArtistDao(ArtistDao artistDao) {
        this.artistDao = artistDao;
    }

	public List<String> getArtistFolder(String sortableName) {
		return artistDao.getArtistsForFolder(sortableName);
	}

}
