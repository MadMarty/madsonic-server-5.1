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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.madsonic.dao.MediaFileDao;
import org.madsonic.domain.*;
import org.madsonic.service.*;
import org.madsonic.filter.ParameterDecodingFilter;
import org.madsonic.lastfm.util.*;

import org.springframework.web.bind.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;
import org.springframework.web.servlet.view.*;

import javax.servlet.http.*;

/**
 * Controller for file statistics.
 *
 * @author Madevil
 */
public class StatisticsController extends ParameterizableViewController {

    private static final int MAX_LIST_SIZE = 200;
    private static final int MAX_LIST_OFFSET = 1000;
    private static final int DEFAULT_LIST_SIZE = 25;
	private static final int DEFAULT_LIST_OFFSET = 0;
	
    private static final String DEFAULT_LIST_TYPE = "lastplayed";
	
    private MediaFileDao mediaFileDao;
	private SecurityService securityService;
    private MediaFileService mediaFileService;
    private SettingsService settingsService;
    private PlayerService playerService;
    private RatingService ratingService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
    	
    //	String id = request.getParameter("id");
    //  MediaFile mediaFile = mediaFileService.getMediaFile(path);

        int listOffset  = DEFAULT_LIST_OFFSET;
        int listSize    = DEFAULT_LIST_SIZE;
	    String listType = DEFAULT_LIST_TYPE;
			
        User user = securityService.getCurrentUser(request);
        String username = user.getUsername();        
        UserSettings userSettings = settingsService.getUserSettings(username);
        int userGroupId = securityService.getCurrentUserGroupId(request);
        
        if (request.getParameter("listOffset") != null) {
            listOffset = Math.max(0, Math.min(Integer.parseInt(request.getParameter("listOffset")), MAX_LIST_OFFSET));
        }

        if (request.getParameter("listSize") != null) {
            listSize = Math.max(0, Math.min(Integer.parseInt(request.getParameter("listSize")), MAX_LIST_SIZE));
        }
		
		if (request.getParameter("listType") != null) {
            listType = String.valueOf(request.getParameter("listType"));
        }
		List<MediaFile> songs;
        if ("topplayed".equals(listType)) {
            songs = mediaFileDao.getTopPlayedCountForUser(listOffset, listSize, username);   
        } else if ("otheruser".equals(listType)) {
            songs = mediaFileDao.getLastPlayedCountForAllUser(listOffset, listSize, userGroupId);   
        } else if ("overall".equals(listType)) {
            songs = mediaFileDao.getTopPlayedCountForAllUser(listOffset, listSize, userGroupId);
        } else if ("lastplayed".equals(listType)) {
			songs = mediaFileDao.getLastPlayedCountForUser(listOffset, listSize, username);
        } else {
			songs = mediaFileDao.getLastPlayedCountForAllUser(0, 1, userGroupId);   
        }
		
        mediaFileService.populateStarredDate(songs, username);
        
        map.put("user", user);
        map.put("songs", songs);
		
        map.put("customScrollbar", userSettings.isCustomScrollbarEnabled()); 		
        map.put("partyModeEnabled", userSettings.isPartyModeEnabled());
        map.put("player", playerService.getPlayer(request, response));
        map.put("buttonVisibility", userSettings.getButtonVisibility()); 
        map.put("listOffset", listOffset);
        map.put("listSize", listSize);		
        map.put("listType", listType);
		
    //  map.put("starred", mediaFileService.getMediaFileStarredDate(dir.getId(), username) != null);		
        
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
    
    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setMediaFileDao(MediaFileDao mediaFileDao) {
        this.mediaFileDao = mediaFileDao;
    }    
	
	public void setRatingService(RatingService ratingService) {
        this.ratingService = ratingService;
    }
}
