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

import org.madsonic.command.GeneralSettingsCommand;
import org.madsonic.domain.Theme;
import org.madsonic.domain.User;
import org.madsonic.domain.UserSettings;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Controller for the page used to administrate general settings.
 *
 * @author Sindre Mehus
 */
public class GeneralSettingsController extends SimpleFormController {

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

    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        GeneralSettingsCommand command = new GeneralSettingsCommand();
        command.setCoverArtFileTypes(settingsService.getCoverArtFileTypes());
        command.setIgnoredArticles(settingsService.getIgnoredArticles());
        command.setShortcuts(settingsService.getShortcuts());
        
        command.setAllFolderIndex(settingsService.getAllIndexString());
        command.setMusicFolderIndex(settingsService.getMusicIndexString());
        command.setVideoFolderIndex(settingsService.getVideoIndexString());
        
        command.setIndex1(settingsService.getIndex1String());
        command.setIndex2(settingsService.getIndex2String());
        command.setIndex3(settingsService.getIndex3String());		
        command.setIndex4(settingsService.getIndex4String());	
        command.setMusicFileTypes(settingsService.getMusicFileTypes());
        command.setVideoFileTypes(settingsService.getVideoFileTypes());
        command.setPlaylistImportFolder(settingsService.getPlaylistImportFolder());
        command.setPlaylistExportFolder(settingsService.getPlaylistExportFolder());
        command.setPlaylistBackupFolder(settingsService.getPlaylistBackupFolder());
        command.setShowShortcuts(settingsService.isShowShortcuts());
        command.setShowAlbumsYear(settingsService.isShowAlbumsYear());
        command.setShowAlbumsYearApi(settingsService.isShowAlbumsYearApi());
        command.setSortAlbumsByFolder(settingsService.isSortAlbumsByFolder());
        command.setSortFilesByFilename(settingsService.isSortFilesByFilename());
        command.setFolderParsing(settingsService.isFolderParsingEnabled());
        command.setAlbumSetParsing(settingsService.isAlbumSetParsingEnabled());
        command.setLogfileReverse(settingsService.isLogfileReverse());
        command.setLogfileLevel(settingsService.getLogLevel());
        command.setSortMediaFileFolder(settingsService.isSortMediaFileFolder());
        command.setUsePremiumServices(settingsService.isUsePremiumServices());
        command.setShowGenericArtistArt(settingsService.isShowGenericArtistArt());
        command.setGettingStartedEnabled(settingsService.isGettingStartedEnabled());
        command.setPageTitle(settingsService.getPageTitle());        
        command.setWelcomeTitle(settingsService.getWelcomeTitle());
        command.setWelcomeSubtitle(settingsService.getWelcomeSubtitle());
        command.setWelcomeMessage(settingsService.getWelcomeMessage());
        command.setLoginMessage(settingsService.getLoginMessage());
        command.setListType(settingsService.getListType());
        command.setNewAdded(settingsService.getNewaddedTimespan());
        command.setLeftframeSize(settingsService.getLeftframeSize());
        command.setPlayQueueSize(settingsService.getPlayqueueSize());
        command.setShowQuickEdit(settingsService.isShowQuickEdit());
        
//      command.setHTML5Enabled(settingsService.isHTML5PlayerEnabled());
        command.setUsedVideoPlayer(SettingsService.getUsedVideoPlayer());
        
        command.setOwnGenreEnabled(settingsService.isOwnGenreEnabled());
        command.setPlaylistEnabled(settingsService.isPlaylistEnabled());
        command.setUploadFolder(SettingsService.getUploadFolder());
        
        command.setShowHomeRandom(settingsService.showHomeRandom());
        command.setShowHomeNewAdded(settingsService.showHomeNewAdded());
        command.setShowHomeHotRated(settingsService.showHomeHotRated());
        command.setShowHomeAllArtist(settingsService.showHomeAllArtist());
        command.setShowHomeStarredArtist(settingsService.showHomeStarredArtist());
        command.setShowHomeStarredAlbum(settingsService.showHomeStarredAlbum());
        command.setShowHomeAblumTip(settingsService.showHomeAlbumTip());
        command.setShowHomeTopRated(settingsService.showHomeTopRated());
        command.setShowHomeMostPlayed(settingsService.showHomeMostPlayed());
        command.setShowHomeLastPlayed(settingsService.showHomeLastPlayed());
        command.setShowHomeDecade(settingsService.showHomeDecade());
        command.setShowHomeGenre(settingsService.showHomeGenre());
        command.setShowHomeName(settingsService.showHomeName());
        command.setShowHomeTop100(settingsService.showHomeTop100());
        command.setShowHomeNew100(settingsService.showHomeNew100());
        
        Theme[] themes = settingsService.getAvailableThemes();
        command.setThemes(themes);
        String currentThemeId = settingsService.getThemeId();
        for (int i = 0; i < themes.length; i++) {
            if (currentThemeId.equals(themes[i].getId())) {
                command.setThemeIndex(String.valueOf(i));
                break;
            }
        }

        Locale currentLocale = settingsService.getLocale();
        Locale[] locales = settingsService.getAvailableLocales();
        String[] localeStrings = new String[locales.length];
        for (int i = 0; i < locales.length; i++) {
            localeStrings[i] = locales[i].getDisplayName(locales[i]);

            if (currentLocale.equals(locales[i])) {
                command.setLocaleIndex(String.valueOf(i));
            }
        }
        command.setLocales(localeStrings);
        return command;

    }

    protected void doSubmitAction(Object comm) throws Exception {
        GeneralSettingsCommand command = (GeneralSettingsCommand) comm;

        int themeIndex = Integer.parseInt(command.getThemeIndex());
        Theme theme = settingsService.getAvailableThemes()[themeIndex];

        int localeIndex = Integer.parseInt(command.getLocaleIndex());
        Locale locale = settingsService.getAvailableLocales()[localeIndex];

        command.setToast(true);
        
        command.setStatusPlayerChanged(!SettingsService.getUsedVideoPlayer().equalsIgnoreCase(command.getUsedVideoPlayer()));
        
        command.setReloadNeeded(!settingsService.getIndex1String().equals(command.getIndex1()) ||
								!settingsService.getIndex2String().equals(command.getIndex2()) ||
								!settingsService.getIndex3String().equals(command.getIndex3()) ||
								!settingsService.getIndex4String().equals(command.getIndex4()) ||
								!settingsService.isSortMediaFileFolder() == command.isSortMediaFileFolder() ||
                                !settingsService.getIgnoredArticles().equals(command.getIgnoredArticles()) ||
                                !settingsService.getShortcuts().equals(command.getShortcuts()) ||
                                !settingsService.getThemeId().equals(theme.getId()) ||
                                !settingsService.getLocale().equals(locale));

        command.setFullReloadNeeded(!settingsService.isShowShortcuts() == command.isShowShortcuts() ||
        						   !(settingsService.getLeftframeSize()==(command.getLeftframeSize())) ||
        						   !(settingsService.isPlaylistEnabled()==(command.isPlaylistEnabled())) ||
        						   !(settingsService.getPlayqueueSize()==(command.getPlayQueueSize())) );

        settingsService.setAllIndexString(command.getAllFolderIndex());
        settingsService.setMusicIndexString(command.getMusicFolderIndex());
        settingsService.setVideoIndexString(command.getVideoFolderIndex());
        
        settingsService.setIndex1String(command.getIndex1());
        settingsService.setIndex2String(command.getIndex2());
        settingsService.setIndex3String(command.getIndex3());
        settingsService.setIndex4String(command.getIndex4());
        settingsService.setIgnoredArticles(command.getIgnoredArticles());
        settingsService.setShortcuts(command.getShortcuts());
        settingsService.setPlaylistImportFolder(command.getPlaylistImportFolder());
        settingsService.setPlaylistExportFolder(command.getPlaylistExportFolder());
        settingsService.setPlaylistBackupFolder(command.getPlaylistBackupFolder());        
        settingsService.setMusicFileTypes(command.getMusicFileTypes());
        settingsService.setVideoFileTypes(command.getVideoFileTypes());
        settingsService.setCoverArtFileTypes(command.getCoverArtFileTypes());
        settingsService.setShowAlbumsYear(command.isShowAlbumsYear());
        settingsService.setShowShortcuts(command.isShowShortcuts());
        settingsService.setShowAlbumsYearApi(command.isShowAlbumsYearApi());
        settingsService.setSortAlbumsByFolder(command.isSortAlbumsByFolder());
        settingsService.setSortFilesByFilename(command.isSortFilesByFilename());
        settingsService.setSortMediaFileFolder(command.isSortMediaFileFolder());
        settingsService.setFolderParsingEnabled(command.isFolderParsing());
        settingsService.setAlbumSetParsingEnabled(command.isAlbumSetParsing());
        settingsService.setLogfileReverse(command.isLogfileReverse());
        settingsService.setLogfileLevel(command.getLogfileLevel());
        settingsService.setUsePremiumServices(command.isUsePremiumServices());
        settingsService.setShowGenericArtistArt(command.isShowGenericArtistArt());
        settingsService.setGettingStartedEnabled(command.isGettingStartedEnabled());
        settingsService.setWelcomeTitle(command.getWelcomeTitle());
        settingsService.setPageTitle(command.getPageTitle());        
        settingsService.setWelcomeSubtitle(command.getWelcomeSubtitle());
        settingsService.setWelcomeMessage(command.getWelcomeMessage());
        settingsService.setLoginMessage(command.getLoginMessage());
        settingsService.setThemeId(theme.getId());
        settingsService.setLocale(locale);
        settingsService.setListType(command.getListType());
        settingsService.setNewaddedTimespan(command.getNewAdded());
        settingsService.setLeftframeSize(command.getLeftframeSize());
        settingsService.setPlayqueueSize(command.getPlayQueueSize());
        settingsService.setShowQuickEdit(command.isShowQuickEdit());
        
//      settingsService.setHTML5PlayerEnabled(command.isHTML5Enabled());
        settingsService.setUsedVideoPlayer(command.getUsedVideoPlayer());
        
        settingsService.setOwnGenreEnabled(command.isOwnGenreEnabled());
        settingsService.setPlaylistEnabled(command.isPlaylistEnabled());
        SettingsService.setUploadFolder(command.getUploadFolder());
        
        settingsService.setHomeRandom(command.isShowHomeRandom());
        settingsService.setHomeNewAdded(command.isShowHomeNewAdded());
        settingsService.setHomeHotRated(command.isShowHomeHotRated());
        settingsService.setHomeAllArtist(command.isShowHomeAllArtist());
        settingsService.setHomeStarredArtist(command.isShowHomeStarredArtist());
        settingsService.setHomeStarredAlbum(command.isShowHomeStarredAlbum());
        settingsService.setHomeAlbumTip(command.isShowHomeAblumTip());
        settingsService.setHomeTopRated(command.isShowHomeTopRated());
        settingsService.setHomeMostPlayed(command.isShowHomeMostPlayed());
        settingsService.setHomeLastPlayed(command.isShowHomeLastPlayed());
        settingsService.setHomeDecade(command.isShowHomeDecade());
        settingsService.setHomeGenre(command.isShowHomeGenre());
        settingsService.setHomeName(command.isShowHomeName());
        settingsService.setHomeTop100(command.isShowHomeTop100());
        settingsService.setHomeNew100(command.isShowHomeNew100());
        
        settingsService.save();
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
    
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }    
}
