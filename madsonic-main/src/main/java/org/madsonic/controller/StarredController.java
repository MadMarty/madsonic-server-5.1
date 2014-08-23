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

import org.madsonic.dao.MediaFileDao;
import org.madsonic.domain.CoverArtScheme;
import org.madsonic.domain.MediaFile;
import org.madsonic.domain.User;
import org.madsonic.domain.UserSettings;
import org.madsonic.service.MediaFileService;
import org.madsonic.service.PlayerService;
import org.madsonic.service.PlaylistService;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for showing a user's starred items.
 *
 * @author Sindre Mehus
 */
public class StarredController extends ParameterizableViewController {

    private PlayerService playerService;
    private MediaFileDao mediaFileDao;
    private SecurityService securityService;
    private SettingsService settingsService;
    private MediaFileService mediaFileService;
	private PlaylistService playlistService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        User user = securityService.getCurrentUser(request);
        String username = user.getUsername();
        UserSettings userSettings = settingsService.getUserSettings(username);

        List<MediaFile> artists = mediaFileDao.getStarredDirectories(0, Integer.MAX_VALUE, username);
        List<MediaFile> albums = mediaFileDao.getStarredAlbums(0, Integer.MAX_VALUE, username);
        List<MediaFile> songs = mediaFileDao.getStarredFiles(0, Integer.MAX_VALUE, username);
        List<MediaFile> links = mediaFileDao.getStarredLinks(0, Integer.MAX_VALUE, username);
        List<MediaFile> sets = mediaFileDao.getStarredSets(0, Integer.MAX_VALUE, username);
        List<MediaFile> videos = mediaFileDao.getStarredVideos(0, Integer.MAX_VALUE, username);
        
		
        mediaFileService.populateStarredDate(artists, username);
        mediaFileService.populateStarredDate(albums, username);
        mediaFileService.populateStarredDate(songs, username);
        mediaFileService.populateStarredDate(links, username);        
        mediaFileService.populateStarredDate(sets, username);        
        mediaFileService.populateStarredDate(videos, username);  
        
        map.put("user", user);
        map.put("partyModeEnabled", userSettings.isPartyModeEnabled());
        map.put("customScrollbar", userSettings.isCustomScrollbarEnabled()); 		
        map.put("player", playerService.getPlayer(request, response));
        
        map.put("artists", artists);
        map.put("albums", albums);
        map.put("songs", songs);
        map.put("links", links);
        map.put("sets", sets);
        map.put("videos", videos);
        
        map.put("coverArtSize", CoverArtScheme.SMALL.getSize());
        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setMediaFileDao(MediaFileDao mediaFileDao) {
        this.mediaFileDao = mediaFileDao;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }	
	
    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }
}
