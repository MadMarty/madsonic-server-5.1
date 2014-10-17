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

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import org.madsonic.domain.MediaFile;
import org.madsonic.service.MediaFileService;
import org.madsonic.service.PlayerService;
import org.madsonic.service.SettingsService;
import org.madsonic.service.SecurityService;
import org.madsonic.util.StringUtil;
import org.madsonic.util.UrlFile;

/**
 * Controller for the page used to play videos.
 *
 * @author Sindre Mehus
 */
public class VideoPlayerController extends ParameterizableViewController {
	
    private MediaFileService mediaFileService;
    private SettingsService settingsService;
    private PlayerService playerService;
    private SecurityService securityService;
	
	public static final int DEFAULT_BIT_RATE = 1000;
    public static final int[] BIT_RATES = {100, 200, 300, 400, 500, 700, 1000, 1200, 1500, 2000, 3000, 5000, 7000, 10000, 15000};

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        int id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        MediaFile file = mediaFileService.getMediaFile(id);

        int timeOffset = ServletRequestUtils.getIntParameter(request, "timeOffset", 0);
        timeOffset = Math.max(0, timeOffset);
        Integer duration = file.getDurationSeconds();
		
        if (duration != null) {
            map.put("skipOffsets", createSkipOffsets(duration));
            timeOffset = Math.min(duration, timeOffset);
            duration -= timeOffset;
        } else {
        	if ( "hdrun".equalsIgnoreCase(file.getFormat())) {
            	duration = 86400; // hdrun fix 24h
        	}
        }
		
        String playerId = playerService.getPlayer(request, response).getId();
        String url = request.getRequestURL().toString();
		
        String streamUrl = url.replaceFirst("/videoPlayer.view.*", "/stream?id=" + file.getId() + "&player=" + playerId);
        String coverArtUrl = url.replaceFirst("/videoPlayer.view.*", "/coverArt.view?id=" + file.getId());

//      boolean urlRedirectionEnabled = settingsService.isUrlRedirectionEnabled();
		boolean usePremiumServices = settingsService.isUsePremiumServices();
		
        String urlRedirectFrom = settingsService.getUrlRedirectFrom();
        String urlRedirectContextPath = settingsService.getUrlRedirectContextPath();

        String shareUrl = settingsService.getMadsonicUrl();
        String localIp = settingsService.getLocalIpAddress();
        int localPort = settingsService.getPort();
		
        String remoteStreamUrl = StringUtil.rewriteRemoteUrl(streamUrl, shareUrl, usePremiumServices, urlRedirectFrom, urlRedirectContextPath, localIp, localPort);
        String remoteCoverArtUrl = StringUtil.rewriteRemoteUrl(coverArtUrl, shareUrl, usePremiumServices, urlRedirectFrom, urlRedirectContextPath, localIp, localPort);

        if (file.getPath().toLowerCase().endsWith("url")) {
			 UrlFile urlFile = new UrlFile(file.getPath());   
			 String YoutubeId = StringUtil.getYoutubeVideoId(urlFile.getString("InternetShortcut", "URL", null));
			 streamUrl = StringUtil.getYoutubeVideoUrl() + YoutubeId;
			 duration = 0;
        }        
        
        int userGroupId = securityService.getCurrentUserGroupId(request);
        int userDefaultBitrate = securityService.getUserGroupVideoDefault(userGroupId);
        int uservideoMaxBitrate = securityService.getUserGroupVideoMax(userGroupId);
        
        int[] FILTERED_BIT_RATES = {};
        for (int bitrate : BIT_RATES) {
        	if (bitrate <= uservideoMaxBitrate) {
            	FILTERED_BIT_RATES = addElement(FILTERED_BIT_RATES, bitrate);
        	}
        }
        map.put("video", file);
        map.put("player", playerService.getPlayer(request, response).getId());

        map.put("streamUrl", streamUrl);
        map.put("remoteStreamUrl", remoteStreamUrl);
        map.put("remoteCoverArtUrl", remoteCoverArtUrl);
        
        map.put("popout", ServletRequestUtils.getBooleanParameter(request, "popout", false));
        map.put("maxBitRate", ServletRequestUtils.getIntParameter(request, "maxBitRate", userDefaultBitrate));
        map.put("duration", duration);
        map.put("timeOffset", timeOffset);
        map.put("bitRates", BIT_RATES);
        map.put("filteredBitRates", FILTERED_BIT_RATES);       
        map.put("licenseInfo", settingsService.getLicenseInfo());

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        
        if (SettingsService.getUsedVideoPlayer().equalsIgnoreCase("CHROMECAST") ) {
	        result.setViewName("videoPlayer");
	        
        } else if (SettingsService.getUsedVideoPlayer().equalsIgnoreCase("FLASH")) {
	        result.setViewName("videoPlayerFlash");
	        
        } else if (SettingsService.getUsedVideoPlayer().equalsIgnoreCase("HTML5")) {
	        result.setViewName("videoPlayerHTML5");
	        
        } else if (SettingsService.getUsedVideoPlayer().equalsIgnoreCase("MEDIAELEMENT")) {
	        result.setViewName("videoPlayerMEDIAELEMENT");	        
        }
        return result;
    }

    static int[] addElement(int[] a, int e) {
        a  = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }
    
    public static Map<String, Integer> createSkipOffsets(int durationSeconds) {
        LinkedHashMap<String, Integer> result = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < durationSeconds; i += 60) {
            result.put(StringUtil.formatDuration(i), i);
        }
        return result;
    }
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
	
    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }
}
