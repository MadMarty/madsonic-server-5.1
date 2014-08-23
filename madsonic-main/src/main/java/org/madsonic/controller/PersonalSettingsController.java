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
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.madsonic.command.PersonalSettingsCommand;
import org.madsonic.domain.AvatarScheme;
import org.madsonic.domain.Theme;
import org.madsonic.domain.User;
import org.madsonic.domain.UserSettings;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;


/**
 * Controller for the page used to administrate per-user settings.
 *
 * @author Sindre Mehus
 */
public class PersonalSettingsController extends SimpleFormController {

    private SettingsService settingsService;
    private SecurityService securityService;
 
    @Override
    protected Map referenceData(HttpServletRequest request) throws Exception {
    Map<String, Object> model = new HashMap<String, Object>();
    
    User user = securityService.getCurrentUser(request);
    UserSettings userSettings = settingsService.getUserSettings(user.getUsername());

    model.put("customScrollbar", userSettings.isCustomScrollbarEnabled());    
    
    return model;
    }    

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        ModelAndView result = super.handleRequestInternal(request, response);
        
		if (request.getRequestURI().toLowerCase().contains("/profileSettings.view".toLowerCase())) {
            result.setViewName("profileSettings");
		}        
		if (request.getRequestURI().toLowerCase().contains("/defaultSettings.view".toLowerCase())) {
            result.setViewName("defaultSettings");
		}
		if (request.getRequestURI().toLowerCase().contains("/personalSettings.view".toLowerCase())) {
            result.setViewName("personalSettings");
		}
        result.addObject("model", map);
        return result;
    }
    
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        PersonalSettingsCommand command = new PersonalSettingsCommand();

        User user = securityService.getCurrentUser(request);
        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());

		if (request.getRequestURI().toLowerCase().contains("/profileSettings.view".toLowerCase())) {
			String username = request.getParameter("profile");
			if (username != null){
				user = securityService.getUserByName(username);
		        userSettings = settingsService.getUserSettings(username);			
			}
        }
		if (request.getRequestURI().toLowerCase().contains("/defaultSettings.view".toLowerCase())) {
	        user = securityService.getUserByName("default");
	        userSettings = settingsService.getUserSettings("default");		        
        }
		
		command.setUser(user);
        command.setLocaleIndex("-1");
        command.setThemeIndex("-1");

        command.setProfile(user.getUsername());
        command.setAvatars(settingsService.getAllSystemAvatars());
        command.setCustomAvatar(settingsService.getCustomAvatar(user.getUsername()));
        command.setAvatarId(getAvatarId(userSettings));
        command.setPartyModeEnabled(userSettings.isPartyModeEnabled());
        command.setShowNowPlayingEnabled(userSettings.isShowNowPlayingEnabled());
        command.setShowChatEnabled(userSettings.isShowChatEnabled());
        command.setAutoHideChatEnabled(userSettings.isAutoHideChat());
        command.setNowPlayingAllowed(userSettings.isNowPlayingAllowed());
        command.setMainVisibility(userSettings.getMainVisibility());
        command.setPlaylistVisibility(userSettings.getPlaylistVisibility());
        command.setButtonVisibility(userSettings.getButtonVisibility());
        command.setFinalVersionNotificationEnabled(userSettings.isFinalVersionNotificationEnabled());
        command.setBetaVersionNotificationEnabled(userSettings.isBetaVersionNotificationEnabled());
        command.setSongNotificationEnabled(userSettings.isSongNotificationEnabled());
        command.setLastFmEnabled(userSettings.isLastFmEnabled());
        command.setLastFmUsername(userSettings.getLastFmUsername());
        command.setLastFmPassword(userSettings.getLastFmPassword());
        command.setListType(userSettings.getListType());
        command.setListRows(userSettings.getListRows());
        command.setListColumns(userSettings.getListColumns());
        command.setPlayQueueResize(userSettings.getPlayQueueResize());
        command.setleftFrameResize(userSettings.getLeftFrameResize());
        command.setCustomScrollbarEnabled(userSettings.isCustomScrollbarEnabled());
        command.setCustomAccordionEnabled(userSettings.isCustomAccordionEnabled());
        
       
        Locale currentLocale = userSettings.getLocale();
        Locale[] locales = settingsService.getAvailableLocales();
        String[] localeStrings = new String[locales.length];
        for (int i = 0; i < locales.length; i++) {
            localeStrings[i] = locales[i].getDisplayName(locales[i]);
            if (locales[i].equals(currentLocale)) {
                command.setLocaleIndex(String.valueOf(i));
            }
        }
        command.setLocales(localeStrings);

        String currentThemeId = userSettings.getThemeId();
        Theme[] themes = settingsService.getAvailableThemes();
        command.setThemes(themes);
        for (int i = 0; i < themes.length; i++) {
            if (themes[i].getId().equals(currentThemeId)) {
                command.setThemeIndex(String.valueOf(i));
                break;
            }
        }
        return command;
    }

    @Override
    protected void doSubmitAction(Object comm) throws Exception {
        PersonalSettingsCommand command = (PersonalSettingsCommand) comm;

        int localeIndex = Integer.parseInt(command.getLocaleIndex());
        Locale locale = null;
        if (localeIndex != -1) {
            locale = settingsService.getAvailableLocales()[localeIndex];
        }

        int themeIndex = Integer.parseInt(command.getThemeIndex());
        String themeId = null;
        if (themeIndex != -1) {
            themeId = settingsService.getAvailableThemes()[themeIndex].getId();
        }

		String username = command.getProfile();
        UserSettings settings = settingsService.getUserSettings(username);

        settings.setLocale(locale);
        settings.setThemeId(themeId);
        settings.setPartyModeEnabled(command.isPartyModeEnabled());
        settings.setShowNowPlayingEnabled(command.isShowNowPlayingEnabled());
        settings.setShowChatEnabled(command.isShowChatEnabled());
        settings.setAutoHideChat(command.isAutoHideChatEnabled());
        settings.setNowPlayingAllowed(command.isNowPlayingAllowed());
        settings.setMainVisibility(command.getMainVisibility());
        settings.setPlaylistVisibility(command.getPlaylistVisibility());
        settings.setButtonVisibility(command.getButtonVisibility());
        settings.setFinalVersionNotificationEnabled(command.isFinalVersionNotificationEnabled());
        settings.setBetaVersionNotificationEnabled(command.isBetaVersionNotificationEnabled());
        settings.setSongNotificationEnabled(command.isSongNotificationEnabled());
        settings.setLastFmEnabled(command.isLastFmEnabled());
        settings.setLastFmUsername(command.getLastFmUsername());
        settings.setSystemAvatarId(getSystemAvatarId(command));
        settings.setAvatarScheme(getAvatarScheme(command));
        settings.setListType(command.getListType());
        settings.setListRows(command.getListRows());
        settings.setListColumns(command.getListColumns());
        settings.setPlayQueueResize(command.getPlayQueueResize());
        settings.setLeftFrameResize(command.getLeftFrameResize());
        settings.setCustomScrollbarEnabled(command.isCustomScrollbarEnabled());
        settings.setCustomAccordionEnabled(command.isCustomAccordionEnabled());

// old workaround        
//        if (settings.isCustomAccordionEnabled() == true){
//        	settings.setCustomScrollbarEnabled(false);
//        }        
        
        if (StringUtils.isNotBlank(command.getLastFmPassword())) {
            settings.setLastFmPassword(command.getLastFmPassword());
        }

        settings.setChanged(new Date());
        settingsService.updateUserSettings(settings);

        command.setReloadNeeded(true);
    }

    private int getAvatarId(UserSettings userSettings) {
        AvatarScheme avatarScheme = userSettings.getAvatarScheme();
        return avatarScheme == AvatarScheme.SYSTEM ? userSettings.getSystemAvatarId() : avatarScheme.getCode();
    }

    private AvatarScheme getAvatarScheme(PersonalSettingsCommand command) {
        if (command.getAvatarId() == AvatarScheme.NONE.getCode()) {
            return AvatarScheme.NONE;
        }
        if (command.getAvatarId() == AvatarScheme.CUSTOM.getCode()) {
            return AvatarScheme.CUSTOM;
        }
        return AvatarScheme.SYSTEM;
    }

    private Integer getSystemAvatarId(PersonalSettingsCommand command) {
        int avatarId = command.getAvatarId();
        if (avatarId == AvatarScheme.NONE.getCode() ||
            avatarId == AvatarScheme.CUSTOM.getCode()) {
            return null;
        }
        return avatarId;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
