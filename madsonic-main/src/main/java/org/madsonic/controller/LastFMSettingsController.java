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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.madsonic.dao.ArtistDao;
import org.madsonic.domain.Artist;
import org.madsonic.domain.InternetRadio;
import org.madsonic.service.LastFMService;
import org.madsonic.service.MediaScannerService;
import org.madsonic.service.SecurityService;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * @author Madevil
 */
public class LastFMSettingsController extends ParameterizableViewController {

    private ArtistDao artistDao;

    private LastFMService lastFMService;
    private SecurityService securityService;
    private MediaScannerService mediaScannerService;
   
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();

        if (request.getParameter("ScanNow") != null) {
        	map.put("warn", true);
			map.put("warnInfo", "Artist-Coverscan <br>This take some time!");	
            List<Artist> allArtists = artistDao.getAllArtists();
            lastFMService.getArtistImages(allArtists);
			map.put("done", true);
            }		
        
        if (request.getParameter("ScanInfo") != null) {
			map.put("warn", true);
			map.put("warnInfo", "Artist-Infoscan <br>This take some time!");	        	
            List<Artist> allArtists = artistDao.getGroupedAlbumArtists();
            lastFMService.getArtistInfo(allArtists);
			map.put("done", true);
            }	        

        if (request.getParameter("ScanNewInfo") != null) {
			map.put("warn", true);
			map.put("warnInfo", "Artist-Infoscan <br>This take some time!");	        	
            List<Artist> allArtists = artistDao.getNewGroupedAlbumArtists();
            lastFMService.getArtistInfo(allArtists);
			map.put("done", true);
            }	        

        if (request.getParameter("CleanupArtist") != null) {
            lastFMService.CleanupArtist();
			map.put("done", true);
            }	        

        if (request.getParameter("CleanupArtistTopTracks") != null) {
            lastFMService.CleanupArtistTopTracks();
			map.put("done", true);
            }	        
        
        if (request.getParameter("ScanBio") != null) {
            List<Artist> allArtists = artistDao.getGroupedAlbumArtists();
            lastFMService.getArtistBio(allArtists);
    		map.put("done", true);
            }        
			map.put("scanning", mediaScannerService.isScanning());	

			
	        if (isFormSubmission(request)) {
	            String error = handleParameters(request);
	            map.put("error", error);
	            if (error == null) {
	                map.put("reload", true);
	            }
	        }

        ModelAndView result = super.handleRequestInternal(request, response);
        
        map.put("LastFMResultSize", lastFMService.getLastFMResultSize());
        map.put("LastFMTopTrackSearch", lastFMService.getLastFMTopTrackSearch());
        
        result.addObject("model", map);
        return result;
    }


    /**
     * Determine if the given request represents a form submission.
     *
     * @param request current HTTP request
     * @return if the request represents a form submission
     */
    private boolean isFormSubmission(HttpServletRequest request) {
        return "POST".equals(request.getMethod());
    }

    private String handleParameters(HttpServletRequest request) {
    	
        boolean lastFMTopTrackSearch = "on".equalsIgnoreCase(request.getParameter("lastFMTopTrackSearch")) ? true : false; 
        lastFMService.setLastFMTopTrackSearch(lastFMTopTrackSearch);        
   	
        int size = Integer.parseInt(request.getParameter("lastFMResultSize"));
        if (size != 0) {
            lastFMService.setLastFMResultSize(size);
        }

        return null;
    }
    
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }    
 
     public void setMediaScannerService(MediaScannerService mediaScannerService) {
        this.mediaScannerService = mediaScannerService;
    }
     
     public void setLastFMService(LastFMService lastFMService) {
         this.lastFMService = lastFMService;
     }
     
     public void setArtistDao(ArtistDao artistDao) {
         this.artistDao = artistDao;
     }
}
