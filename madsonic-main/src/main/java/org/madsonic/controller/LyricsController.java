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
import org.madsonic.service.MediaFileService;
import org.madsonic.service.metadata.MetaData;
import org.madsonic.service.metadata.MetaDataParser;
import org.madsonic.service.metadata.MetaDataParserFactory;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.HashMap;

/**
 * Controller for the lyrics popup.
 *
 * @author Sindre Mehus
 */
public class LyricsController extends ParameterizableViewController {

    private MediaFileService mediaFileService;
    private MetaDataParserFactory metaDataParserFactory;	
	
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();

        int mediaFileId = NumberUtils.toInt(request.getParameter("id"));
        if (mediaFileId > 0) {
	        MediaFile mediaFile = mediaFileService.getMediaFile(mediaFileId);
	        MetaData metaData = null;
	        String lyrics = null;
	        MetaDataParser parser = metaDataParserFactory.getParser(mediaFile.getFile());
	        if (parser != null) {
	            metaData = parser.getMetaData(mediaFile.getFile());
	            lyrics = metaData.getLyrics();
	            map.put("lyrics", lyrics);
	        }
        }
        map.put("artist", request.getParameter("artist"));
        map.put("song", request.getParameter("song"));

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }
    
    public void setMetaDataParserFactory(MetaDataParserFactory metaDataParserFactory) {
        this.metaDataParserFactory = metaDataParserFactory;
    }
    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }    
    
}
