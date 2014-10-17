/*
 This file is part of Madsonic.

 Madsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Madsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License		
 along with Madsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2013 (C) Madevil
 */
package org.madsonic.controller;

import org.madsonic.command.IconCommand;

import org.madsonic.service.SettingsService;

import org.jfree.util.Log;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for the icon top page.
 *
 * @author Madevil
 */
public class IconSettingsController extends SimpleFormController {

    private SettingsService settingsService;

    protected Object formBackingObject(HttpServletRequest request) throws Exception {
    	
        IconCommand command = new IconCommand();
        
        command.setToast(false);
      	command.setReloadNeeded(false);
      
        command.setShowIconHome(settingsService.showIconHome());
        command.setShowIconArtist(settingsService.showIconArtist());
        command.setShowIconPlaying(settingsService.showIconPlaying());
        command.setShowIconCover(settingsService.showIconCover());
        command.setShowIconStarred(settingsService.showIconStarred());
        command.setShowIconRadio(settingsService.showIconRadio());
        command.setShowIconPodcast(settingsService.showIconPodcast());
        command.setShowIconSettings(settingsService.showIconSettings());
        command.setShowIconStatus(settingsService.showIconStatus());
        command.setShowIconSocial(settingsService.showIconSocial());
        command.setShowIconHistory(settingsService.showIconHistory());
        command.setShowIconStatistics(settingsService.showIconStatistics());
        command.setShowIconPlaylists(settingsService.showIconPlaylists());
        command.setShowIconPlaylistEditor(settingsService.showIconPlaylistEditor());
        command.setShowIconMore(settingsService.showIconMore());
        command.setShowIconRandom(settingsService.showIconRandom());
        command.setShowIconAbout(settingsService.showIconAbout());
        command.setShowIconGenre(settingsService.showIconGenre());
        command.setShowIconMoods(settingsService.showIconMoods());
        command.setShowIconAdmins(settingsService.showIconAdmins());
        
        return command;
    }

    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object com, BindException errors)
		throws Exception {
        IconCommand command = (IconCommand) com;
        
        command.setToast(true);
      	command.setReloadNeeded(true);
        
        settingsService.setshowIconHome(command.isShowIconHome());
        settingsService.setshowIconArtist(command.isShowIconArtist());
        settingsService.setshowIconPlaying(command.isShowIconPlaying());
        settingsService.setshowIconCover(command.isShowIconCover());        
        settingsService.setshowIconStarred(command.isShowIconStarred());
        settingsService.setshowIconRadio(command.isShowIconRadio());
        settingsService.setshowIconPodcast(command.isShowIconPodcast());
        settingsService.setshowIconSettings(command.isShowIconSettings());
        settingsService.setshowIconStatus(command.isShowIconStatus());
        settingsService.setshowIconSocial(command.isShowIconSocial());
        settingsService.setshowIconHistory(command.isShowIconHistory());
        settingsService.setshowIconStatistics(command.isShowIconStatistics());
        settingsService.setshowIconPlaylists(command.isShowIconPlaylists());
        settingsService.setshowIconPlaylistEditor(command.isShowIconPlaylistEditor());
        settingsService.setshowIconMore(command.isShowIconMore()); 
        settingsService.setshowIconRandom(command.isShowIconRandom()); 
        settingsService.setshowIconGenre(command.isShowIconGenre());
        settingsService.setshowIconMoods(command.isShowIconMoods());
        settingsService.setshowIconAbout(command.isShowIconAbout());        
        settingsService.setshowIconAdmins(command.isShowIconAdmins());        
        settingsService.save();

        return new ModelAndView(getSuccessView(), errors.getModel());
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}