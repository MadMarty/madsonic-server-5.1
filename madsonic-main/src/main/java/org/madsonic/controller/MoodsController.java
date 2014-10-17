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

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.madsonic.Logger;
import org.madsonic.service.MediaFileService;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;
import org.madsonic.domain.User;
import org.madsonic.domain.UserSettings;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * Controller for the moods radio.
 *
 */
public class MoodsController extends ParameterizableViewController {

    private SettingsService settingsService;
    private MediaFileService mediaFileService;
    private SecurityService securityService;    
    
    private long cacheTimestamp;
    private List <String> cachedMoods;    
    
    private static final Logger LOG = Logger.getLogger(MoodsController.class);
    
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();

        User user = securityService.getCurrentUser(request);
        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());
		
        int userGroupId = securityService.getCurrentUserGroupId(request);        

        if (cachedMoods == null || cacheTimestamp < settingsService.getLastScanned().getTime()) {
        	cachedMoods = mediaFileService.getMoods();
        	cacheTimestamp = settingsService.getLastScanned().getTime();
        	LOG.debug("## moods recached");  
        }   
        
		map.put("moods", cachedMoods);
        map.put("currentYear", Calendar.getInstance().get(Calendar.YEAR));

        map.put("musicFolders", settingsService.getAllMusicFolders(userGroupId, settingsService.isSortMediaFileFolder()));
        map.put("customScrollbar", userSettings.isCustomScrollbarEnabled()); 		

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);

        return result;
    }
    
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
    
    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
    
    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }	
}