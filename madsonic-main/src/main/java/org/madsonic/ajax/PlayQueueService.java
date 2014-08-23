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
package org.madsonic.ajax;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.madsonic.controller.EditTagsController.Song;
import org.madsonic.dao.MediaFileDao;
import org.madsonic.domain.Playlist;
import org.madsonic.service.*;
import org.madsonic.service.PlaylistService;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.TermQuery;
import org.directwebremoting.WebContextFactory;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.madsonic.domain.MediaFile;
import org.madsonic.domain.MediaFile.MediaType;
import org.madsonic.domain.LastFMArtistTopTrack;
import org.madsonic.domain.MoodsSearchCriteria;
import org.madsonic.domain.MultiSearchCriteria;
import org.madsonic.domain.Player;
import org.madsonic.domain.PlayQueue;
import org.madsonic.domain.GenreSearchCriteria;
import org.madsonic.domain.RandomSearchCriteria;
import org.madsonic.util.StringUtil;

/**
 * Provides AJAX-enabled services for manipulating the play queue of a player.
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class PlayQueueService {

    private PlayerService playerService;
    private JukeboxService jukeboxService;
    private TranscodingService transcodingService;
    private SettingsService settingsService;
    private MediaFileService mediaFileService;
    private SecurityService securityService;
    private MediaFileDao mediaFileDao;
    private SearchService searchService;
    private LastFMService lastFMService;   
    
    private org.madsonic.service.PlaylistService playlistService;

    /**
     * Returns the play queue for the player of the current user.
     *
     * @return The play queue.
     */
    public PlayQueueInfo getPlayQueue() throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        Player player = getCurrentPlayer(request, response);
        return convert(request, player, false);
    }

    public PlayQueueInfo start() throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        return doStart(request, response);
    }

    public PlayQueueInfo doStart(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Player player = getCurrentPlayer(request, response);
        player.getPlayQueue().setStatus(PlayQueue.Status.PLAYING);
        return convert(request, player, true);
    }

    public PlayQueueInfo stop() throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        return doStop(request, response);
    }

    public PlayQueueInfo doStop(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Player player = getCurrentPlayer(request, response);
        player.getPlayQueue().setStatus(PlayQueue.Status.STOPPED);
        return convert(request, player, true);
    }

    public PlayQueueInfo skip(int index) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        return doSkip(request, response, index, 0);
    }

    public PlayQueueInfo doSkip(HttpServletRequest request, HttpServletResponse response, int index, int offset) throws Exception {
        Player player = getCurrentPlayer(request, response);
        player.getPlayQueue().setIndex(index);
        boolean serverSidePlaylist = !player.isExternalWithPlaylist();
        return convert(request, player, serverSidePlaylist, offset);
    }

	
    public PlayQueueInfo play(int id) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();

        Player player = getCurrentPlayer(request, response);
        MediaFile file = mediaFileService.getMediaFile(id);
        List<MediaFile> files = mediaFileService.getDescendantsOf(file, true);
        return doPlay(request, player, files);
    }

    public PlayQueueInfo playMore(int id) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();

        Player player = getCurrentPlayer(request, response);
        MediaFile file = mediaFileService.getMediaFile(id);

        if (file.isFile()) {
            MediaFile dir = mediaFileService.getParentOf(file);
            List<MediaFile> songs = mediaFileService.getChildrenOf(dir, true, false, true);
            if (!songs.isEmpty()) {
                int index = songs.indexOf(file);
                songs = songs.subList(index, songs.size());
            }
            return doPlay(request, player, songs).setStartPlayerAt(0);
        } else {
            List<MediaFile> songs = mediaFileService.getDescendantsOf(file, true);
            return doPlay(request, player, songs).setStartPlayerAt(0);
        }
    }

	
    ////////////////////
    public PlayQueueInfo AddPlaylist(int id) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();

        List<MediaFile> playlistFiles = playlistService.getFilesInPlaylist(id);
        int[] files = new int[playlistFiles.size()];
        for (int i=0; i < files.length; i++)
        {
            files[i] = playlistFiles.get(i).getId();
        }
//      Player player = getCurrentPlayer(request, response);
        return doAdd(request, response, files, null);
    }
	////////////////////
	
    public PlayQueueInfo playPlaylist(int id, boolean random) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();

        List<MediaFile> files = playlistService.getFilesInPlaylist(id);
        Player player = getCurrentPlayer(request, response);
        if (player.isWeb()) {
            removeVideoFiles(files);
        }
        
        player.getPlayQueue().addFiles(false, files);
        player.getPlayQueue().setRandomSearchCriteria(null);
        if (random){
        player.getPlayQueue().shuffle();
        }
        return convert(request, player, true);
   //   return doPlay(request, player, files).setStartPlayerAt(0);
    }

    private PlayQueueInfo doPlay(HttpServletRequest request, Player player, List<MediaFile> files) throws Exception {
        if (player.isWeb()) {
            removeVideoFiles(files);
        }
        player.getPlayQueue().addFiles(false, files);
        player.getPlayQueue().setRandomSearchCriteria(null);
        return convert(request, player, true);
    }
    
    public PlayQueueInfo playPandoraRadio(String[] artist, String[] albums, String[] genres, String[] tags) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        int userGroupId = securityService.getCurrentUserGroupId(request);  
        Player player = getCurrentPlayer(request, response);
        List<MediaFile> result = new ArrayList<MediaFile>();
		
		//RESULT: ALBUM
		MultiSearchCriteria criteria1 = new MultiSearchCriteria (settingsService.getPandoraResultAlbum(), artist, albums, null, null, null, null, null, userGroupId);
		result.addAll(searchService.getRandomSongs(criteria1));
		if ("TEST".contains(settingsService.getLogfileLevel())){ System.out.println("-=Pandora-R1=-");
			for (MediaFile m : searchService.getRandomSongs(criteria1)) {System.out.println( m.getPath());}
		}
		//RESULT: ARTIST
		MultiSearchCriteria criteria2 = new MultiSearchCriteria (settingsService.getPandoraResultArtist(), artist, null, null, null, null, null, null, userGroupId);
		result.addAll(searchService.getRandomSongs(criteria2));
		if ("TEST".contains(settingsService.getLogfileLevel()))         {System.out.println("-=Pandora-R2=-");
			for (MediaFile m : searchService.getRandomSongs(criteria2)) {System.out.println( m.getPath());}
		}
		//RESULT: GENRE
		GenreSearchCriteria criteria3 = new GenreSearchCriteria (settingsService.getPandoraResultGenre(), null, genres, null, null, null, userGroupId);
		result.addAll(searchService.getRandomSongs(criteria3));
		if ("TEST".contains(settingsService.getLogfileLevel()))         { System.out.println("-=Pandora-R3=-");
			for (MediaFile m : searchService.getRandomSongs(criteria3)) {System.out.println( m.getPath());}
		}
		//RESULT: MOODS
		MoodsSearchCriteria criteria4 = new MoodsSearchCriteria (settingsService.getPandoraResultMood(), null, tags, null, null, null, userGroupId);
		result.addAll(searchService.getRandomSongs(criteria4));
		if ("TEST".contains(settingsService.getLogfileLevel()))         {System.out.println("-=Pandora-R4=-");
			for (MediaFile m : searchService.getRandomSongs(criteria4)) {System.out.println( m.getPath());}
		}
		
		//RESULT: OTHER
        List<String> allArtists = new ArrayList<String>();
        List<String> similar = new ArrayList<String>();
        
		for (String _artist : artist) {
	        similar = lastFMService.getSimilarArtist(_artist) ;
	        allArtists.addAll(similar);
	        if (allArtists.size() > 10) {
	        	break;
	        }
		} 
        String[] array = allArtists.toArray(new String[allArtists.size()]);
		
		//RESULT: RANDOM
		MultiSearchCriteria criteria5 = new MultiSearchCriteria (settingsService.getPandoraResultSimilar(), array, null, null, null, null, null, null, userGroupId);
		result.addAll(searchService.getRandomSongs(criteria5));
		if ("TEST".contains(settingsService.getLogfileLevel())){
			System.out.println("-=Pandora-R5=-");
			for (MediaFile m : searchService.getRandomSongs(criteria5)) {System.out.println( m.getPath());}
		}

		//RESULT: ARTIST TOPTRACKS
		List<MediaFile> resultTopTrack = new ArrayList<MediaFile>();
		
		if ( mediaFileDao.getTopTracks(artist[0], userGroupId).size() == 0) {
			resultTopTrack = lastFMService.getTopTrack(artist[0], 20, userGroupId);
		}
		
		if (artist.length > 1) {
			for (String _artist : artist) {
//				resultTopTrack = lastFMService.getTopTrack(_artist, 20, userGroupId);
				resultTopTrack = mediaFileDao.getTopTracks(artist[0], userGroupId);
				for(int i = 0; i < settingsService.getPandoraResultArtistTopTrack(); i++) {
					Random myRandomizer = new Random();
					MediaFile randomTopTrack = resultTopTrack.get(myRandomizer.nextInt(resultTopTrack.size()));
					result.add(randomTopTrack);
				}
			}			
		}

		if (artist.length == 1) {		
//			resultTopTrack = lastFMService.getTopTrack(artist[0], settingsService.getPandoraResultArtistTopTrack(), userGroupId);
			resultTopTrack = mediaFileDao.getTopTracks(artist[0], userGroupId);
			
			for(int i = 0; i < settingsService.getPandoraResultArtistTopTrack(); i++) {
				Random myRandomizer = new Random();
				MediaFile randomTopTrack = resultTopTrack.get(myRandomizer.nextInt(resultTopTrack.size()));
				result.add(randomTopTrack);
			}
		}
		

		
//		MoodsSearchCriteria criteria6 = new MoodsSearchCriteria (settingsService.getPandoraResultSimilar(), null, tags, null, null, null, userGroupId);
//		result.addAll(searchService.getRandomSongs(criteria6));
//		if ("TEST".contains(settingsService.getLogfileLevel())){
//			System.out.println("-=Pandora-R6=-");
//			for (MediaFile m : searchService.getRandomSongs(criteria6)) {System.out.println( m.getPath());}
//		}

        // Filter out duplicates
		HashSet<MediaFile> hs = new HashSet<MediaFile>();
		hs.addAll(result);
		result.clear();
		result.addAll(hs);
		
		player.getPlayQueue().addFiles(false, result);
		player.getPlayQueue().setRandomSearchCriteria(null);
        player.getPlayQueue().shuffle();
        
        return convert(request, player, true);
    }

    public PlayQueueInfo playGenreRadio(String[] tags, int count, String year) throws Exception {
    	
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        
        int userGroupId = securityService.getCurrentUserGroupId(request);  
        
        Player player = getCurrentPlayer(request, response);
		if (count > 501 || count == 0) { count = 10; }
		
        Integer fromYear = null;
        Integer toYear = null;

        if (!StringUtils.equalsIgnoreCase("any", year)) {
            String[] tmp = StringUtils.split(year);
            fromYear = Integer.parseInt(tmp[0]);
            toYear = Integer.parseInt(tmp[1]);
        }
        		
		GenreSearchCriteria criteria = new GenreSearchCriteria (count, null, tags, fromYear, toYear, null, userGroupId);
		player.getPlayQueue().addFiles(false, searchService.getRandomSongs(criteria));
        player.getPlayQueue().setRandomSearchCriteria(null);
        return convert(request, player, true);
    }
    
    public PlayQueueInfo playMoodRadio(String[] tags, int count, String year) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        
        int userGroupId = securityService.getCurrentUserGroupId(request);  
        
        Player player = getCurrentPlayer(request, response);
		if (count > 501 || count == 0) { count = 20; }
		
        Integer fromYear = null;
        Integer toYear = null;

        if (!StringUtils.equalsIgnoreCase("any", year)) {
            String[] tmp = StringUtils.split(year);
            fromYear = Integer.parseInt(tmp[0]);
            toYear = Integer.parseInt(tmp[1]);
        }
		MoodsSearchCriteria criteria = new MoodsSearchCriteria (count, null, tags, fromYear, toYear, null, userGroupId);
		player.getPlayQueue().addFiles(false, searchService.getRandomSongs(criteria));
        player.getPlayQueue().setRandomSearchCriteria(null);
        return convert(request, player, true);
    }
    
    public PlayQueueInfo playRandomGenre(int id, int count) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();

        MediaFile file = mediaFileService.getMediaFile(id);
        List<MediaFile> randomFiles = getRandomChildren(file, count);
        Player player = getCurrentPlayer(request, response);
        player.getPlayQueue().addFiles(false, randomFiles);
        player.getPlayQueue().setRandomSearchCriteria(null);
        return convert(request, player, true);
    }

    public PlayQueueInfo playTopTrack(String artist) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();

        int userGroupId = securityService.getCurrentUserGroupId(request); 
	    List<MediaFile> topTrackSongs = new ArrayList<MediaFile>();
	    
	    topTrackSongs = mediaFileService.getTopTracks(artist, userGroupId);
	    if (topTrackSongs.size() < 1) {
	    	lastFMService.updateTopTrackArtist(artist);
        	lastFMService.updateTopTrackEntries(artist, settingsService.getLastFMResultSize(), userGroupId); 
		    topTrackSongs = mediaFileService.getTopTracks(artist, userGroupId);
	    }
	    
        Player player = getCurrentPlayer(request, response);
        player.getPlayQueue().addFiles(false, topTrackSongs);
        player.getPlayQueue().setRandomSearchCriteria(null);
        return convert(request, player, true);
    }

    public PlayQueueInfo addTopTrack(String artist) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();

        int userGroupId = securityService.getCurrentUserGroupId(request); 
	    List<MediaFile> topTrackSongs = new ArrayList<MediaFile>();
	    topTrackSongs = mediaFileService.getTopTracks(artist, userGroupId); 
	    if (topTrackSongs.size() < 1) {
	    	lastFMService.updateTopTrackArtist(artist);
        	lastFMService.updateTopTrackEntries(artist, settingsService.getLastFMResultSize(), userGroupId); 
		    topTrackSongs = mediaFileService.getTopTracks(artist, userGroupId);
	    }
        Player player = getCurrentPlayer(request, response);
        player.getPlayQueue().addFiles(true, topTrackSongs);
        player.getPlayQueue().setRandomSearchCriteria(null);
        return convert(request, player, true);
    }
    
    public void updateTopTrack(String artist) throws Exception {
    	
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        int userGroupId = securityService.getCurrentUserGroupId(request); 
        
    	int toplastFMfound = lastFMService.updateTopTrackArtist(artist);
    	int topPlayedfound = lastFMService.updateTopTrackEntries(artist, settingsService.getLastFMResultSize(), userGroupId).size();
    	topPlayedfound = mediaFileService.getTopTracks(artist, userGroupId).size();
    	
    	lastFMService.updateTopTrackStats(artist, topPlayedfound, toplastFMfound );   	
    }
    
    public PlayQueueInfo playRandom(int id, int count) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();

        MediaFile file = mediaFileService.getMediaFile(id);
        List<MediaFile> randomFiles = getRandomChildren(file, count);
        Player player = getCurrentPlayer(request, response);
        player.getPlayQueue().addFiles(false, randomFiles);
        player.getPlayQueue().setRandomSearchCriteria(null);
        return convert(request, player, true);
    }

    public PlayQueueInfo add(int id) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        return doAdd(request, response, new int[]{id}, null);
    }

    public PlayQueueInfo addAt(int id, int index) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        return doAdd(request, response, new int[]{id}, index);
    }
    
    public PlayQueueInfo addSelected(int[] ids) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        return doAdd(request, response, ids, null);
    }

    public PlayQueueInfo addSelectedAt(int[] ids, int index) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        return doAdd(request, response, ids, index);
    }    
    
    public PlayQueueInfo doAdd(HttpServletRequest request, HttpServletResponse response, int[] ids, Integer index) throws Exception {
        Player player = getCurrentPlayer(request, response);
        List<MediaFile> files = new ArrayList<MediaFile>(ids.length);
        for (int id : ids) {
            MediaFile ancestor = mediaFileService.getMediaFile(id);
            files.addAll(mediaFileService.getDescendantsOf(ancestor, true));
        }
        if (player.isWeb()) {
            removeVideoFiles(files);
        }
        if (index != null) {
            player.getPlayQueue().addFilesAt(files, index);
        } else {
        player.getPlayQueue().addFiles(true, files);
        }
        player.getPlayQueue().setRandomSearchCriteria(null);
        return convert(request, player, false);
    }
    
    public PlayQueueInfo doSet(HttpServletRequest request, HttpServletResponse response, int[] ids) throws Exception {
        Player player = getCurrentPlayer(request, response);
        PlayQueue playQueue = player.getPlayQueue();
        MediaFile currentFile = playQueue.getCurrentFile();
        PlayQueue.Status status = playQueue.getStatus();

        playQueue.clear();
        PlayQueueInfo result = doAdd(request, response, ids, null);

        int index = currentFile == null ? -1 : playQueue.getFiles().indexOf(currentFile);
        playQueue.setIndex(index);
        playQueue.setStatus(status);
        return result;
    }

    public PlayQueueInfo clear() throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        return doClear(request, response);
    }

    public PlayQueueInfo doClear(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Player player = getCurrentPlayer(request, response);
        player.getPlayQueue().clear();
        boolean serverSidePlaylist = !player.isExternalWithPlaylist();
        return convert(request, player, serverSidePlaylist);
    }

    public PlayQueueInfo shuffle() throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        return doShuffle(request, response);
    }

    public PlayQueueInfo doShuffle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Player player = getCurrentPlayer(request, response);
        player.getPlayQueue().shuffle();
        return convert(request, player, false);
    }

    public PlayQueueInfo remove(int index) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        return doRemove(request, response, index);
    }

    public PlayQueueInfo toggleStar(int index) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        Player player = getCurrentPlayer(request, response);

        MediaFile file = player.getPlayQueue().getFile(index);
        String username = securityService.getCurrentUsername(request);
        boolean starred = mediaFileDao.getMediaFileStarredDate(file.getId(), username) != null;
        if (starred) {
            mediaFileDao.unstarMediaFile(file.getId(), username);
        } else {
            mediaFileDao.starMediaFile(file.getId(), username);
        }
        return convert(request, player, false);
    }

    public PlayQueueInfo doRemove(HttpServletRequest request, HttpServletResponse response, int index) throws Exception {
        Player player = getCurrentPlayer(request, response);
        player.getPlayQueue().removeFileAt(index);
        return convert(request, player, false);
    }

    public PlayQueueInfo removeMany(int[] indexes) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        Player player = getCurrentPlayer(request, response);
        for (int i = indexes.length - 1; i >= 0; i--) {
            player.getPlayQueue().removeFileAt(indexes[i]);
        }
        return convert(request, player, false);
    }

    public PlayQueueInfo up(int index) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        Player player = getCurrentPlayer(request, response);
        player.getPlayQueue().moveUp(index);
        return convert(request, player, false); 
    }

    public PlayQueueInfo down(int index) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        Player player = getCurrentPlayer(request, response);
        player.getPlayQueue().moveDown(index);
        return convert(request, player, false);
    }

    public PlayQueueInfo toggleRepeat() throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        Player player = getCurrentPlayer(request, response);
        player.getPlayQueue().setRepeatEnabled(!player.getPlayQueue().isRepeatEnabled());
        return convert(request, player, false);
    } 

    public PlayQueueInfo togglePandora() throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        Player player = getCurrentPlayer(request, response);
        player.getPlayQueue().setPandoraEnabled(!player.getPlayQueue().isPandoraEnabled());
        return convert(request, player, false);
    }	
	
    public PlayQueueInfo undo() throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        Player player = getCurrentPlayer(request, response);
        player.getPlayQueue().undo();
        boolean serverSidePlaylist = !player.isExternalWithPlaylist();
        return convert(request, player, serverSidePlaylist);
    }

    public PlayQueueInfo sortByTrack() throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        Player player = getCurrentPlayer(request, response);
        player.getPlayQueue().sort(PlayQueue.SortOrder.TRACK);
        return convert(request, player, false);
    }

    public PlayQueueInfo sortByArtist() throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        Player player = getCurrentPlayer(request, response);
        player.getPlayQueue().sort(PlayQueue.SortOrder.ARTIST);
        return convert(request, player, false);
    }

    public PlayQueueInfo sortByAlbum() throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        Player player = getCurrentPlayer(request, response);
        player.getPlayQueue().sort(PlayQueue.SortOrder.ALBUM);
        return convert(request, player, false);
    }

    public void setGain(float gain) {
        jukeboxService.setGain(gain);
    }


    public String savePlaylist() {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        HttpServletResponse response = WebContextFactory.get().getHttpServletResponse();
        Player player = getCurrentPlayer(request, response);
        Locale locale = settingsService.getLocale();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);

        Date now = new Date();
        Playlist playlist = new Playlist();
        playlist.setUsername(securityService.getCurrentUsername(request));
        playlist.setCreated(now);
        playlist.setChanged(now);
        playlist.setPublic(false);
        playlist.setName(dateFormat.format(now));

        playlistService.createPlaylist(playlist);
        playlistService.setFilesInPlaylist(playlist.getId(), player.getPlayQueue().getFiles());
        return playlist.getName();
    }
        
    private List<MediaFile> getRandomChildren(MediaFile file, int count) throws IOException {
        List<MediaFile> children = mediaFileService.getDescendantsOf(file, false);
        removeVideoFiles(children);

        if (children.isEmpty()) {
            return children;
        }
        Collections.shuffle(children);
        return children.subList(0, Math.min(count, children.size()));
    }

    private void removeVideoFiles(List<MediaFile> files) {
        Iterator<MediaFile> iterator = files.iterator();
        while (iterator.hasNext()) {
            MediaFile file = iterator.next();
            if (file.isVideo()) {
                iterator.remove();
            }
        }
    }

    private PlayQueueInfo convert(HttpServletRequest request, Player player, boolean sendM3U) throws Exception {
        return convert(request, player, sendM3U, 0);
    }

    private PlayQueueInfo convert(HttpServletRequest request, Player player, boolean sendM3U, int offset) throws Exception {
        String url = request.getRequestURL().toString();

        if (sendM3U && player.isJukebox()) {
            jukeboxService.updateJukebox(player, offset);
        }
        boolean isCurrentPlayer = player.getIpAddress() != null && player.getIpAddress().equals(request.getRemoteAddr());

        boolean m3uSupported = player.isExternal() || player.isExternalWithPlaylist();
        sendM3U = player.isAutoControlEnabled() && m3uSupported && isCurrentPlayer && sendM3U;
        Locale locale = RequestContextUtils.getLocale(request);

        List<PlayQueueInfo.Entry> entries = new ArrayList<PlayQueueInfo.Entry>();
        PlayQueue playQueue = player.getPlayQueue();
        String localIp = settingsService.getLocalIpAddress();
        int localPort = settingsService.getPort();
        
        for (MediaFile file : playQueue.getFiles()) {
            String albumUrl = url.replaceFirst("/dwr/.*", "/main.view?id=" + file.getId());
            String streamUrl = url.replaceFirst("/dwr/.*", "/stream?player=" + player.getId() + "&id=" + file.getId());
            String coverArtUrl = url.replaceFirst("/dwr/.*", "/coverArt.view?id=" + file.getId());
            
//            String host = new URL(streamUrl).getHost();
//            String remoteStreamUrl = streamUrl.replaceFirst(host, ip);
//            String remoteCoverArtUrl = coverArtUrl.replaceFirst(host, ip);

            // Rewrite URLs in case we're behind a proxy.
            if (settingsService.isRewriteUrlEnabled()) {
                String referer = request.getHeader("referer");
                albumUrl = StringUtil.rewriteUrl(albumUrl, referer);
                streamUrl = StringUtil.rewriteUrl(streamUrl, referer);
            }
            String shareUrl = settingsService.getMadsonicUrl();
            boolean urlRedirectionEnabled = settingsService.isUrlRedirectionEnabled();
            String urlRedirectFrom = settingsService.getUrlRedirectFrom();
            String urlRedirectContextPath = settingsService.getUrlRedirectContextPath();

            String remoteStreamUrl = StringUtil.rewriteRemoteUrl(streamUrl, shareUrl, urlRedirectionEnabled, urlRedirectFrom, urlRedirectContextPath, localIp, localPort);
            String remoteCoverArtUrl = StringUtil.rewriteRemoteUrl(coverArtUrl, shareUrl, urlRedirectionEnabled, urlRedirectFrom, urlRedirectContextPath, localIp, localPort);

            String format = formatFormat(player, file);
            String username = securityService.getCurrentUsername(request);
            boolean starred = mediaFileService.getMediaFileStarredDate(file.getId(), username) != null;
            
            entries.add(new PlayQueueInfo.Entry(file.getId(), file.getDiscNumber(), file.getTrackNumber(), file.getTitle(), file.getArtist(),
                    file.getAlbumName(), file.getGenre(), file.getMood(), file.getYear(), formatBitRate(file),
                    file.getDurationSeconds(), file.getDurationString(), format, formatContentType(format),
                    formatFileSize(file.getFileSize(), locale), starred, albumUrl, streamUrl, remoteStreamUrl, coverArtUrl, remoteCoverArtUrl, file.getRank() ));
        }
        
        boolean isStopEnabled = playQueue.getStatus() == PlayQueue.Status.PLAYING && !player.isExternalWithPlaylist();
        float gain = jukeboxService.getGain();
        return new PlayQueueInfo(entries, playQueue.getIndex(), isStopEnabled, playQueue.isRepeatEnabled(), playQueue.isPandoraEnabled(), sendM3U, gain);
    }

    private String formatFileSize(Long fileSize, Locale locale) {
        if (fileSize == null) {
            return null;
        }
        return StringUtil.formatBytes(fileSize, locale);
    }

    private String formatFormat(Player player, MediaFile file) {
        return transcodingService.getSuffix(player, file, null);
    }

    private String formatContentType(String format) {
        return StringUtil.getMimeType(format);
    }

    private String formatBitRate(MediaFile mediaFile) {
        if (mediaFile.getBitRate() == null) {
            return null;
        }
        if (mediaFile.isVariableBitRate()) {
            return mediaFile.getBitRate() + " Kbps vbr";
        }
        return mediaFile.getBitRate() + " Kbps";
    }

    private Player getCurrentPlayer(HttpServletRequest request, HttpServletResponse response) {
        return playerService.getPlayer(request, response);
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setJukeboxService(JukeboxService jukeboxService) {
        this.jukeboxService = jukeboxService;
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setMediaFileDao(MediaFileDao mediaFileDao) {
        this.mediaFileDao = mediaFileDao;
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }
    
    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }
    
    public void setLastFMService(LastFMService lastFMService) {
        this.lastFMService = lastFMService;
    }    
}