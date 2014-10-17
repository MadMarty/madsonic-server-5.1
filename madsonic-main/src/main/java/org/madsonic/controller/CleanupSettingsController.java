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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.madsonic.Logger;
import org.madsonic.dao.AlbumDao;
import org.madsonic.dao.ArtistDao;
import org.madsonic.dao.MediaFileDao;
import org.madsonic.dao.MusicFolderDao;
import org.madsonic.dao.PlaylistDao;
import org.madsonic.dao.TranscodingDao;
import org.madsonic.service.MediaScannerService;
import org.madsonic.service.PlaylistService;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * Controller for the page used to administrate the set of internet radio/tv stations.
 *
 * @author Sindre Mehus
 */
public class CleanupSettingsController extends ParameterizableViewController {

    private static final Logger LOG = Logger.getLogger(CleanupSettingsController.class);	
	
    private SecurityService securityService;
    private MediaScannerService mediaScannerService;
    private ArtistDao artistDao;
    private AlbumDao albumDao;
    private MediaFileDao mediaFolderDao;
	private MusicFolderDao musicFolderDao;	
    private PlaylistDao playlistDao;
    private TranscodingDao transcodingDao;    
    private PlaylistService playlistService;
    private SettingsService settingsService;

    // protected ModelAndView playlistSettings(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Map<String, Object> map = new HashMap<String, Object>();
		// return null;
    // }
    
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        
        String playlistExportFolderPath = settingsService.getPlaylistExportFolder();
        String playlistBackupFolderPath = settingsService.getPlaylistBackupFolder();

        if (request.getParameter("FullCleanupNow") != null) {
        	
			playlistService.backupAllPlaylists();
        	playlistDao.deleteAllPlaylists();			
			
        	musicFolderDao.DisableAllMusicFolder();
        	Thread.sleep(200);
        	
            mediaScannerService.scanLibrary();
        	Thread.sleep(200);
        	
        	mediaFolderDao.markNonPresent(new Date());
            expunge();
        	Thread.sleep(200);
        	
        	musicFolderDao.EnableAllMusicFolder();
        	Thread.sleep(200);
        	
        	mediaScannerService.scanLibrary();        	
			map.put("done", true);
			map.put("reload", true);	
        }		

        if (request.getParameter("FullscanNow") != null) {
        	
			playlistService.backupAllPlaylists();
			
        	musicFolderDao.DisableAllMusicFolder();
        	Thread.sleep(200);
        	
            mediaScannerService.scanLibrary();
        	Thread.sleep(200);
        	
        	mediaFolderDao.markNonPresent(new Date());
        	musicFolderDao.EnableAllMusicFolder();
        	Thread.sleep(200);
        	
        	mediaScannerService.scanLibrary();        	
			map.put("done", true);
			map.put("reload", true);	
        }	        
        
        if (request.getParameter("scanNow") != null) {
			playlistService.backupAllPlaylists();
            mediaScannerService.scanLibrary();
			map.put("done", true);
            map.put("reload", true);			
        }

        if (request.getParameter("resetStats") != null) {
        	
            mediaScannerService.scanLibrary();
            securityService.resetStats();
			map.put("done", true);
        }         
        
        if (request.getParameter("resetPlaylists") != null) {
        	playlistDao.deleteAllImportedPlaylists();
            mediaScannerService.scanLibrary();
			map.put("done", true);
            map.put("reload", true);
        }        

        if (request.getParameter("backupPlaylists") != null) {
			playlistService.backupAllPlaylists();
			map.put("done", true);
            map.put("reload", true);
        }  
        
        if (request.getParameter("recoveryPlaylists") != null) {
        	playlistService.recoveryPlaylists();
			map.put("done", true);
            map.put("reload", true);
        }          
        
        if (request.getParameter("deletePlaylists") != null) {
        	playlistDao.deleteAllPlaylists();
			map.put("done", true);
            map.put("reload", true);
        }        
        
        if (request.getParameter("cleanupHistory") != null) {
			mediaFolderDao.cleanupStatistics();			
			map.put("done", true);
            map.put("reload", true);			
        }
        
        if (request.getParameter("reset2MadsonicFLV") != null) {
        	transcodingDao.reset2FLV();
			map.put("warn", true);
			map.put("warnInfo", "Reset transcoding profil to:<br> FLV only");
        }
        
        if (request.getParameter("reset2WEBM") != null) {
        	transcodingDao.reset2WEBM();
			map.put("done", true);
        }
        
        if (request.getParameter("reset2MadsonicDefault") != null) {
        	transcodingDao.reset2MadsonicDefault();
    		settingsService.setDownsamplingCommand("ffmpeg -i %s -map 0:0 -b:a %bk -v 0 -f mp3 -");
    		settingsService.setHlsCommand("ffmpeg -ss %o -t %d -i %s -async 1 -b:v %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f mpegts -c:v libx264 -preset superfast -c:a libmp3lame -threads 0 -");
    		settingsService.save();
			map.put("warn", true);
			map.put("warnInfo", "Reset transcoding profil to: Madsonic default");
			
        }
        
        if (request.getParameter("reset2MadsonicOld") != null) {
        	transcodingDao.reset2MadsonicOld();
    		settingsService.setDownsamplingCommand("ffmpeg -i %s -map 0:0 -b:a %bk -v 0 -f mp3 -");
    		settingsService.setHlsCommand("ffmpeg -ss %o -t %d -i %s -async 1 -b:v %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f mpegts -vcodec libx264 -preset superfast -c:a libmp3lame -threads 0 -");
    		settingsService.save();
			map.put("warn", true);
			map.put("warnInfo", "Reset transcoding profil to:<br> ffmpeg compatibility");
        }
        
        if (request.getParameter("reset2MadsonicWEBM") != null) {
        	transcodingDao.reset2MadsonicWEBM();
			map.put("warn", true);
			map.put("warnInfo", "Reset transcoding profil to:<br> WEBM only");
        }
        
        if (request.getParameter("reset2MadsonicMP4") != null) {
        	transcodingDao.reset2MadsonicMP4();
			map.put("warn", true);
			map.put("warnInfo", "Reset transcoding profil to:<br> MP4 only");
        }

        if (request.getParameter("reset2Subsonic") != null) {
        	transcodingDao.reset2Subsonic();
    		settingsService.setDownsamplingCommand("ffmpeg -i %s -map 0:0 -b:a %bk -v 0 -f mp3 -");
    		settingsService.setHlsCommand("ffmpeg -ss %o -t %d -i %s -async 1 -b:v %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f mpegts -c:v libx264 -preset superfast -c:a libmp3lame -threads 0 -");
			map.put("warn", true);
			map.put("warnInfo", "Reset transcoding profil to<br> Subsonic default");
        }
        
        
        if (request.getParameter("expunge") != null) {
            expunge();
			map.put("done", true);
        }
        
        if (request.getParameter("exportPlaylists") != null) {
			playlistService.exportAllPlaylists();
			map.put("done", true);
	        }		
        
        if (request.getParameter("resetControl") != null) {
            securityService.resetControl();
			map.put("done", true);
            }		
        	map.put("exportfolder", playlistExportFolderPath);		
        	map.put("backupfolder", playlistBackupFolderPath);			
			map.put("scanning", mediaScannerService.isScanning());	
			
        ModelAndView result = super.handleRequestInternal(request, response);
        
		if (request.getRequestURI().toLowerCase().contains("/playlistSettings.view".toLowerCase())) {
//			LOG.warn("## FOUND " + request.getRequestURI().toLowerCase());
            result.setViewName("playlistSettings");
        }
        if (request.getRequestURI().toLowerCase().contains("/folderSettings.view".toLowerCase())) {
//    		LOG.warn("## FOUND " + request.getRequestURI().toLowerCase());
        	result.setViewName("folderSettings");
        }
        result.addObject("model", map);
        return result;
    }

    private void expunge() {
        artistDao.expunge();
        albumDao.expunge();
        mediaFolderDao.expunge();
    }	
	
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }    
 
     public void setMediaScannerService(MediaScannerService mediaScannerService) {
        this.mediaScannerService = mediaScannerService;
    }

     public void setTranscodingDao(TranscodingDao transcodingDao) {
         this.transcodingDao = transcodingDao;
     }
     
    public void setPlaylistDao(PlaylistDao playlistDao) {
        this.playlistDao = playlistDao;
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

    public void setMusicFolderDao(MusicFolderDao musicFolderDao) {
        this.musicFolderDao = musicFolderDao;
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }
    
    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
