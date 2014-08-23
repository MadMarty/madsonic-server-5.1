/*
 This file is part of Madsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Madsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Madsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2014 (C) Madevil
 */
package org.madsonic.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
public class CreateURLFileController extends AbstractController {

    private MediaFileService mediaFileService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Integer id = ServletRequestUtils.getIntParameter(request, "id");    	
        String title = request.getParameter("title");
        String youTubeId = request.getParameter("URL");
        MediaFile mediaFile = mediaFileService.getMediaFile(id);
		
      try {
      String target = "https://www.youtube.com/watch?v=" + youTubeId;
      File file = new File(mediaFile.getPath() + "\\" + title + " [" + youTubeId + "].URL");
      FileWriter fw = new FileWriter(file);
      fw.write("[InternetShortcut]\n");
      fw.write("URL=" + target + "\n");
      fw.flush();
      fw.close();
     
    } catch ( IOException e ) {
       e.printStackTrace();
    }
        String url = "main.view?id=" + id;
        return new ModelAndView(new RedirectView(url));
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }
}