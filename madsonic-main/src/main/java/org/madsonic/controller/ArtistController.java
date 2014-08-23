/*
 This file is part of Madsonic.

 Madsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Madsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License		
 along with Madsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2013 (C) Madevil
 */
package org.madsonic.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;

import org.madsonic.service.PlaylistService;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.support.RequestContextUtils;

import org.madsonic.Logger;
import org.madsonic.dao.ArtistDao;
import org.madsonic.domain.AccessRight;
import org.madsonic.domain.Artist;
import org.madsonic.domain.InternetRadio;
import org.madsonic.domain.LastFMAlbumSimilar;
import org.madsonic.domain.LastFMArtist;
import org.madsonic.domain.MediaFile;
import org.madsonic.domain.MediaLibraryStatistics;
import org.madsonic.domain.MusicFolder;
import org.madsonic.domain.MusicIndex;
import org.madsonic.domain.MusicIndex.SortableArtistWithData;
import org.madsonic.domain.MusicIndex.SortableArtistWithAlbums;
import org.madsonic.domain.MusicIndex.SortableArtistWithArtist;
import org.madsonic.domain.MusicIndex.SortableArtistWithMediaFiles;
import org.madsonic.domain.UserSettings;
import org.madsonic.service.LastFMService;
import org.madsonic.service.MediaFileService;
import org.madsonic.service.MediaScannerService;
import org.madsonic.service.MusicIndexService;
import org.madsonic.service.PlayerService;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;
import org.madsonic.util.FileUtil;
import org.madsonic.util.StringUtil;

/**
 * Controller for the left index frame.
 *
 * @author Sindre Mehus
 */
public class ArtistController extends ParameterizableViewController {

    private static final Logger LOG = Logger.getLogger(ArtistController.class);

    // Update this time if you want to force a refresh in clients.
    private static final Calendar LAST_COMPATIBILITY_TIME = Calendar.getInstance();

	private static final int DEFAULT_GROUP_MUSIC = 1;
	private static final int DEFAULT_GROUP_VIDEO = 2;
	
    static {
        LAST_COMPATIBILITY_TIME.set(2013, Calendar.SEPTEMBER, 1, 0, 0, 0);
        LAST_COMPATIBILITY_TIME.set(Calendar.MILLISECOND, 0);
    }

    private MediaScannerService mediaScannerService;
    private SettingsService settingsService;
    private SecurityService securityService;
    private MediaFileService mediaFileService;
    private MusicIndexService musicIndexService;
    private PlayerService playerService;
    private PlaylistService playlistService;
    private LastFMService lastFMService;   

    private ArtistDao artistDao;
    
    /**
     * Note: This class intentionally does not implement org.springframework.web.servlet.mvc.LastModified
     * as we don't need browser-side caching of left.jsp.  This method is only used by RESTController.
     */
    public long getLastModified(HttpServletRequest request) {
        saveSelectedMusicFolder(request);

        if (mediaScannerService.isScanning()) {
            return -1L;
        }

        long lastModified = LAST_COMPATIBILITY_TIME.getTimeInMillis();
        String username = securityService.getCurrentUsername(request);

        // When was settings last changed?
        lastModified = Math.max(lastModified, settingsService.getSettingsChanged());

        // When was music folder(s) on disk last changed?
        List<MusicFolder> allMusicFolders = settingsService.getAllMusicFolders();
        MusicFolder selectedMusicFolder = getSelectedMusicFolder(request);
        if (selectedMusicFolder != null) {
            File file = selectedMusicFolder.getPath();
            lastModified = Math.max(lastModified, FileUtil.lastModified(file));
        } else {
            for (MusicFolder musicFolder : allMusicFolders) {
                File file = musicFolder.getPath();
                lastModified = Math.max(lastModified, FileUtil.lastModified(file));
            }
        }

        // When was music folder table last changed?
        for (MusicFolder musicFolder : allMusicFolders) {
            lastModified = Math.max(lastModified, musicFolder.getChanged().getTime());
        }

        // When was user settings last changed?
        UserSettings userSettings = settingsService.getUserSettings(username);
        lastModified = Math.max(lastModified, userSettings.getChanged().getTime());

        return lastModified;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        saveSelectedMusicFolder(request);
        Map<String, Object> map = new HashMap<String, Object>();

        MediaLibraryStatistics statistics = mediaScannerService.getStatistics();
        Locale locale = RequestContextUtils.getLocale(request);

        String username = securityService.getCurrentUsername(request);
        int userGroupId = securityService.getCurrentUserGroupId(request);
        
        List<MusicFolder> allMusicFolders = settingsService.getAllMusicFolders(userGroupId, true, DEFAULT_GROUP_MUSIC);
        MusicFolder selectedMusicFolder = getSelectedMusicFolder(request);
        
        List<MusicFolder> musicFoldersToUse = selectedMusicFolder == null ? allMusicFolders : Arrays.asList(selectedMusicFolder);
        String[] shortcuts = settingsService.getShortcutsAsArray();
        
        UserSettings userSettings = settingsService.getUserSettings(username);
        
        boolean refresh = ServletRequestUtils.getBooleanParameter(request, "refresh", false);

        boolean showAlbum = ServletRequestUtils.getBooleanParameter(request, "showAlbum", false);
        map.put("showAlbum", showAlbum);
        
        String index = ServletRequestUtils.getStringParameter(request, "name");
        if (index == null){
        	index = "?";
        }
        
        map.put("player", playerService.getPlayer(request, response));
        map.put("scanning", mediaScannerService.isScanning());
        map.put("musicFolders", allMusicFolders);
        map.put("selectedMusicFolder", selectedMusicFolder);

        map.put("shortcuts", getShortcuts(musicFoldersToUse, shortcuts));
        map.put("captionCutoff", userSettings.getMainVisibility().getCaptionCutoff());
        map.put("partyMode", userSettings.isPartyModeEnabled());

        
//      map.put("allArtists", mediaFileService.getAllArtists());
        MusicFolderContent musicFolderContent = getMusicFolderContent(musicFoldersToUse, index, refresh);
        
//      map.put("indexedArtistHub", musicFolderContent.getIndexedArtists()); // .getIndexedArtistHub());
//      map.put("indexedArtistHub", musicFolderContent.getIndexedArtistHub()); // .getIndexedArtistHub());

        map.put("indexedArtistHub", musicFolderContent.getIndexedArtistsHub()); // .getIndexedArtistHub());
        map.put("indexes", musicFolderContent.getIndexedArtists().keySet());
        
        try	{
	        LastFMArtist lastFMArtist = lastFMService.getArtist(index);
	        
	        if ((lastFMArtist == null))  {
	        	
	        	if (index.toString().length() > 1) { 
	        	
	        	LOG.debug("#Try search for Data: " + index);
	        	
	            List<Artist> artist = new ArrayList<Artist>();
	            Artist a = new Artist();
	            a.setName(index);
	            a.setArtistFolder(index);
	            artist.add(a);
				lastFMService.getArtistInfo(artist);
	        	lastFMArtist = lastFMService.getArtist(index);
	        	
		        if ((lastFMArtist != null)){
		        	LOG.debug("## Data found for: " + index);
		        } else {
		        	LOG.error("## No Data found for: " + index);
		        }
         	  } 
	        }
	        
	        List listTopTag = null; 
	        List listTopAlbum = null; 
	        
	        List <LastFMAlbumSimilar> al = new ArrayList<LastFMAlbumSimilar>();
	    //  al.add(new LastFMAlbumSimilar("Discovery",null,1233));
	        
	        if (lastFMArtist != null){
		        if (lastFMArtist.getToptag() != null) {	
			        String[] sep = lastFMArtist.getToptag().split("\\|");
			        listTopTag = Arrays.asList(sep);
		        }
	        }
	        
	        if (lastFMArtist != null){
		        if (lastFMArtist.getTopalbum() != null) {	
			        String[] sep = lastFMArtist.getTopalbum().split("\\|");
			        listTopAlbum = Arrays.asList(sep);
		        }
	        }
	        if (listTopAlbum != null){
	        for (Object Album : listTopAlbum) {
        	
		        LastFMAlbumSimilar a = new LastFMAlbumSimilar();

		        a.setAlbumName(Album.toString());
		        a.setMediaFileId(mediaFileService.getIdsForAlbums(index, Album.toString()));
		        
		        al.add(a);        	
	        }
	        }
	        
	        map.put("lastFMArtist", lastFMArtist) ;
	        map.put("lastFMArtistTopTags", listTopTag);
	        map.put("lastFMArtistTopAlbums", listTopAlbum);
	        map.put("lastFMArtistTopAlbumX", al);
	        List <String> Similar = null ;
	        
	        if (lastFMArtist != null){
	        	Similar = lastFMService.getSimilarArtist(lastFMArtist.getArtistname());	        
	        }
	        map.put("lastFMArtistSimilar", Similar) ;
        }
        catch (Exception x) {
            LOG.error("## Failed to Fetch Data: " + index, x);
        }   
        
//      map.put("lastFMArtistTopTracks", null);
        map.put("user", securityService.getCurrentUser(request));
        map.put("customScrollbar", userSettings.isCustomScrollbarEnabled()); 		
		
        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private void saveSelectedMusicFolder(HttpServletRequest request) {
        if (request.getParameter("musicFolderId") == null) {
            return;
        }
        int musicFolderId = Integer.parseInt(request.getParameter("musicFolderId"));

        // Note: UserSettings.setChanged() is intentionally not called. This would break browser caching
        // of the left frame.
        UserSettings settings = settingsService.getUserSettings(securityService.getCurrentUsername(request));
        settings.setSelectedMusicFolderId(musicFolderId);
        settingsService.updateUserSettings(settings);
    }

    /**
     * Returns the selected music folder, or <code>null</code> if all music folders should be displayed.
     */
    private MusicFolder getSelectedMusicFolder(HttpServletRequest request) {
        UserSettings settings = settingsService.getUserSettings(securityService.getCurrentUsername(request));
        int musicFolderId = settings.getSelectedMusicFolderId();

        return settingsService.getMusicFolderById(musicFolderId);
    }

    private List<MediaFile> getMediaFiles(HttpServletRequest request, int user_group_id) {
        List<MediaFile> mediaFiles = new ArrayList<MediaFile>();
        for (String path : ServletRequestUtils.getStringParameters(request, "path")) {
            MediaFile mediaFile = mediaFileService.getMediaFile(path);
            if (mediaFile != null) {
                mediaFiles.add(mediaFile);
            }
        }
        for (int id : ServletRequestUtils.getIntParameters(request, "id")) {
            MediaFile mediaFile = mediaFileService.getMediaFile(id, user_group_id);
            if (mediaFile != null) {
                mediaFiles.add(mediaFile);
            }
        }
        return mediaFiles;
    }

    private List<MediaFile> getMultiFolderChildren(List<MediaFile> mediaFiles) throws IOException {
        List<MediaFile> result = new ArrayList<MediaFile>();
        for (MediaFile mediaFile : mediaFiles) {
            if (mediaFile.isFile()) {
                mediaFile = mediaFileService.getParentOf(mediaFile);
            }
            result.addAll(mediaFileService.getChildrenOf(mediaFile, true, true, true));
            result = mediaFileService.getChildrenSorted(result, true);
        }
        return result;
    }    
    
    public List<MediaFile> getShortcuts(List<MusicFolder> musicFoldersToUse, String[] shortcuts) {
        List<MediaFile> result = new ArrayList<MediaFile>();

        for (String shortcut : shortcuts) {
            for (MusicFolder musicFolder : musicFoldersToUse) {
                File file = new File(musicFolder.getPath(), shortcut);
                if (FileUtil.exists(file)) {
                    result.add(mediaFileService.getMediaFile(file, true));
                }
            }
        }

        return result;
    }

    public MusicFolderContent getMusicFolderContent(List<MusicFolder> musicFoldersToUse, String indexToUse, boolean refresh) throws Exception {
    	
    	int TempIndex = 1;
    	
    	if (musicFoldersToUse.size() > 1 ){
    		TempIndex = 1; 
    	}else
    	{
	        for (MusicFolder folder : musicFoldersToUse) {
		    	if (folder.getIndex() != null){
		    		TempIndex = folder.getIndex();
		    		break;
		    	}
		    }
    	}
    	
    	//TODO: INDEX

   	    SortedMap<MusicIndex, SortedSet<MusicIndex.SortableArtistWithMediaFiles>> indexedArtists = musicIndexService.getIndexedArtists(musicFoldersToUse, refresh, 1);
        SortedMap<MusicIndex, SortedSet<MusicIndex.SortableArtistWithAlbums>> indexedArtistsHub = musicIndexService.getIndexedArtistsHub(musicFoldersToUse, indexToUse);
//      SortedMap<MusicIndex, SortedSet<MusicIndex.SortableArtistWithData>> indexedArtistsData = musicIndexService.getIndexedArtistsData(musicFoldersToUse, true, indexToUse);
        
        return new MusicFolderContent(indexedArtists, indexedArtistsHub); // indexedArtistsOnly
    }

    public void setMediaScannerService(MediaScannerService mediaScannerService) {
        this.mediaScannerService = mediaScannerService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setMusicIndexService(MusicIndexService musicIndexService) {
        this.musicIndexService = musicIndexService;
    }

    public void setArtistDao(ArtistDao artistDao) {
        this.artistDao = artistDao;
    }
    
    public void setLastFMService(LastFMService lastFMService) {
        this.lastFMService = lastFMService;
    }
    
    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    public static class MusicFolderContent {

        private final SortedMap<MusicIndex, SortedSet<SortableArtistWithMediaFiles>> indexedArtists;
        private final SortedMap<MusicIndex, SortedSet<SortableArtistWithAlbums>> indexedArtistsHub;
//      private final SortedMap<MusicIndex, SortedSet<SortableArtistWithData>> indexedArtistsData;		

        public MusicFolderContent(SortedMap<MusicIndex, SortedSet<SortableArtistWithMediaFiles>> indexedArtists, 
       						      SortedMap<MusicIndex, SortedSet<SortableArtistWithAlbums>> indexedArtistsHub ) {
             	        
            this.indexedArtists = indexedArtists;
            this.indexedArtistsHub = indexedArtistsHub;
//          this.indexedArtistsData = indexedArtistsData;
            }

		public SortedMap<MusicIndex, SortedSet<MusicIndex.SortableArtistWithMediaFiles>> getIndexedArtists() {
			return indexedArtists;
		}
		
		public SortedMap<MusicIndex, SortedSet<MusicIndex.SortableArtistWithAlbums>> getIndexedArtistsHub() {
            return indexedArtistsHub;
        }
  
//		public SortedMap<MusicIndex, SortedSet<MusicIndex.SortableArtistWithData>> getIndexedArtistsData() {
//			return indexedArtistsData;
//		}
}
}
