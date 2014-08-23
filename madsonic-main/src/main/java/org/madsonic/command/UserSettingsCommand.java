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
package org.madsonic.command;

import java.util.List;

import org.madsonic.controller.*;
import org.madsonic.domain.*;

/**
 * Command used in {@link UserSettingsController}.
 *
 * @author Sindre Mehus
 */
public class UserSettingsCommand {
    private String username;
    private boolean isAdminRole;
    private boolean isDownloadRole;
    private boolean isUploadRole;
    private boolean isCoverArtRole;
    private boolean isCommentRole;
    private boolean isPodcastRole;
    private boolean isStreamRole;
    private boolean isJukeboxRole;
    private boolean isSettingsRole;
    private boolean isShareRole;
    private boolean isSearchRole;
    private boolean isLastFMRole;
    
    private boolean isLocked;
    
    private List<User> users;
    private List<Group> groups;
    
    private boolean isAdmin;
    private boolean isPasswordChange;
    private boolean isNewUser;
    private boolean isNewClone;
    private boolean isDelete;
    private String password;
    private String confirmPassword;
    private String email;
    private String comment;
    private boolean isLdapAuthenticated;
    private boolean isLdapEnabled;

	private int groupId;
    
    private String transcodeSchemeName;
    private EnumHolder[] transcodeSchemeHolders;
    private boolean transcodingSupported;
    private String transcodeDirectory;
    private boolean toast;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdminRole() {
        return isAdminRole;
    }

    public void setAdminRole(boolean adminRole) {
        isAdminRole = adminRole;
    }

    public boolean isDownloadRole() {
        return isDownloadRole;
    }

    public void setDownloadRole(boolean downloadRole) {
        isDownloadRole = downloadRole;
    }

    public boolean isUploadRole() {
        return isUploadRole;
    }

    public void setUploadRole(boolean uploadRole) {
        isUploadRole = uploadRole;
    }

    public boolean isCoverArtRole() {
        return isCoverArtRole;
    }

    public void setCoverArtRole(boolean coverArtRole) {
        isCoverArtRole = coverArtRole;
    }

    public boolean isCommentRole() {
        return isCommentRole;
    }

    public void setCommentRole(boolean commentRole) {
        isCommentRole = commentRole;
    }

    public boolean isPodcastRole() {
        return isPodcastRole;
    }

    public void setPodcastRole(boolean podcastRole) {
        isPodcastRole = podcastRole;
    }

    public boolean isStreamRole() {
        return isStreamRole;
    }

    public void setStreamRole(boolean streamRole) {
        isStreamRole = streamRole;
    }

    public boolean isJukeboxRole() {
        return isJukeboxRole;
    }

    public void setJukeboxRole(boolean jukeboxRole) {
        isJukeboxRole = jukeboxRole;
    }

    public boolean isSettingsRole() {
        return isSettingsRole;
    }

    public void setSettingsRole(boolean settingsRole) {
        isSettingsRole = settingsRole;
    }

    public boolean isShareRole() {
        return isShareRole;
    }

    public void setShareRole(boolean shareRole) {
        isShareRole = shareRole;
    }

    public boolean isSearchRole() {
        return isSearchRole;
    }

    public boolean isLastFMRole() {
		return isLastFMRole;
	}

	public void setLastFMRole(boolean LastFMRole) {
		isLastFMRole = LastFMRole;
	}

	public void setSearchRole(boolean searchRole) {
        isSearchRole = searchRole;
    }    
    
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
    
    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isPasswordChange() {
        return isPasswordChange;
    }

    public void setPasswordChange(boolean passwordChange) {
        isPasswordChange = passwordChange;
    }

    public boolean isNewUser() {
        return isNewUser;
    }

    public boolean isNewClone() {
		return isNewClone;
	}

	public void setNewClone(boolean isNewClone) {
		this.isNewClone = isNewClone;
	}

	public void setNewUser(boolean isNewUser) {
        this.isNewUser = isNewUser;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLdapAuthenticated() {
        return isLdapAuthenticated;
    }

    public void setLdapAuthenticated(boolean ldapAuthenticated) {
        isLdapAuthenticated = ldapAuthenticated;
    }

    public boolean isLdapEnabled() {
        return isLdapEnabled;
    }

    public void setLdapEnabled(boolean ldapEnabled) {
        isLdapEnabled = ldapEnabled;
    }
    
	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}	 

    public String getTranscodeSchemeName() {
        return transcodeSchemeName;
    }

    public void setTranscodeSchemeName(String transcodeSchemeName) {
        this.transcodeSchemeName = transcodeSchemeName;
    }

    public EnumHolder[] getTranscodeSchemeHolders() {
        return transcodeSchemeHolders;
    }

    public void setTranscodeSchemes(TranscodeScheme[] transcodeSchemes) {
        transcodeSchemeHolders = new EnumHolder[transcodeSchemes.length];
        for (int i = 0; i < transcodeSchemes.length; i++) {
            TranscodeScheme scheme = transcodeSchemes[i];
            transcodeSchemeHolders[i] = new EnumHolder(scheme.name(), scheme.toString());
        }
    }

    public boolean isTranscodingSupported() {
        return transcodingSupported;
    }

    public void setTranscodingSupported(boolean transcodingSupported) {
        this.transcodingSupported = transcodingSupported;
    }

    public String getTranscodeDirectory() {
        return transcodeDirectory;
    }

    public void setTranscodeDirectory(String transcodeDirectory) {
        this.transcodeDirectory = transcodeDirectory;
    }

    public void setUser(User user) {
        username = user == null ? null : user.getUsername();
        isAdminRole = user != null && user.isAdminRole();
        isDownloadRole = user != null && user.isDownloadRole();
        isUploadRole = user != null && user.isUploadRole();
        isCoverArtRole = user != null && user.isCoverArtRole();
        isCommentRole = user != null && user.isCommentRole();
        isPodcastRole = user != null && user.isPodcastRole();
        isStreamRole = user != null && user.isStreamRole();
        isJukeboxRole = user != null && user.isJukeboxRole();
        isSettingsRole = user != null && user.isSettingsRole();
        isShareRole = user != null && user.isShareRole();
        isSearchRole = user != null && user.isSearchRole();
        isLastFMRole = user != null && user.isLastFMRole();
        isLdapAuthenticated = user != null && user.isLdapAuthenticated();
        groupId = user == null ? 0 : user.getGroupId();
        isLocked = user != null && user.isLocked();
        comment = user == null ? null : user.getComment();
    }

    public void setToast(boolean toast) {
        this.toast = toast;
    }

    public boolean isToast() {
        return toast;
    }

	public boolean isLocked() {
		return isLocked;
	}

	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}