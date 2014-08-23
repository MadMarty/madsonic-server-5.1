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

import org.madsonic.domain.*;
import org.madsonic.service.*;
import org.madsonic.filter.ParameterDecodingFilter;
import org.madsonic.lastfm.util.*;

import org.springframework.web.bind.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;
import org.springframework.web.servlet.view.*;

import javax.servlet.http.*;

/**
 * Controller for updating music file ratings.
 *
 * @author Sindre Mehus
 */
public class SetRatingController extends AbstractController {

    private RatingService ratingService;
    private SecurityService securityService;
    private MediaFileService mediaFileService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
 
	//String path = request.getParameter("path");
    	
 	Integer id = ServletRequestUtils.getIntParameter(request, "id");    	

        Integer rating = ServletRequestUtils.getIntParameter(request, "rating");
        if (rating == 0) {
            rating = null;
        }

         MediaFile mediaFile = mediaFileService.getMediaFile(id);
		
        String username = securityService.getCurrentUsername(request);
        ratingService.setRatingForUser(username, mediaFile, rating);

		//TODO:Recheck 
//      String url = "main.view?id=" + mediaFile.getId();
        String url = "main.view?id=" + id;

        return new ModelAndView(new RedirectView(url));
    }

    public void setRatingService(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }
}
