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

import java.util.HashMap;
import java.util.Map;

import org.madsonic.command.AdvancedSettingsCommand;
import org.madsonic.domain.User;
import org.madsonic.domain.UserSettings;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for the page used to administrate advanced settings.
 *
 * @author Sindre Mehus
 */
public class AdvancedSettingsController extends SimpleFormController {

    private SettingsService settingsService;
    private SecurityService securityService;    

    @Override
    protected Map referenceData(HttpServletRequest request) throws Exception {
    Map<String, Boolean> model = new HashMap<String, Boolean>();
    
    User user = securityService.getCurrentUser(request);
    UserSettings userSettings = settingsService.getUserSettings(user.getUsername());

    model.put("customScrollbar", userSettings.isCustomScrollbarEnabled());     
    return model;
    }      
    
    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        AdvancedSettingsCommand command = new AdvancedSettingsCommand();
        command.setCoverArtLimit(String.valueOf(settingsService.getCoverArtLimit()));
        command.setDownloadLimit(String.valueOf(settingsService.getDownloadBitrateLimit()));
        command.setUploadLimit(String.valueOf(settingsService.getUploadBitrateLimit()));
        command.setStreamPort(String.valueOf(settingsService.getStreamPort()));
        command.setLdapEnabled(settingsService.isLdapEnabled());
        command.setLdapUrl(settingsService.getLdapUrl());
        command.setLdapSearchFilter(settingsService.getLdapSearchFilter());
        command.setLdapManagerDn(settingsService.getLdapManagerDn());
        command.setLdapAutoShadowing(settingsService.isLdapAutoShadowing());
        command.setBrand(settingsService.getBrand());

        return command;
    }

    @Override
    protected void doSubmitAction(Object comm) throws Exception {
        AdvancedSettingsCommand command = (AdvancedSettingsCommand) comm;

        command.setToast(true);
        command.setReloadNeeded(false);

        try {
            settingsService.setCoverArtLimit(Integer.parseInt(command.getCoverArtLimit()));
        } catch (NumberFormatException x) { /* Intentionally ignored. */ }
        try {
            settingsService.setDownloadBitrateLimit(Long.parseLong(command.getDownloadLimit()));
        } catch (NumberFormatException x) { /* Intentionally ignored. */ }
        try {
            settingsService.setUploadBitrateLimit(Long.parseLong(command.getUploadLimit()));
        } catch (NumberFormatException x) { /* Intentionally ignored. */ }
        try {
            settingsService.setStreamPort(Integer.parseInt(command.getStreamPort()));
        } catch (NumberFormatException x) { /* Intentionally ignored. */ }

        settingsService.setLdapEnabled(command.isLdapEnabled());
        settingsService.setLdapUrl(command.getLdapUrl());
        settingsService.setLdapSearchFilter(command.getLdapSearchFilter());
        settingsService.setLdapManagerDn(command.getLdapManagerDn());
        settingsService.setLdapAutoShadowing(command.isLdapAutoShadowing());

        if (StringUtils.isNotEmpty(command.getLdapManagerPassword())) {
            settingsService.setLdapManagerPassword(command.getLdapManagerPassword());
        }

        settingsService.save();
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
    
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }    
       
}
