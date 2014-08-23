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
import org.madsonic.domain.MediaFile.MediaType;
import org.madsonic.service.*;
import org.madsonic.filter.ParameterDecodingFilter;
import org.madsonic.lastfm.util.*;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.view.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;

/**
 * Controller for updating media files.
 *
 * @author Madevil
 */
public class SetMediaFileController extends AbstractController {

    private MediaFileService mediaFileService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Integer id = ServletRequestUtils.getIntParameter(request, "id");    	
		String action = request.getParameter("action");
        String strmediaType = request.getParameter("mediatype");

		MediaType mediaType = null; 
        MediaFile mediaFile = mediaFileService.getMediaFile(id);
		
        if ("setmediatype".equals(action)) {
			if ("MULTIARTIST".equals(strmediaType)) {mediaType = MediaType.MULTIARTIST;}
			if ("ARTIST".equals(strmediaType)) 		{mediaType = MediaType.ARTIST;}
			if ("DIRECTORY".equals(strmediaType)) 	{mediaType = MediaType.DIRECTORY;}
			if ("VIDEOSET".equals(strmediaType)) 	{mediaType = MediaType.VIDEOSET;}
			if ("ALBUMSET".equals(strmediaType)) 	{mediaType = MediaType.ALBUMSET;}
			if ("ALBUM".equals(strmediaType)) 		{mediaType = MediaType.ALBUM;}
			if ("AUTO".equals(strmediaType))		{mediaFile.setMediaTypeOverride(false);}
			else
			{	
				mediaFile.setMediaType(mediaType);
    			mediaFile.setMediaTypeOverride(true);
			}
			if (mediaFile != null) {
				mediaFileService.updateMediaFile(mediaFile);
			}
        }

        String url = "main.view?id=" + id;
        return new ModelAndView(new RedirectView(url));
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }
}