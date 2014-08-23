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

import org.madsonic.domain.MediaFile;
import org.madsonic.domain.PlayQueue;
import org.madsonic.domain.Player;
import org.madsonic.domain.Share;
import org.madsonic.service.MediaFileService;
import org.madsonic.service.PlayerService;
import org.madsonic.service.PlaylistService;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;
import org.madsonic.service.ShareService;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for sharing music on Twitter, Facebook etc.
 *
 * @author Sindre Mehus
 */
public class ShareManagementController extends MultiActionController {

    private MediaFileService mediaFileService;
    private SettingsService settingsService;
    private ShareService shareService;
    private PlayerService playerService;
    private SecurityService securityService;
    private PlaylistService playlistService;

    public ModelAndView createPlaylistShare(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
    	Integer id = ServletRequestUtils.getIntParameter(request, "id");  
        List<MediaFile> files = playlistService.getFilesInPlaylist(id);
    
        Map<String, Object> map = new HashMap<String, Object>();
        
        if (settingsService.isUsePremiumServices()) {	
	        map.put("urlRedirectionEnabled", settingsService.isUrlRedirectionEnabled());
        } else {	
        	map.put("urlRedirectionEnabled", true);
        }

        MediaFile dir = null;
        if (!files.isEmpty()) {
            dir = files.get(0);
            if (!dir.isAlbum()) {
                dir = mediaFileService.getParentOf(dir);
            }
        }        
        
        map.put("dir", dir);
        map.put("user", securityService.getCurrentUser(request));
        Share share = shareService.createShare(request, files);
        map.put("playUrl", shareService.getShareUrl(share));

        return new ModelAndView("createShare", "model", map);        
        
    }
    
    public ModelAndView createShare(HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<MediaFile> files = getMediaFiles(request);
        MediaFile dir = null;
        if (!files.isEmpty()) {
            dir = files.get(0);
            if (!dir.isAlbum()) {
                dir = mediaFileService.getParentOf(dir);
            }
        }

        Map<String, Object> map = new HashMap<String, Object>();
        
        if (settingsService.isUsePremiumServices()) {	
	        map.put("urlRedirectionEnabled", settingsService.isUrlRedirectionEnabled());
        } else {	
        	map.put("urlRedirectionEnabled", true);
        }
        
        map.put("dir", dir);
        map.put("user", securityService.getCurrentUser(request));

        Share share = shareService.createShare(request, files);
        String description = getDescription(request);
        if (description != null) {
            share.setDescription(description);
            shareService.updateShare(share);
        }

        map.put("playUrl", shareService.getShareUrl(share));
        map.put("licenseInfo", settingsService.getLicenseInfo());

        return new ModelAndView("createShare", "model", map);
    }

    private String getDescription(HttpServletRequest request) throws ServletRequestBindingException {
        Integer playlistId = ServletRequestUtils.getIntParameter(request, "playlist");
        return playlistId == null ? null : playlistService.getPlaylist(playlistId).getName();
    }

    private List<MediaFile> getMediaFiles(HttpServletRequest request) throws Exception {
        Integer id = ServletRequestUtils.getIntParameter(request, "id");
        String playerId = request.getParameter("player");
        Integer playlistId = ServletRequestUtils.getIntParameter(request, "playlist");

        List<MediaFile> result = new ArrayList<MediaFile>();

        if (id != null) {
            MediaFile album = mediaFileService.getMediaFile(id);
            int[] indexes = ServletRequestUtils.getIntParameters(request, "i");
            if (indexes.length == 0) {
                return Arrays.asList(album);
            }
            List<MediaFile> children = mediaFileService.getChildrenOf(album, true, true, true);
            for (int index : indexes) {
                result.add(children.get(index));
            }
        }

        else if (playerId != null) {
            Player player = playerService.getPlayerById(playerId);
            PlayQueue playQueue = player.getPlayQueue();
            result = playQueue.getFiles();
        }

        else if (playlistId != null) {
            result = playlistService.getFilesInPlaylist(playlistId);
        }

        return result;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setShareService(ShareService shareService) {
        this.shareService = shareService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }
}