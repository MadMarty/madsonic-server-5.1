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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import org.madsonic.domain.MusicFolder;
import org.madsonic.domain.User;
import org.madsonic.domain.UserSettings;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;
import org.madsonic.service.VersionService;

/**
 * Controller for the top frame.
 *
 * @author Sindre Mehus
 */
public class TopController extends ParameterizableViewController {

    private SettingsService settingsService;
    private VersionService versionService;
    private SecurityService securityService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        List<MusicFolder> allMusicFolders = settingsService.getAllMusicFolders();
        User user = securityService.getCurrentUser(request);

        Date trialExpires = settingsService.getTrialExpires();
        Date now = new Date();
        boolean trialValid = trialExpires.after(now);
        long trialDaysLeft = trialValid ? (trialExpires.getTime() - now.getTime()) /  (24L * 3600L * 1000L) : 0L;

        map.put("user", user);
        map.put("musicFoldersExist", !allMusicFolders.isEmpty());
        map.put("brand", settingsService.getBrand());
		
        if (settingsService.isUsePremiumServices()) {
              map.put("licensed", settingsService.isLicenseValid());
   		      map.put("licenseInfo", settingsService.getLicenseInfo());
        } else
        {
            map.put("licensed", true);
        }
        
        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());
        if (userSettings.isFinalVersionNotificationEnabled() && versionService.isNewFinalVersionAvailable()) {
            map.put("newVersionAvailable", true);
            map.put("latestVersion", versionService.getLatestFinalVersion());

        } else if (userSettings.isBetaVersionNotificationEnabled() && versionService.isNewBetaVersionAvailable()) {
            map.put("newVersionAvailable", true);
            map.put("latestVersion", versionService.getLatestBetaVersion());
        }

        map.put("NotificationEnabled", userSettings.isBetaVersionNotificationEnabled() || userSettings.isFinalVersionNotificationEnabled()); 

        if (user.isAdminRole()){
            map.put("NotificationEnabled", true);
        }
        
        map.put("listType", userSettings.getListType());
        map.put("listRows", userSettings.getListRows());
        map.put("listColumns", userSettings.getListColumns());
        map.put("showRight", userSettings.isShowNowPlayingEnabled() || userSettings.isShowChatEnabled());
        map.put("leftframeSize", settingsService.getLeftframeSize());
        		
        map.put("showIconHome", settingsService.showIconHome());
        map.put("showIconArtist", settingsService.showIconArtist());
        map.put("showIconPlaying", settingsService.showIconPlaying());
        map.put("showIconCover", settingsService.showIconCover());
        map.put("showIconStarred", settingsService.showIconStarred());
        map.put("showIconRadio", settingsService.showIconRadio());
		try {
			if (userSettings.isLastFmEnabled() &&
			    userSettings.getLastFmUsername().length() > 0) {
				map.put("showIconLastFM", true);
			}
		} catch (Throwable ex) {
		} 
        map.put("showIconPodcast", settingsService.showIconPodcast());
        map.put("showIconSettings", settingsService.showIconSettings());
        map.put("showIconStatus", settingsService.showIconStatus());
        map.put("showIconSocial", settingsService.showIconSocial());
        map.put("showIconHistory", settingsService.showIconHistory());
        map.put("showIconStatistics", settingsService.showIconStatistics());
        map.put("showIconPlaylists", settingsService.showIconPlaylists());
        map.put("showIconPlaylistEditor", settingsService.showIconPlaylistEditor());
        map.put("showIconMore", settingsService.showIconMore());
        map.put("showIconRandom", settingsService.showIconRandom());
        map.put("showIconGenre", settingsService.showIconGenre());
        map.put("showIconMoods", settingsService.showIconMoods());
        map.put("showIconAbout", settingsService.showIconAbout());
		
        if (user.isAdminRole()){
	        if (settingsService.showIconAdmins()){
	        	// to default
	        } else {
	            map.put("showIconHome", true);
	            map.put("showIconArtist", true);
	            map.put("showIconPlaying", true);
	            map.put("showIconCover", true);
	            map.put("showIconStarred", true);
	            map.put("showIconGenre", true);
	            map.put("showIconRadio", true);
	            map.put("showIconPodcast", true);
	            map.put("showIconSettings", true);
	            map.put("showIconStatus", true);
	            map.put("showIconSocial", true);
	            map.put("showIconStatistics", true);
	            map.put("showIconPlaylists", true);
	            map.put("showIconPlaylistEditor", true);
	            map.put("showIconMore", true);
	            map.put("showIconRandom", true);
	            map.put("showIconMoods", true);
	            map.put("showIconAbout", false);
	        }
        }
        
        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setVersionService(VersionService versionService) {
        this.versionService = versionService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
