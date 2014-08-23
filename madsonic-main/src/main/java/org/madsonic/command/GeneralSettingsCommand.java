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

import org.madsonic.controller.GeneralSettingsController;
import org.madsonic.domain.Theme;

/**
 * Command used in {@link GeneralSettingsController}.
 *
 * @author Sindre Mehus
 */
public class GeneralSettingsCommand {

    private String uploadFolder;

    private String playlistImportFolder;
    private String playlistExportFolder;
    private String playlistBackupFolder;
    
    private String musicFileTypes;
    private String videoFileTypes;
    private String coverArtFileTypes;
    
    private String allFolderIndex;
    private String musicFolderIndex;
    private String videoFolderIndex;
    
    private String index1;
    private String index2;
    private String index3;
    private String index4;	
    
    private String ignoredArticles;
    private String shortcuts;
    
    private boolean folderParsing;
    private boolean albumSetParsing;
    
    private boolean logfileReverse;
    private String logfileLevel;
    
	private boolean showAlbumsYear;
	private boolean showAlbumsYearApi;
    private boolean sortAlbumsByFolder;
    private boolean sortFilesByFilename;
    private boolean sortMediaFileFolder;
    private boolean usePremiumServices;
	private boolean showGenericArtistArt;
	private boolean showShortcuts;
	
//	private boolean HTML5Enabled;
	private String usedVideoPlayer;
	
	private boolean ownGenreEnabled;
	private boolean playlistEnabled;
		
    private boolean gettingStartedEnabled;
	
	private String pageTitle;
    private String welcomeTitle;
    private String welcomeSubtitle;
    private String welcomeMessage;
    private String loginMessage;
    private String localeIndex;
    private String[] locales;
    private String themeIndex;
    private Theme[] themes;
	private String listType = "random";
	private String newAdded;
    private boolean isReloadNeeded;
    private boolean isFullReloadNeeded;
    private boolean toast;
    private boolean statusPlayerChanged;
    
    private boolean showHomeRandom;    
    private boolean showHomeNewAdded; 
    private boolean showHomeHotRated; 
    private boolean showHomeAllArtist; 
    private boolean showHomeStarredArtist; 
    private boolean showHomeStarredAlbum; 
    private boolean showHomeAblumTip; 
    private boolean showHomeTopRated; 
    private boolean showHomeMostPlayed; 
    private boolean showHomeLastPlayed; 
    private boolean showHomeDecade; 
    private boolean showHomeGenre; 
    private boolean showHomeName; 
    private boolean showHomeTop100; 
    private boolean showHomeNew100; 
    
    private int leftframeSize;
    private int playQueueSize;
    
    private boolean showQuickEdit;
    
    public String getPlaylistImportFolder() {
        return playlistImportFolder;
    }

    public void setPlaylistImportFolder(String playlistFolder) {
        this.playlistImportFolder = playlistFolder;
    }

    public String getMusicFileTypes() {
        return musicFileTypes;
    }

    public void setMusicFileTypes(String musicFileTypes) {
        this.musicFileTypes = musicFileTypes;
    }

    public String getVideoFileTypes() {
        return videoFileTypes;
    }

    public void setVideoFileTypes(String videoFileTypes) {
        this.videoFileTypes = videoFileTypes;
    }

    public String getCoverArtFileTypes() {
        return coverArtFileTypes;
    }

    public void setCoverArtFileTypes(String coverArtFileTypes) {
        this.coverArtFileTypes = coverArtFileTypes;
    }

    public String getIndex1() {
        return index1;
    }

    public void setIndex1(String index) {
        this.index1 = index;
    }

    public String getIndex2() {
        return index2;
    }

    public void setIndex2(String index) {
        this.index2 = index;
    }

    public String getIndex3() {
        return index3;
    }

    public void setIndex3(String index) {
        this.index3 = index;
    }

    public String getIndex4() {
        return index4;
    }

    public void setIndex4(String index) {
        this.index4 = index;
    }
	
    public String getIgnoredArticles() {
        return ignoredArticles;
    }

    public void setIgnoredArticles(String ignoredArticles) {
        this.ignoredArticles = ignoredArticles;
    }

    public String getShortcuts() {
        return shortcuts;
    }

    public void setShortcuts(String shortcuts) {
        this.shortcuts = shortcuts;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getWelcomeTitle() {
        return welcomeTitle;
    }

    public void setWelcomeTitle(String welcomeTitle) {
        this.welcomeTitle = welcomeTitle;
    }

    public String getWelcomeSubtitle() {
        return welcomeSubtitle;
    }

    public void setWelcomeSubtitle(String welcomeSubtitle) {
        this.welcomeSubtitle = welcomeSubtitle;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public String getLoginMessage() {
        return loginMessage;
    }

    public void setLoginMessage(String loginMessage) {
        this.loginMessage = loginMessage;
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

	public String getListType() {
		return listType;
	}
	
	public void setListType(String listType) {
		this.listType = listType;
	}
		
    public boolean isReloadNeeded() {
        return isReloadNeeded;
    }

    public void setReloadNeeded(boolean reloadNeeded) {
        isReloadNeeded = reloadNeeded;
    }
    public boolean isFullReloadNeeded() {
		return isFullReloadNeeded;
	}

	public void setFullReloadNeeded(boolean fullReloadNeeded) {
		isFullReloadNeeded = fullReloadNeeded;
	}

	public boolean isShowAlbumsYear() {
        return showAlbumsYear;
    }
    public void setShowAlbumsYear(boolean showAlbumsYear) {
        this.showAlbumsYear = showAlbumsYear;
    }
	
	public boolean isShowAlbumsYearApi() {
        return showAlbumsYearApi;
    }
    public void setShowAlbumsYearApi(boolean showAlbumsYearApi) {
        this.showAlbumsYearApi = showAlbumsYearApi;
    }
	
    public boolean isSortAlbumsByFolder() {
        return sortAlbumsByFolder;
    }

    public void setSortAlbumsByFolder(boolean sortAlbumsByFolder) {
        this.sortAlbumsByFolder = sortAlbumsByFolder;
    }

    public boolean isSortFilesByFilename() {
		return sortFilesByFilename;
	}

	public void setSortFilesByFilename(boolean sortFilesByFilename) {
		this.sortFilesByFilename = sortFilesByFilename;
	}
	
	/**
	 * @return the sortMediaFileFolder
	 */
	public boolean isSortMediaFileFolder() {
		return sortMediaFileFolder;
	}

	/**
	 * @param sortMediaFileFolder the sortMediaFileFolder to set
	 */
	public void setSortMediaFileFolder(boolean sortMediaFileFolder) {
		this.sortMediaFileFolder = sortMediaFileFolder;
	}

	public int getLeftframeSize() {
		return leftframeSize;
	}

	public void setLeftframeSize(int leftframeSize) {
		this.leftframeSize = leftframeSize;
	}

	public int getPlayQueueSize() {
		return playQueueSize;
	}

	public void setPlayQueueSize(int playQueueSize) {
		this.playQueueSize = playQueueSize;
	}

	public boolean isUsePremiumServices() {
        return usePremiumServices;
    }

    public void setUsePremiumServices(boolean usePremiumServices) {
        this.usePremiumServices = usePremiumServices;
    }


    public boolean isGettingStartedEnabled() {
        return gettingStartedEnabled;
    }

    public void setGettingStartedEnabled(boolean gettingStartedEnabled) {
        this.gettingStartedEnabled = gettingStartedEnabled;
    }

    public boolean isToast() {
        return toast;
    }

    public void setToast(boolean toast) {
        this.toast = toast;
    }

	/**
	 * @return the showGenericArtistArt
	 */
	public boolean isShowGenericArtistArt() {
		return showGenericArtistArt;
	}

	/**
	 * @param showGenericArtistArt the showGenericArtistArt to set
	 */
	public void setShowGenericArtistArt(boolean showGenericArtistArt) {
		this.showGenericArtistArt = showGenericArtistArt;
	}

	/**
	 * @return the newAdded
	 */
	public String getNewAdded() {
		return newAdded;
	}

	/**
	 * @param newAdded the newAdded to set
	 */
	public void setNewAdded(String newAdded) {
		this.newAdded = newAdded;
	}

	public boolean isShowShortcuts() {
		return showShortcuts;
	}

	public void setShowShortcuts(boolean showShortcuts) {
		this.showShortcuts = showShortcuts;
	}

	public String getPlaylistExportFolder() {
		return playlistExportFolder;
	}

	public void setPlaylistExportFolder(String playlistExportFolder) {
		this.playlistExportFolder = playlistExportFolder;
	}

	public boolean isShowQuickEdit() {
		return showQuickEdit;
	}

	public void setShowQuickEdit(boolean showQuickEdit) {
		this.showQuickEdit = showQuickEdit;
	}

	public String getUsedVideoPlayer() {
		return usedVideoPlayer;
	}

	public void setUsedVideoPlayer(String usedVideoPlayer) {
		this.usedVideoPlayer = usedVideoPlayer;
	}

//	public boolean isHTML5Enabled() {
//		return HTML5Enabled;
//	}
//	
//	public void setHTML5Enabled(boolean hTML5Enabled) {
//		this.HTML5Enabled = hTML5Enabled;
//	}

	public boolean isStatusPlayerChanged() {
		return statusPlayerChanged;
	}

	public void setStatusPlayerChanged(boolean statusPlayerChanged) {
		this.statusPlayerChanged = statusPlayerChanged;
	}

	public boolean isOwnGenreEnabled() {
		return ownGenreEnabled;
	}

	public void setOwnGenreEnabled(boolean ownGenreEnabled) {
		this.ownGenreEnabled = ownGenreEnabled;
	}

	public boolean isPlaylistEnabled() {
		return playlistEnabled;
	}

	public void setPlaylistEnabled(boolean playlistEnabled) {
		this.playlistEnabled = playlistEnabled;
	}
	
	
	public String getUploadFolder() {
		return uploadFolder;
	}

	public void setUploadFolder(String uploadFolder) {
		this.uploadFolder = uploadFolder;
	}

	public boolean isFolderParsing() {
		return folderParsing;
	}

	public void setFolderParsing(boolean folderParsing) {
		this.folderParsing = folderParsing;
	}

	public boolean isAlbumSetParsing() {
		return albumSetParsing;
	}

	public void setAlbumSetParsing(boolean albumSetParsing) {
		this.albumSetParsing = albumSetParsing;
	}

	/**
	 * @return the logfileReverse
	 */
	public boolean isLogfileReverse() {
		return logfileReverse;
	}

	/**
	 * @param logfileReverse the logfileReverse to set
	 */
	public void setLogfileReverse(boolean logfileReverse) {
		this.logfileReverse = logfileReverse;
	}

	/**
	 * @return the logfileLevel
	 */
	public String getLogfileLevel() {
		return logfileLevel;
	}

	/**
	 * @param logfileLevel the logfileLevel to set
	 */
	public void setLogfileLevel(String logfileLevel) {
		this.logfileLevel = logfileLevel;
	}

	public boolean isShowHomeRandom() {
		return showHomeRandom;
	}

	public void setShowHomeRandom(boolean showHomeRandom) {
		this.showHomeRandom = showHomeRandom;
	}

	public boolean isShowHomeNewAdded() {
		return showHomeNewAdded;
	}

	public void setShowHomeNewAdded(boolean showHomeNewAdded) {
		this.showHomeNewAdded = showHomeNewAdded;
	}

	public boolean isShowHomeHotRated() {
		return showHomeHotRated;
	}

	public void setShowHomeHotRated(boolean showHomeHotRated) {
		this.showHomeHotRated = showHomeHotRated;
	}

	public boolean isShowHomeAllArtist() {
		return showHomeAllArtist;
	}

	public void setShowHomeAllArtist(boolean showHomeAllArtist) {
		this.showHomeAllArtist = showHomeAllArtist;
	}

	public boolean isShowHomeStarredArtist() {
		return showHomeStarredArtist;
	}

	public void setShowHomeStarredArtist(boolean showHomeStarredArtist) {
		this.showHomeStarredArtist = showHomeStarredArtist;
	}

	public boolean isShowHomeStarredAlbum() {
		return showHomeStarredAlbum;
	}

	public void setShowHomeStarredAlbum(boolean showHomeStarredAlbum) {
		this.showHomeStarredAlbum = showHomeStarredAlbum;
	}

	public boolean isShowHomeAblumTip() {
		return showHomeAblumTip;
	}

	public void setShowHomeAblumTip(boolean showHomeAblumTip) {
		this.showHomeAblumTip = showHomeAblumTip;
	}

	public boolean isShowHomeTopRated() {
		return showHomeTopRated;
	}

	public void setShowHomeTopRated(boolean showHomeTopRated) {
		this.showHomeTopRated = showHomeTopRated;
	}

	public boolean isShowHomeMostPlayed() {
		return showHomeMostPlayed;
	}

	public void setShowHomeMostPlayed(boolean showHomeMostPlayed) {
		this.showHomeMostPlayed = showHomeMostPlayed;
	}

	public boolean isShowHomeLastPlayed() {
		return showHomeLastPlayed;
	}

	public void setShowHomeLastPlayed(boolean showHomeLastPlayed) {
		this.showHomeLastPlayed = showHomeLastPlayed;
	}

	public boolean isShowHomeDecade() {
		return showHomeDecade;
	}

	public void setShowHomeDecade(boolean showHomeDecade) {
		this.showHomeDecade = showHomeDecade;
	}

	public boolean isShowHomeGenre() {
		return showHomeGenre;
	}

	public void setShowHomeGenre(boolean showHomeGenre) {
		this.showHomeGenre = showHomeGenre;
	}

	public boolean isShowHomeName() {
		return showHomeName;
	}

	public void setShowHomeName(boolean showHomeName) {
		this.showHomeName = showHomeName;
	}

	public boolean isShowHomeTop100() {
		return showHomeTop100;
	}

	public void setShowHomeTop100(boolean showHomeTop100) {
		this.showHomeTop100 = showHomeTop100;
	}

	public boolean isShowHomeNew100() {
		return showHomeNew100;
	}

	public void setShowHomeNew100(boolean showHomeNew100) {
		this.showHomeNew100 = showHomeNew100;
	}

	public String getMusicFolderIndex() {
		return musicFolderIndex;
	}

	public void setMusicFolderIndex(String musicFolderIndex) {
		this.musicFolderIndex = musicFolderIndex;
	}

	public String getVideoFolderIndex() {
		return videoFolderIndex;
	}

	public void setVideoFolderIndex(String videoFolderIndex) {
		this.videoFolderIndex = videoFolderIndex;
	}

	public String getAllFolderIndex() {
		return allFolderIndex;
	}

	public void setAllFolderIndex(String allFolderIndex) {
		this.allFolderIndex = allFolderIndex;
	}

	public String getPlaylistBackupFolder() {
		return playlistBackupFolder;
	}

	public void setPlaylistBackupFolder(String playlistBackupFolder) {
		this.playlistBackupFolder = playlistBackupFolder;
	}

}
