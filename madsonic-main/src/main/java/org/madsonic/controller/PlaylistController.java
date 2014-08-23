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

import org.madsonic.domain.Playlist;
import org.madsonic.domain.User;
import org.madsonic.domain.UserSettings;
import org.madsonic.service.PlaylistService;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the main page.
 *
 * @author Sindre Mehus
 */
public class PlaylistController extends ParameterizableViewController {

    private SecurityService securityService;
    private PlaylistService playlistService;
    private SettingsService settingsService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        int id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        User user = securityService.getCurrentUser(request);
        String username = user.getUsername();
        UserSettings userSettings = settingsService.getUserSettings(username);
        
        Playlist playlist = playlistService.getPlaylist(id);
        if (playlist == null) {
            return new ModelAndView(new RedirectView("notFound.view"));
        }
        
        map.put("playlist", playlist);
        map.put("user", user);
        map.put("editAllowed", username.equals(playlist.getUsername()) || securityService.isAdmin(username));
        map.put("partyMode", userSettings.isPartyModeEnabled());

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
