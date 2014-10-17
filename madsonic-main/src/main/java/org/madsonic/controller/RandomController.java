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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import org.madsonic.domain.MusicFolder;
import org.madsonic.domain.Player;
import org.madsonic.service.MediaFileService;
import org.madsonic.service.PlayerService;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;

/**
 * Controller for the "more" page.
 *
 * @author Sindre Mehus
 */
public class RandomController extends ParameterizableViewController {

    private SettingsService settingsService;
    private SecurityService securityService;
    private PlayerService playerService;
    private MediaFileService mediaFileService;
	
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        Player player = playerService.getPlayer(request, response);
        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        
        map.put("user", securityService.getCurrentUser(request));
        map.put("genres", mediaFileService.getGenres(false));
        map.put("moods", mediaFileService.getMoods());
        
        map.put("currentYear", Calendar.getInstance().get(Calendar.YEAR));
        map.put("musicFolders", settingsService.getAllMusicFolders());
        map.put("clientSidePlaylist", player.isExternalWithPlaylist() || player.isWeb());
        map.put("brand", settingsService.getBrand());
        
        return result;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
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
}
