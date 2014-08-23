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
package org.madsonic.domain;

import java.util.*;

/**
 * Represent user-specific settings.
 *
 * @author Sindre Mehus
 */
public class UserSettings {

    private String username;
    private Locale locale;
    private String themeId;
    private boolean showNowPlayingEnabled;
    private boolean showChatEnabled;
    private boolean autoHideChat = false; 
    private boolean finalVersionNotificationEnabled;
    private boolean betaVersionNotificationEnabled;
    private boolean songNotificationEnabled;
    private Visibility mainVisibility = new Visibility();
    private Visibility playlistVisibility = new Visibility();
    private ButtonVisibility buttonVisibility = new ButtonVisibility();
    
    private boolean lastFmEnabled;
    private String lastFmUsername;
    private String lastFmPassword;
    private TranscodeScheme transcodeScheme = TranscodeScheme.OFF;
    private int selectedMusicFolderId = -1;
    private String selectedGenre;
    private boolean partyModeEnabled;
    private boolean nowPlayingAllowed;
    private AvatarScheme avatarScheme = AvatarScheme.NONE;
    private Integer systemAvatarId;
    private Date changed = new Date();
    private String listType = "random";
    private int listRows = 2;
    private int listColumns = 5;
    private boolean PlayQueueResize = false;
    private boolean LeftFrameResize = false;
    private boolean CustomScrollbarEnabled = true;  
    private boolean CustomAccordionEnabled = false;  
    
    public UserSettings(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getThemeId() {
        return themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId = themeId;
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

    public boolean isSongNotificationEnabled() {
        return songNotificationEnabled;
    }

    public void setSongNotificationEnabled(boolean songNotificationEnabled) {
        this.songNotificationEnabled = songNotificationEnabled;
    }

    public Visibility getMainVisibility() {
        return mainVisibility;
    }

    public void setMainVisibility(Visibility mainVisibility) {
        this.mainVisibility = mainVisibility;
    }

    public Visibility getPlaylistVisibility() {
        return playlistVisibility;
    }

    public void setPlaylistVisibility(Visibility playlistVisibility) {
        this.playlistVisibility = playlistVisibility;
    }

    public ButtonVisibility getButtonVisibility() {
		return buttonVisibility;
	}

	public void setButtonVisibility(ButtonVisibility buttonVisibility) {
		this.buttonVisibility = buttonVisibility;
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

    public TranscodeScheme getTranscodeScheme() {
        return transcodeScheme;
    }

    public void setTranscodeScheme(TranscodeScheme transcodeScheme) {
        this.transcodeScheme = transcodeScheme;
    }

    public int getSelectedMusicFolderId() {
        return selectedMusicFolderId;
    }

    public void setSelectedMusicFolderId(int selectedMusicFolderId) {
        this.selectedMusicFolderId = selectedMusicFolderId;
    }

    public boolean isPartyModeEnabled() {
        return partyModeEnabled;
    }

    public void setPartyModeEnabled(boolean partyModeEnabled) {
        this.partyModeEnabled = partyModeEnabled;
    }

    public boolean isNowPlayingAllowed() {
        return nowPlayingAllowed;
    }

    public void setNowPlayingAllowed(boolean nowPlayingAllowed) {
        this.nowPlayingAllowed = nowPlayingAllowed;
    }

    public AvatarScheme getAvatarScheme() {
        return avatarScheme;
    }

    public void setAvatarScheme(AvatarScheme avatarScheme) {
        this.avatarScheme = avatarScheme;
    }

    public Integer getSystemAvatarId() {
        return systemAvatarId;
    }

    public void setSystemAvatarId(Integer systemAvatarId) {
        this.systemAvatarId = systemAvatarId;
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

    public int getListColumns() {
        return listColumns;
    }

    public void setListColumns(int listColumns) {
        this.listColumns = listColumns;
    }

	public boolean isPlayQueueResizeEnabled() {
		return PlayQueueResize;
	}    
    
	public boolean getPlayQueueResize() {
		return PlayQueueResize;
	}   	
	
	public void setPlayQueueResize(boolean PlayQueueResize) {
		this.PlayQueueResize = PlayQueueResize;
	}    

	public boolean isLeftFrameResizeEnabled() {
		return LeftFrameResize;
	}   

	public boolean getLeftFrameResize() {
		return LeftFrameResize;
	}   	
	

	public void setLeftFrameResize(boolean LeftFrameResize) {
		this.LeftFrameResize = LeftFrameResize;
	}    
    	
	/**
	 * @return the customScrollbarEnabled
	 */
	public boolean isCustomScrollbarEnabled() {
		return CustomScrollbarEnabled;
	}

	/**
	 * @param customScrollbarEnabled the customScrollbarEnabled to set
	 */
	public void setCustomScrollbarEnabled(boolean customScrollbarEnabled) {
		this.CustomScrollbarEnabled = customScrollbarEnabled;
	}

	public boolean isCustomAccordionEnabled() {
		return CustomAccordionEnabled;
	}

	public void setCustomAccordionEnabled(boolean customAccordionEnabled) {
		CustomAccordionEnabled = customAccordionEnabled;
	}

	/**
     * Returns when the corresponding database entry was last changed.
     *
     * @return When the corresponding database entry was last changed.
     */
    public Date getChanged() {
        return changed;
    }

    /**
     * Sets when the corresponding database entry was last changed.
     *
     * @param changed When the corresponding database entry was last changed.
     */
    public void setChanged(Date changed) {
        this.changed = changed;
    }

    /**
	 * @return the autoHideChat
	 */
	public boolean isAutoHideChat() {
		return autoHideChat;
	}

	/**
	 * @param autoHideChat the autoHideChat to set
	 */
	public void setAutoHideChat(boolean autoHideChat) {
		this.autoHideChat = autoHideChat;
	}

	public String getSelectedGenre() {
		return selectedGenre;
	}

	public void setSelectedGenre(String selectedGenre) {
		this.selectedGenre = selectedGenre;
	}

    public static class ButtonVisibility {
        private boolean isRankVisible;
        private boolean isStarredVisible;
        private boolean isPlayVisible;
        private boolean isPlayAddVisible;
        private boolean isPlayMoreVisible;
        private boolean isAddContextVisible;
        private boolean isAddNextVisible;
        private boolean isAddLastVisible;
        private boolean isDownloadVisible;
        private boolean isYoutubeVisible;

        public ButtonVisibility() {}
        
        public ButtonVisibility(boolean rankVisible, boolean starredVisible, boolean playVisible, boolean playAddVisible, 
        						boolean playMoreVisible, boolean addContextVisible, boolean addNextVisible, 
        						boolean addLastVisible, boolean downloadVisible, boolean youtubeVisible) {

        	isRankVisible = rankVisible;
            isStarredVisible = starredVisible;
            isPlayVisible = playVisible;
            isPlayAddVisible = playAddVisible;
            isPlayMoreVisible = playMoreVisible;
            isAddContextVisible = addContextVisible;
            isAddNextVisible = addNextVisible;
            isAddLastVisible = addLastVisible;
            isDownloadVisible = downloadVisible;
            isYoutubeVisible = youtubeVisible;
        }

		public boolean isRankVisible() {
			return isRankVisible;
		}

		public void setRankVisible(boolean RankVisible) {
			isRankVisible = RankVisible;
		}

		public boolean isStarredVisible() {
			return isStarredVisible;
		}

		public void setStarredVisible(boolean StarredVisible) {
			isStarredVisible = StarredVisible;
		}

		public boolean isPlayVisible() {
			return isPlayVisible;
		}

		public void setPlayVisible(boolean PlayVisible) {
			isPlayVisible = PlayVisible;
		}

		public boolean isPlayAddVisible() {
			return isPlayAddVisible;
		}

		public void setPlayAddVisible(boolean PlayAddVisible) {
			isPlayAddVisible = PlayAddVisible;
		}

		public boolean isPlayMoreVisible() {
			return isPlayMoreVisible;
		}

		public void setPlayMoreVisible(boolean PlayMoreVisible) {
			isPlayMoreVisible = PlayMoreVisible;
		}

		public boolean isAddContextVisible() {
			return isAddContextVisible;
		}

		public void setAddContextVisible(boolean AddContextVisible) {
			isAddContextVisible = AddContextVisible;
		}

		public boolean isAddNextVisible() {
			return isAddNextVisible;
		}

		public void setAddNextVisible(boolean AddNextVisible) {
			isAddNextVisible = AddNextVisible;
		}

		public boolean isAddLastVisible() {
			return isAddLastVisible;
		}

		public void setAddLastVisible(boolean AddLastVisible) {
			isAddLastVisible = AddLastVisible;
		}

		public boolean isDownloadVisible() {
			return isDownloadVisible;
		}

		public void setDownloadVisible(boolean DownloadVisible) {
			isDownloadVisible = DownloadVisible;
		}

		public boolean isYoutubeVisible() {
			return isYoutubeVisible;
		}

		public void setYoutubeVisible(boolean YoutubeVisible) {
			isYoutubeVisible = YoutubeVisible;
		}
    }
	
	
	/**
     * Configuration of what information to display about a song.
     */
    public static class Visibility {
        private int captionCutoff;
        private boolean isDiscNumberVisible;
        private boolean isTrackNumberVisible;
        private boolean isArtistVisible;
        private boolean isAlbumVisible;
        private boolean isGenreVisible;
        private boolean isMoodVisible;        
        private boolean isYearVisible;
        private boolean isBitRateVisible;
        private boolean isDurationVisible;
        private boolean isFormatVisible;
        private boolean isFileSizeVisible;

        public Visibility() {}

        public Visibility(int captionCutoffvalue, boolean discNumberVisible, boolean trackNumberVisible, boolean artistVisible, boolean albumVisible,
                          boolean genreVisible, boolean moodVisible, boolean yearVisible, boolean bitRateVisible,
                          boolean durationVisible, boolean formatVisible, boolean fileSizeVisible) {
        	captionCutoff = captionCutoffvalue;
            isDiscNumberVisible = discNumberVisible;
            isTrackNumberVisible = trackNumberVisible;
            isArtistVisible = artistVisible;
            isAlbumVisible = albumVisible;
            isGenreVisible = genreVisible;
            isMoodVisible = moodVisible;            
            isYearVisible = yearVisible;
            isBitRateVisible = bitRateVisible;
            isDurationVisible = durationVisible;
            isFormatVisible = formatVisible;
            isFileSizeVisible = fileSizeVisible;
        }

        public int getCaptionCutoff() {
            return captionCutoff;
        }

        public void setCaptionCutoff(int value) {
            captionCutoff = value;
        }

        public boolean isDiscNumberVisible() {
            return isDiscNumberVisible;
        }

        public void setDiscNumberVisible(boolean discNumberVisible) {
            isDiscNumberVisible = discNumberVisible;
        }        
        
        public boolean isTrackNumberVisible() {
            return isTrackNumberVisible;
        }

        public void setTrackNumberVisible(boolean trackNumberVisible) {
            isTrackNumberVisible = trackNumberVisible;
        }

        public boolean isArtistVisible() {
            return isArtistVisible;
        }

        public void setArtistVisible(boolean artistVisible) {
            isArtistVisible = artistVisible;
        }

        public boolean isAlbumVisible() {
            return isAlbumVisible;
        }

        public void setAlbumVisible(boolean albumVisible) {
            isAlbumVisible = albumVisible;
        }

        public boolean isGenreVisible() {
            return isGenreVisible;
        }

        public void setGenreVisible(boolean genreVisible) {
            isGenreVisible = genreVisible;
        }

        public boolean isYearVisible() {
            return isYearVisible;
        }

        public void setYearVisible(boolean yearVisible) {
            isYearVisible = yearVisible;
        }

        public boolean isBitRateVisible() {
            return isBitRateVisible;
        }

        public void setBitRateVisible(boolean bitRateVisible) {
            isBitRateVisible = bitRateVisible;
        }

        public boolean isDurationVisible() {
            return isDurationVisible;
        }

        public void setDurationVisible(boolean durationVisible) {
            isDurationVisible = durationVisible;
        }

        public boolean isFormatVisible() {
            return isFormatVisible;
        }

        public void setFormatVisible(boolean formatVisible) {
            isFormatVisible = formatVisible;
        }

        public boolean isFileSizeVisible() {
            return isFileSizeVisible;
        }

        public void setFileSizeVisible(boolean fileSizeVisible) {
            isFileSizeVisible = fileSizeVisible;
        }

		public boolean isMoodVisible() {
			return isMoodVisible;
		}

		public void setMoodVisible(boolean MoodVisible) {
			isMoodVisible = MoodVisible;
		}
    }

}
