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
package org.madsonic.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;

import org.madsonic.service.PlaylistService;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.support.RequestContextUtils;

import org.madsonic.Logger;
import org.madsonic.domain.InternetRadio;
import org.madsonic.domain.MediaFile;
import org.madsonic.domain.MediaLibraryStatistics;
import org.madsonic.domain.MusicFolder;
import org.madsonic.domain.MusicFolderComparator;
import org.madsonic.domain.MusicIndex;
import org.madsonic.domain.UserSettings;
import org.madsonic.service.MediaFileService;
import org.madsonic.service.MediaScannerService;
import org.madsonic.service.MusicIndexService;
import org.madsonic.service.PlayerService;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;
import org.madsonic.service.metadata.JaudiotaggerParser;
import org.madsonic.util.FileUtil;
import org.madsonic.util.StringUtil;

/**
 * Controller for the left index frame.
 *
 * @author Sindre Mehus
 */
public class LeftController extends ParameterizableViewController {

    private static final Logger LOG = Logger.getLogger(LeftController.class);

    // Update this time if you want to force a refresh in clients.
    private static final Calendar LAST_COMPATIBILITY_TIME = Calendar.getInstance();
    
    static {
        LAST_COMPATIBILITY_TIME.set(2014, Calendar.MAY, 1, 0, 0, 0);
        LAST_COMPATIBILITY_TIME.set(Calendar.MILLISECOND, 0);
    }

    private MediaScannerService mediaScannerService;
    private SettingsService settingsService;
    private SecurityService securityService;
    private MediaFileService mediaFileService;
    private MusicIndexService musicIndexService;
    private PlayerService playerService;
    private PlaylistService playlistService;

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

        // When was internet radio table last changed?
        for (InternetRadio internetRadio : settingsService.getAllInternetRadios()) {
            lastModified = Math.max(lastModified, internetRadio.getChanged().getTime());
        }

        // When was user settings last changed?
        UserSettings userSettings = settingsService.getUserSettings(username);
        lastModified = Math.max(lastModified, userSettings.getChanged().getTime());

        return lastModified;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        saveSelectedMusicFolder(request);
        saveSelectedGenre(request);
        
        Map<String, Object> map = new HashMap<String, Object>();

        MediaLibraryStatistics statistics = mediaScannerService.getStatistics();
        Locale locale = RequestContextUtils.getLocale(request);

        String username = securityService.getCurrentUsername(request);
        int userGroupId = securityService.getCurrentUserGroupId(request);
        
        List<MusicFolder> allFolders = new ArrayList<MusicFolder>();
        
        //todo: get grouping setting 
        
        List<MusicFolder> allOtherFolders = settingsService.getAllMusicFolders(userGroupId, settingsService.isSortMediaFileFolder(), 0);
        List<MusicFolder> allMusicFolders = settingsService.getAllMusicFolders(userGroupId, settingsService.isSortMediaFileFolder(), 1);
        List<MusicFolder> allVideoFolders = settingsService.getAllMusicFolders(userGroupId, settingsService.isSortMediaFileFolder(), 2);
        
        allFolders.addAll(allOtherFolders);
        allFolders.addAll(allMusicFolders);
        allFolders.addAll(allVideoFolders);
        
        // TODO: new musicFolder Sort
        Comparator<MusicFolder> comparator = new MusicFolderComparator();
        Set<MusicFolder> set = new TreeSet<MusicFolder>(comparator);
        set.addAll(allFolders);
        allFolders = new ArrayList<MusicFolder>(set);
        
        int selectedMusicFolderId = getSelectedMusicFolderId(request);
        MusicFolder selectedMusicFolder = getSelectedMusicFolder(request);
        List<MusicFolder> musicFoldersToUse ;
        
        switch(selectedMusicFolderId){ 
        case -1: musicFoldersToUse = allFolders; break; 
        case -2: musicFoldersToUse = allMusicFolders; break; 
        case -3: musicFoldersToUse = allVideoFolders; break;             
        default: musicFoldersToUse = selectedMusicFolder == null ? allOtherFolders : Arrays.asList(selectedMusicFolder);
        break; 
        } 
        
        if ( allMusicFolders.size() > 0) {
            map.put("MusicFolderEnabled", true);
        }
        if ( allVideoFolders.size() > 0) {
            map.put("VideoFolderEnabled", true);
        }
        
        String selectedGenre = getSelectedGenre(request);
        
        // musicFoldersToUse = selectedMusicFolder == null ? allFolders : Arrays.asList(selectedMusicFolder);
        
        String[] shortcuts = settingsService.getShortcutsAsArray();
        UserSettings userSettings = settingsService.getUserSettings(username);
        boolean refresh = ServletRequestUtils.getBooleanParameter(request, "refresh", false);

        map.put("player", playerService.getPlayer(request, response));
        map.put("scanning", mediaScannerService.isScanning());
        
        map.put("musicFolders", allOtherFolders);
        
        map.put("selectedMusicFolder", selectedMusicFolder);
        map.put("selectedMusicFolderId", selectedMusicFolderId);
        
        map.put("selectedGenre", selectedGenre);

        List <String> _allGenres = mediaFileService.getArtistGenresforFolder(musicFoldersToUse, userGroupId);
		map.put("allGenres", _allGenres);
		_allGenres.add("unknown genre");
		if (selectedGenre != "unknown genre") {
			boolean genreFound = false;
			for (String s : _allGenres) {
				if (s.equals(selectedGenre)) { genreFound = true; }
			}
			if (genreFound == false) {
				selectedGenre = "";
//		        if (selectedGenre == "unknown") {
//		        	selectedGenre = "unknown genre" ;
//		        }
		        UserSettings settings = settingsService.getUserSettings(securityService.getCurrentUsername(request));
		        settings.setSelectedGenre(selectedGenre);
		        settingsService.updateUserSettings(settings);
		        map.put("selectedGenre", selectedGenre);
			}
		}
		;
		String selectedIndex;
        switch(selectedMusicFolderId){ 
	        case -1: selectedIndex = settingsService.getAllIndexString(); break; 
	        case -2: selectedIndex = settingsService.getMusicIndexString(); break; 
	        case -3: selectedIndex = settingsService.getVideoIndexString(); break;             
	        default: selectedIndex = "1"; break; 
        } 

		MusicFolderContent musicFolderContent = getMusicFolderContent(musicFoldersToUse, selectedGenre, selectedIndex , refresh);
	
        map.put("radios", settingsService.getAllInternetRadios());
        map.put("shortcuts", getShortcuts(musicFoldersToUse, shortcuts));
        map.put("captionCutoff", userSettings.getMainVisibility().getCaptionCutoff());
        map.put("partyMode", userSettings.isPartyModeEnabled());
        map.put("organizeByFolderStructure", settingsService.isOrganizeByFolderStructure());

        map.put("listType", userSettings.getListType());
        
		map.put("ShowShortcuts", settingsService.isShowShortcuts());
		
        if (statistics != null) {
            map.put("statistics", statistics);
            long bytes = statistics.getTotalLengthInBytes();
            long hours = statistics.getTotalDurationInSeconds() / 3600L;
            map.put("hours", hours);
            map.put("bytes", StringUtil.formatBytes(bytes, locale));
        }

        map.put("indexedArtists", musicFolderContent.getIndexedArtists());
        map.put("singleSongs", musicFolderContent.getSingleSongs());
        map.put("indexes", musicFolderContent.getIndexedArtists().keySet());
        map.put("user", securityService.getCurrentUser(request));
        map.put("customScrollbar", userSettings.isCustomScrollbarEnabled()); 		
        map.put("customAccordion", userSettings.isCustomAccordionEnabled()); 
        map.put("playlistEnabled", settingsService.isPlaylistEnabled());
        
//      map.put("sortMediaFileFolder", settingsService.isSortMediaFileFolder());
        
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

    private void saveSelectedGenre(HttpServletRequest request) {
        if (request.getParameter("genre") == null) {
            return;
        }
        String selectedGenre = request.getParameter("genre");

        UserSettings settings = settingsService.getUserSettings(securityService.getCurrentUsername(request));
        settings.setSelectedGenre(selectedGenre);
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

    private int getSelectedMusicFolderId(HttpServletRequest request) {
        UserSettings settings = settingsService.getUserSettings(securityService.getCurrentUsername(request));
        return settings.getSelectedMusicFolderId();

    }
    
    private String getSelectedGenre(HttpServletRequest request) {
        UserSettings settings = settingsService.getUserSettings(securityService.getCurrentUsername(request));
        String genre = settings.getSelectedGenre();
        return genre;
    }    
    
    protected List<MediaFile> getSingleSongs(List<MusicFolder> folders, boolean refresh) throws IOException {
        List<MediaFile> result = new ArrayList<MediaFile>();
        for (MusicFolder folder : folders) {
            MediaFile parent = mediaFileService.getMediaFile(folder.getPath(), !refresh);
            result.addAll(mediaFileService.getChildrenOf(parent, true, false, true, !refresh));
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

    public MusicFolderContent getMusicFolderContent(List<MusicFolder> musicFoldersToUse, String genre, String selectedIndex, boolean refresh) throws Exception {
    	int usedIndex = Integer.valueOf(selectedIndex);
//    	
//    	if (musicFoldersToUse.size() > 1 ){
//    		usedIndex = 1; 
//    	}else
//    	{
//	        for (MusicFolder folder : musicFoldersToUse) {
//		    	if (folder.getIndex() != null){
//		    		usedIndex = folder.getIndex();
//		    		break;
//		    	}
//		    }
//    	}
        SortedMap<MusicIndex, SortedSet<MusicIndex.SortableArtistforGenre>> indexedArtists = musicIndexService.getIndexedArtistsforGenre(musicFoldersToUse, refresh, genre, usedIndex);
        List<MediaFile> singleSongs = getSingleSongs(musicFoldersToUse, refresh);
        return new MusicFolderContent(indexedArtists, singleSongs);
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

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    public static class MusicFolderContent {

//      private final SortedMap<MusicIndex, SortedSet<MusicIndex.SortableArtistWithMediaFiles>> indexedArtists;
    	
        private final SortedMap<MusicIndex, SortedSet<MusicIndex.SortableArtistforGenre>> indexedArtists;
        
        private final List<MediaFile> singleSongs;

        public MusicFolderContent(SortedMap<MusicIndex, SortedSet<MusicIndex.SortableArtistforGenre>> indexedArtists, List<MediaFile> singleSongs) {
            this.indexedArtists = indexedArtists;
            this.singleSongs = singleSongs;
        }
//      public MusicFolderContent(SortedMap<MusicIndex, SortedSet<MusicIndex.SortableArtistWithMediaFiles>> indexedArtists, List<MediaFile> singleSongs) {
//            this.indexedArtists = indexedArtists;
//            this.singleSongs = singleSongs;
//        }

//      public SortedMap<MusicIndex, SortedSet<MusicIndex.SortableArtistWithMediaFiles>> getIndexedArtists() {
//            return indexedArtists;
//        }

        public SortedMap<MusicIndex, SortedSet<MusicIndex.SortableArtistforGenre>> getIndexedArtists() {
            return indexedArtists;
        }
        
        public List<MediaFile> getSingleSongs() {
            return singleSongs;
        }

    }
}
