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
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import org.madsonic.util.StringUtil;

import org.madsonic.service.*;
import org.madsonic.domain.*;
import org.madsonic.command.*;
import org.springframework.web.servlet.mvc.*;
import org.springframework.web.bind.*;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.log.Log;

import javax.servlet.http.*;

/**
 * Controller for the page used to administrate users.
 *
 * @author Sindre Mehus
 */
public class UserSettingsController extends SimpleFormController {

    private SecurityService securityService;
    private SettingsService settingsService;
    private TranscodingService transcodingService;

    @Override
    protected Map<String, Boolean> referenceData(HttpServletRequest request) throws Exception {
    Map<String, Boolean> model = new HashMap<String, Boolean>();
    
    User activeUser = securityService.getCurrentUser(request);
    UserSettings activeUserSettings = settingsService.getUserSettings(activeUser.getUsername());

    model.put("customScrollbar", activeUserSettings.isCustomScrollbarEnabled());     
    return model;
    }     
    
    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        UserSettingsCommand command = new UserSettingsCommand();

        User user = getUser(request);
        if (user == null) {
    		if (request.getRequestURI().toLowerCase().contains("/defaultSettings.view".toLowerCase())) {
	        	user = securityService.getUserByName("default");
    		}
    		if (request.getRequestURI().toLowerCase().contains("/userSettings.view".toLowerCase())) {
	        	user = null;
    		}
        }
        String usrAct = ServletRequestUtils.getStringParameter(request, "usrAct");

        // ### Edit ###
        if (usrAct == null || usrAct.contains("edit") ) {
            if (user != null) {
                command.setUser(user);
                command.setEmail(user.getEmail());
                command.setAdmin(User.USERNAME_ADMIN.equals(user.getUsername()));
    			UserSettings userSettings = settingsService.getUserSettings(user.getUsername());
    			command.setTranscodeSchemeName(userSettings.getTranscodeScheme().name());
    			command.setGroupId(user.getGroupId());
    	        command.setLocked(user.isLocked());
    	        command.setComment(user.getComment());
            } 
        } 
        
    	// ### New ###
        else if (usrAct.contains("new")) {
        	command.setNewUser(true);
            command.setStreamRole(true);
            command.setSettingsRole(true);
            command.setSearchRole(false);
            command.setCommentRole(true); 
        } 
        
    	// ### Clone ###
        else if (usrAct.contains("clone")) {
            User defaultUser = securityService.getUserByName(User.USERNAME_DEFAULT);
            Random random = new Random(System.currentTimeMillis());
        	command.setUsername("ClonedUser" + (String.valueOf(random.nextInt()).substring(0, 5)));
        	command.setNewUser(true);
        	command.setNewClone(true);
            command.setStreamRole(defaultUser.isStreamRole());
            command.setSettingsRole(defaultUser.isSettingsRole());
            command.setSearchRole(defaultUser.isSearchRole());
            command.setCommentRole(defaultUser.isCommentRole());
            command.setCoverArtRole(defaultUser.isCoverArtRole());
            command.setLocked(defaultUser.isLocked());
            command.setAdminRole(defaultUser.isAdminRole());
            command.setStreamRole(defaultUser.isStreamRole());
            command.setUploadRole(defaultUser.isUploadRole());
            command.setShareRole(defaultUser.isShareRole());
            command.setPodcastRole(defaultUser.isPodcastRole());
            command.setJukeboxRole(defaultUser.isJukeboxRole());
            command.setDownloadRole(defaultUser.isDownloadRole());
            command.setLastFMRole(defaultUser.isLastFMRole());
            command.setGroupId(defaultUser.getGroupId());
            command.setComment(defaultUser.getComment());
        }
		
        command.setUsers(securityService.getAllUsers());
        command.setGroups(securityService.GetAllGroups());
        
        command.setTranscodingSupported(transcodingService.isDownsamplingSupported(null));
        command.setTranscodeDirectory(transcodingService.getTranscodeDirectory().getPath());
        command.setTranscodeSchemes(TranscodeScheme.values());
        command.setLdapEnabled(settingsService.isLdapEnabled());
        
        return command;
    }

    private User getUser(HttpServletRequest request) throws ServletRequestBindingException {
        Integer userIndex = ServletRequestUtils.getIntParameter(request, "userIndex");
        if (userIndex != null) {
            List<User> allUsers = securityService.getAllUsers();
            if (userIndex >= 0 && userIndex < allUsers.size()) {
                return allUsers.get(userIndex);
            }
        }
        return null;
    }

    @Override
    protected void doSubmitAction(Object comm) throws Exception {
        UserSettingsCommand command = (UserSettingsCommand) comm;

        if (command.isDelete()) {
            deleteUser(command);
        } else if (command.isNewUser()) {
        	if (command.isNewClone()) {
                cloneUser(command);
                cloneUserSettings(command);
        	} else {
	            createUser(command);
        	}
        } else {
            updateUser(command);
        }
        resetCommand(command);
        command.setToast(true);
    }

    private void deleteUser(UserSettingsCommand command) {
        securityService.deleteUser(command.getUsername());
    }
     
    public void createUser(UserSettingsCommand command) {
        User user = new User(command.getUsername(), command.getPassword(), StringUtils.trimToNull(command.getEmail()), false, 0, 0, 0, command.getGroupId(), command.isLocked(),command.getComment());
        user.setLdapAuthenticated(command.isLdapAuthenticated());
		user.setGroupId(command.getGroupId());
        securityService.setSecurePassword(user);
        securityService.createUser(user);
        updateUser(command);
    }

    public void cloneUser(UserSettingsCommand command) {
        User user = new User(command.getUsername(), command.getPassword(), StringUtils.trimToNull(command.getEmail()), false, 0, 0, 0, command.getGroupId(), command.isLocked(), command.getComment());
        securityService.cloneUser(user);
        securityService.setSecurePassword(user);
        securityService.updateUser(user);
        updateUser(command);
    }
    
    private void cloneUserSettings(UserSettingsCommand command){
        UserSettings userSettings = settingsService.getDefaultUserSettings(command.getUsername());
        userSettings.setTranscodeScheme(TranscodeScheme.valueOf(command.getTranscodeSchemeName()));
        userSettings.setChanged(new Date());
        settingsService.updateUserSettings(userSettings);    	
    }
    
    public void updateUser(UserSettingsCommand command) {
        User user = securityService.getUserByName(command.getUsername());
        if (user == null) {
        	user = securityService.getUserByName("default");
        }
        user.setAdminRole(command.isAdminRole());
        user.setDownloadRole(command.isDownloadRole());
        user.setUploadRole(command.isUploadRole());
        user.setCoverArtRole(command.isCoverArtRole());
        user.setCommentRole(command.isCommentRole());
        user.setPodcastRole(command.isPodcastRole());
        user.setStreamRole(command.isStreamRole());
        user.setJukeboxRole(command.isJukeboxRole());
        user.setSettingsRole(command.isSettingsRole());
        user.setShareRole(command.isShareRole());
        user.setSearchRole(command.isSearchRole());
        user.setLastFMRole(command.isLastFMRole());
        user.setGroupId(command.getGroupId());
        user.setLocked(command.isLocked());
        user.setComment(command.getComment());

        if (command.isPasswordChange()) {
            user.setPassword(command.getPassword());
            securityService.setSecurePassword(user);
        }
        securityService.updateUser(user);
        UserSettings userSettings = new UserSettings(null);  
        
        if (command.getUsername() == null) {
        	userSettings = settingsService.getUserSettings("default");
        } else {
        	userSettings = settingsService.getUserSettings(command.getUsername());
        }
        userSettings.setTranscodeScheme(TranscodeScheme.valueOf(command.getTranscodeSchemeName()));
        
        userSettings.setChanged(new Date());
        settingsService.updateUserSettings(userSettings);
    }

    private void resetCommand(UserSettingsCommand command) {
        command.setUser(null);
        command.setUsers(securityService.getAllUsers());
        command.setDelete(false);
        command.setPasswordChange(false);
        command.setNewUser(false);
        command.setStreamRole(true);
        command.setSettingsRole(true);
        command.setPassword(null);
        command.setConfirmPassword(null);
        command.setEmail(null);
        command.setTranscodeSchemeName(null);
        command.setGroupId(0);
        command.setLocked(false);
        command.setComment(null);
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }
}
