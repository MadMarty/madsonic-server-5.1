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

import org.madsonic.command.MusicFolderSettingsCommand;
import org.madsonic.dao.AlbumDao;
import org.madsonic.dao.ArtistDao;
import org.madsonic.dao.MediaFileDao;
import org.madsonic.domain.MusicFolder;
import org.madsonic.domain.User;
import org.madsonic.domain.UserSettings;
import org.madsonic.service.MediaScannerService;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
/**
 * Controller for the page used to administrate the set of music folders.
 *
 * @author Sindre Mehus
 */
public class MusicFolderSettingsController extends SimpleFormController {

    private SettingsService settingsService;
    private MediaScannerService mediaScannerService;
    private ArtistDao artistDao;
    private AlbumDao albumDao;
    private MediaFileDao mediaFolderDao;
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
        MusicFolderSettingsCommand command = new MusicFolderSettingsCommand();

        if (request.getParameter("scanNow") != null) {
            mediaScannerService.backupAllPlaylists();
            mediaScannerService.scanLibrary();
        }
        if (request.getParameter("expunge") != null) {
            expunge();
        }

        command.setInterval(String.valueOf(settingsService.getIndexCreationInterval()));
        command.setHour(String.valueOf(settingsService.getIndexCreationHour()));
        command.setFastCache(settingsService.isFastCacheEnabled());
        command.setOrganizeByFolderStructure(settingsService.isOrganizeByFolderStructure());
        command.setOrganizeByGenreMap(settingsService.isOrganizeByGenreMap());
        command.setScanning(mediaScannerService.isScanning());
        command.setMusicFolders(wrap(settingsService.getAllMusicFolders(true, true)));
        command.setNewMusicFolder(new MusicFolderSettingsCommand.MusicFolderInfo());
        command.setReload(request.getParameter("reload") != null || request.getParameter("scanNow") != null);
        return command;
    }

    private void expunge() {
        artistDao.expunge();
        albumDao.expunge();
        mediaFolderDao.expunge();
    }

    private List<MusicFolderSettingsCommand.MusicFolderInfo> wrap(List<MusicFolder> musicFolders) {
        ArrayList<MusicFolderSettingsCommand.MusicFolderInfo> result = new ArrayList<MusicFolderSettingsCommand.MusicFolderInfo>();
        for (MusicFolder musicFolder : musicFolders) {
            result.add(new MusicFolderSettingsCommand.MusicFolderInfo(musicFolder));
        }
        return result;
    }

    @Override
    protected ModelAndView onSubmit(Object comm) throws Exception {
        MusicFolderSettingsCommand command = (MusicFolderSettingsCommand) comm;

        for (MusicFolderSettingsCommand.MusicFolderInfo musicFolderInfo : command.getMusicFolders()) {
            if (musicFolderInfo.isDelete()) {
                settingsService.deleteMusicFolder(musicFolderInfo.getId());
            } else {
                settingsService.updateMusicFolder(musicFolderInfo.toMusicFolder());
            }
        }

        MusicFolder newMusicFolder = command.getNewMusicFolder().toMusicFolder();
        if (newMusicFolder != null) {
            settingsService.createMusicFolder(newMusicFolder);
        }

        settingsService.setIndexCreationInterval(Integer.parseInt(command.getInterval()));
        settingsService.setIndexCreationHour(Integer.parseInt(command.getHour()));
        settingsService.setFastCacheEnabled(command.isFastCache());
        settingsService.setOrganizeByFolderStructure(command.isOrganizeByFolderStructure());
        settingsService.setOrganizeByGenreMap(command.isOrganizeByGenreMap());
        settingsService.save();

        mediaScannerService.schedule();
        return new ModelAndView(new RedirectView(getSuccessView() + ".view?reload"));
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setMediaScannerService(MediaScannerService mediaScannerService) {
        this.mediaScannerService = mediaScannerService;
    }

    public void setArtistDao(ArtistDao artistDao) {
        this.artistDao = artistDao;
    }

    public void setAlbumDao(AlbumDao albumDao) {
        this.albumDao = albumDao;
    }

    public void setMediaFolderDao(MediaFileDao mediaFolderDao) {
        this.mediaFolderDao = mediaFolderDao;
    }
    
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }    
    
}