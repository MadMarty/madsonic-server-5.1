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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.madsonic.*;
import org.madsonic.service.*;
import org.madsonic.util.StringUtil;
import org.madsonic.util.Util;
import org.madsonic.util.VersionChecker;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.*;

import javax.servlet.http.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Controller for the help page.
 *
 * @author Sindre Mehus
 */
public class HelpController extends ParameterizableViewController {

    private VersionService versionService;
    private SettingsService settingsService;
    private SecurityService securityService;
    private TranscodingService transcodingService;    
    
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        if (versionService.isNewFinalVersionAvailable()) {
            map.put("newVersionAvailable", true);
            map.put("latestVersion", versionService.getLatestFinalVersion());
            
        } else if (versionService.isNewBetaVersionAvailable()) {
            map.put("newVersionAvailable", true);
            map.put("latestVersion", versionService.getLatestBetaVersion());
        }

        File HSQLDB = new File(SettingsService.getMadsonicHome() + Util.getDefaultSlash() + "db");
        long sizeHSQLDB = HSQLDB.exists() ? FileUtils.sizeOfDirectory(HSQLDB) / 1024 / 1024 : 0 ;
        
        File lastFMCache = new File(SettingsService.getMadsonicHome() + Util.getDefaultSlash() + ".last.fm-cache");
        long sizeLastFMCache = lastFMCache.exists() ? FileUtils.sizeOfDirectory(lastFMCache) / 1024 / 1024 : 0 ;
        
        File luceneCache = new File(SettingsService.getMadsonicHome() + Util.getDefaultSlash() + "lucene2");
        long sizeLuceneCache = luceneCache.exists() ? FileUtils.sizeOfDirectory(luceneCache) / 1024 / 1024 : 0 ;

        File thumbsCache = new File(SettingsService.getMadsonicHome() + Util.getDefaultSlash() + "thumbs");
        long sizeThumbsCache = thumbsCache.exists() ? FileUtils.sizeOfDirectory(thumbsCache) / 1024 / 1024 : 0 ;
        
        String cacheInfo;
        
        cacheInfo = "Size: Database: " + sizeHSQLDB + 
	        		"MB, Thumbs-Cache: " + sizeThumbsCache + 
	        		"MB, LastFM-Cache: " + sizeLastFMCache + 
	        		"MB, Lucene-Cache: " + sizeLuceneCache + "MB";
        
    	// Check transcoder
    	String ffmpegVersion = "WARNING no transcoder found!";
        String commandLine = settingsService.getDownsamplingCommand();
        boolean transcoderFound = isTranscodingStepInstalled(commandLine);
        String transcoderPath = transcodingService.getTranscodeDirectory() + Util.getDefaultSlash() + StringUtil.split(commandLine)[0] + (Util.isWindows() == true ? ".exe" : "");
    	
        if (transcoderFound) {
        	ProcessBuilder processBuilder = new ProcessBuilder(transcodingService.getTranscodeDirectory() + Util.getDefaultSlash() + StringUtil.split(commandLine)[0], "-version" );    
        	try {
        		int readInt;
        		Process process = processBuilder.start();
        		InputStream instream = process.getInputStream();
        		StringBuffer commandResult = new StringBuffer();
        		while ((readInt = instream.read()) != -1) commandResult.append((char)readInt);
        		BufferedReader b = new BufferedReader(new StringReader(commandResult.toString()));
        		for (String line = b.readLine(); line != null; line = b.readLine()) {
        			if (line.contains("ffmpeg version")) {
        				ffmpegVersion = line;
        				File file = new File(transcoderPath);
        				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        				String ffmpegFileDate = sdf.format(file.lastModified());
        				ffmpegVersion = ffmpegVersion + " (" + ffmpegFileDate + ")";  
        				break;
        			}
        		}
        	} catch (IOException e) {
        	} catch (Exception e) {
        	}
        }
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();

        String serverInfo = request.getSession().getServletContext().getServerInfo() +
                            ", java " + System.getProperty("java.version") +
                            ", " + System.getProperty("os.name");

        map.put("licenseInfo", settingsService.getLicenseInfo());
        map.put("brand", settingsService.getBrand());
        map.put("localVersion", versionService.getLocalVersion());
        map.put("buildTime", versionService.getLocalBuildTime());
        map.put("buildDate", versionService.getLocalBuildDate());
        map.put("buildNumber", versionService.getLocalBuildNumber());
        map.put("serverInfo", serverInfo);
        map.put("cacheInfo", cacheInfo);        
        map.put("springInfo", VersionChecker.getSpringFrameworkVersion());
        map.put("databaseInfo", VersionChecker.getSpringSecurityVersion());
        map.put("transcoderFound", !ffmpegVersion.contains("WARNING"));
        map.put("ffmpegInfo", ffmpegVersion);
        map.put("RESTInfo", StringUtil.getRESTProtocolVersion());      
        map.put("usedMemory", totalMemory - freeMemory);
        map.put("totalMemory", totalMemory);
        map.put("logEntries", Logger.getLatestLogEntries(settingsService.isLogfileReverse()));
        map.put("logFile", Logger.getLogFile());
        map.put("user", securityService.getCurrentUser(request));

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private boolean isTranscodingStepInstalled(String step) {
        if (StringUtils.isEmpty(step)) {
            return true;
        }
        String executable = StringUtil.split(step)[0];
        PrefixFileFilter filter = new PrefixFileFilter(executable);
        String[] matches = transcodingService.getTranscodeDirectory().list(filter);
        return matches != null && matches.length > 0;
    }
    
    public void setVersionService(VersionService versionService) {
        this.versionService = versionService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
    
    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }
    
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
    
}
