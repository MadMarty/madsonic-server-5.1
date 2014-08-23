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

import static org.madsonic.domain.MediaFile.MediaType.AUDIOBOOK;
import static org.madsonic.domain.MediaFile.MediaType.MUSIC;
import static org.madsonic.domain.MediaFile.MediaType.PODCAST;
import static org.madsonic.domain.MediaFile.MediaType.VIDEO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.madsonic.dao.MediaFileDao;
import org.madsonic.domain.MediaFile;
import org.madsonic.domain.User;
import org.madsonic.domain.UserSettings;
import org.madsonic.service.MediaFileService;
import org.madsonic.service.PlayerService;
import org.madsonic.service.RatingService;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * Controller for file statistics.
 *
 * @author Madevil
 */
public class HistoryController extends ParameterizableViewController {

    private static final int MAX_LIST_SIZE        =  200;
    private static final int MAX_LIST_OFFSET      = 1000;
	
	private static final int DEFAULT_LIST_OFFSET =  0;
    private static final int DEFAULT_LIST_SIZE   = 20;
	
    private static final String DEFAULT_LIST_TYPE = "audio";
	
    private MediaFileDao mediaFileDao;
	private SecurityService securityService;
    private MediaFileService mediaFileService;
    
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();

        int listOffset  = DEFAULT_LIST_OFFSET;
        int listSize    = DEFAULT_LIST_SIZE;
	    String listType = DEFAULT_LIST_TYPE;
			
        User user = securityService.getCurrentUser(request);
        String username = user.getUsername();        

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
		List<MediaFile> songs = null;
		
        if ("audio".equals(listType)) {
            songs = mediaFileDao.getHistory(listOffset, listSize, userGroupId, MUSIC.name());
        } 	
    	else if ("audiobook".equals(listType)) {
            songs = mediaFileDao.getHistory(listOffset, listSize, userGroupId, AUDIOBOOK.name());
        } 	
    	else if ("podcast".equals(listType)) {
            songs = mediaFileDao.getHistory(listOffset, listSize, userGroupId, PODCAST.name());
        } 
    	else if ("video".equals(listType)) {
            songs = mediaFileDao.getHistory(listOffset, listSize, userGroupId, VIDEO.name());
        }	
    	else { 
            songs = mediaFileDao.getHistory(listOffset, listSize, userGroupId, MUSIC.name());
        }
        mediaFileService.populateStarredDate(songs, username);
        
        map.put("user", user);
        map.put("songs", songs);

        map.put("listOffset", listOffset);
        map.put("listSize", listSize);		
        map.put("listType", listType);
		
        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }
		
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
       
    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setMediaFileDao(MediaFileDao mediaFileDao) {
        this.mediaFileDao = mediaFileDao;
    }    
	
}
