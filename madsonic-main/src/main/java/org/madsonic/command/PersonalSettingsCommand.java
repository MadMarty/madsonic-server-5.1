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

import org.madsonic.controller.PersonalSettingsController;
import org.madsonic.domain.Avatar;
import org.madsonic.domain.Theme;
import org.madsonic.domain.User;
import org.madsonic.domain.UserSettings;

import java.util.List;

/**
 * Command used in {@link PersonalSettingsController}.
 *
 * @author Sindre Mehus
 */
public class PersonalSettingsCommand {
    private User  user;
    private String localeIndex;
    private String[] locales;
    private String themeIndex;
    private Theme[] themes;
    private String profile;
    private int avatarId;
    private List<Avatar> avatars;
    private Avatar customAvatar;
    private UserSettings.Visibility mainVisibility;
    private UserSettings.Visibility playlistVisibility;
    private UserSettings.ButtonVisibility buttonVisibility;    
    private boolean partyModeEnabled;
    private boolean showNowPlayingEnabled;
    private boolean showChatEnabled;
    private boolean autoHideChatEnabled;
    private boolean nowPlayingAllowed;
    private boolean finalVersionNotificationEnabled;
    private boolean betaVersionNotificationEnabled;
    private boolean songNotificationEnabled;
    private boolean lastFmEnabled;
    private String lastFmUsername;
    private String lastFmPassword;
    private boolean isReloadNeeded;
    private String listType;
    private int listRows;
    private int listColumns; 
    private boolean playQueueResizeEnabled;
    private boolean leftFrameResizeEnabled;
	private boolean customScrollbarEnabled;
	private boolean customAccordionEnabled;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLocaleIndex() {
        return localeIndex;
    }

    public void setLocaleIndex(String localeIndex) {
        this.localeIndex = localeIndex;
    }

    public String[] getLocales() {
        return locales;
    }

    public void setLocales(String[] locales) {
        this.locales = locales;
    }

    public String getThemeIndex() {
        return themeIndex;
    }

    public void setThemeIndex(String themeIndex) {
        this.themeIndex = themeIndex;
    }

    public Theme[] getThemes() {
        return themes;
    }

    public void setThemes(Theme[] themes) {
        this.themes = themes;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(int avatarId) {
        this.avatarId = avatarId;
    }

    public List<Avatar> getAvatars() {
        return avatars;
    }

    public void setAvatars(List<Avatar> avatars) {
        this.avatars = avatars;
    }

    public Avatar getCustomAvatar() {
        return customAvatar;
    }

    public void setCustomAvatar(Avatar customAvatar) {
        this.customAvatar = customAvatar;
    }

    public UserSettings.Visibility getMainVisibility() {
        return mainVisibility;
    }

    public void setMainVisibility(UserSettings.Visibility mainVisibility) {
        this.mainVisibility = mainVisibility;
    }

    public UserSettings.Visibility getPlaylistVisibility() {
        return playlistVisibility;
    }

    public void setPlaylistVisibility(UserSettings.Visibility playlistVisibility) {
        this.playlistVisibility = playlistVisibility;
    }

    public UserSettings.ButtonVisibility getButtonVisibility() {
		return buttonVisibility;
	}

	public void setButtonVisibility(UserSettings.ButtonVisibility buttonVisibility) {
		this.buttonVisibility = buttonVisibility;
	}

	public boolean isPartyModeEnabled() {
        return partyModeEnabled;
    }

    public void setPartyModeEnabled(boolean partyModeEnabled) {
        this.partyModeEnabled = partyModeEnabled;
    }

    public boolean isShowNowPlayingEnabled() {
        return showNowPlayingEnabled;
    }

    public void setShowNowPlayingEnabled(boolean showNowPlayingEnabled) {
        this.showNowPlayingEnabled = showNowPlayingEnabled;
    }

    public boolean isShowChatEnabled() {
        return showChatEnabled;
    }

    public void setShowChatEnabled(boolean showChatEnabled) {
        this.showChatEnabled = showChatEnabled;
    }

    public boolean isNowPlayingAllowed() {
        return nowPlayingAllowed;
    }

    public void setNowPlayingAllowed(boolean nowPlayingAllowed) {
        this.nowPlayingAllowed = nowPlayingAllowed;
    }

    public boolean isFinalVersionNotificationEnabled() {
        return finalVersionNotificationEnabled;
    }

    public void setFinalVersionNotificationEnabled(boolean finalVersionNotificationEnabled) {
        this.finalVersionNotificationEnabled = finalVersionNotificationEnabled;
    }

    public boolean isBetaVersionNotificationEnabled() {
        return betaVersionNotificationEnabled;
    }

    public void setBetaVersionNotificationEnabled(boolean betaVersionNotificationEnabled) {
        this.betaVersionNotificationEnabled = betaVersionNotificationEnabled;
    }

    public void setSongNotificationEnabled(boolean songNotificationEnabled) {
        this.songNotificationEnabled = songNotificationEnabled;
    }

    public boolean isSongNotificationEnabled() {
        return songNotificationEnabled;
    }

    public boolean isLastFmEnabled() {
        return lastFmEnabled;
    }

    public void setLastFmEnabled(boolean lastFmEnabled) {
        this.lastFmEnabled = lastFmEnabled;
    }

    public String getLastFmUsername() {
        return lastFmUsername;
    }

    public void setLastFmUsername(String lastFmUsername) {
        this.lastFmUsername = lastFmUsername;
    }

    public String getLastFmPassword() {
        return lastFmPassword;
    }

    public void setLastFmPassword(String lastFmPassword) {
        this.lastFmPassword = lastFmPassword;
    }

    public boolean isReloadNeeded() {
        return isReloadNeeded;
    }

    public void setReloadNeeded(boolean reloadNeeded) {
        isReloadNeeded = reloadNeeded;
    }

    public String getListType() {
        return listType;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }

    public int getListRows() {
        return listRows;
    }

    public void setListRows(int listRows) {
        this.listRows = listRows;
    }

    public void setListColumns(int listColumns) {
        this.listColumns = listColumns;
    }

	public int getListColumns() {
        return listColumns;
	}

    public void setPlayQueueResize(boolean PlayQueueResize) {
        this.playQueueResizeEnabled = PlayQueueResize;
    }

	public boolean getPlayQueueResize() {
        return playQueueResizeEnabled;
	}
	
	 public void setPlayQueueResizeEnabled(boolean playQueueResizeEnabled) {
		this.playQueueResizeEnabled = playQueueResizeEnabled;
	 }

	 public boolean getPlayQueueResizeEnabled() {
        return playQueueResizeEnabled;
	 }

	public boolean getLeftFrameResize() {
        return leftFrameResizeEnabled;
	}

	 public void setleftFrameResize(boolean leftFrameResizeEnabled) {
		this.leftFrameResizeEnabled = leftFrameResizeEnabled;
	 }
	
	
	 public void setleftFrameResizeEnabled(boolean leftFrameResizeEnabled) {
		this.leftFrameResizeEnabled = leftFrameResizeEnabled;
	 }

	 public boolean getleftFrameResizeEnabled() {
        return leftFrameResizeEnabled;
	 }

	/**
	 * @return the customScrollbarEnabled
	 */
	public boolean isCustomScrollbarEnabled() {
		return customScrollbarEnabled;
	}

	/**
	 * @param customScrollbarEnabled the customScrollbarEnabled to set
	 */
	public void setCustomScrollbarEnabled(boolean customScrollbarEnabled) {
		this.customScrollbarEnabled = customScrollbarEnabled;
	}

	public boolean isCustomAccordionEnabled() {
		return customAccordionEnabled;
	}

	public void setCustomAccordionEnabled(boolean customAccordionEnabled) {
		this.customAccordionEnabled = customAccordionEnabled;
	}

	/**
	 * @return the autoHideChatEnabled
	 */
	public boolean isAutoHideChatEnabled() {
		return autoHideChatEnabled;
	}

	/**
	 * @param autoHideChatEnabled the autoHideChatEnabled to set
	 */
	public void setAutoHideChatEnabled(boolean autoHideChatEnabled) {
		this.autoHideChatEnabled = autoHideChatEnabled;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}
	
}
