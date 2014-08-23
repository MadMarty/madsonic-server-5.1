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
package org.madsonic.ajax;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.madsonic.dao.MediaFileDao;
import org.madsonic.service.SettingsService;

import org.directwebremoting.WebContextFactory;
import org.eclipse.jetty.util.log.Log;

import org.madsonic.domain.MediaFile;
import org.madsonic.domain.Playlist;
import org.madsonic.service.MediaFileService;
import org.madsonic.service.SecurityService;

/**
 * Provides AJAX-enabled services for manipulating playlists.
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class PlaylistService {

    private MediaFileService mediaFileService;
    private SecurityService securityService;
    private org.madsonic.service.PlaylistService playlistService;
    private MediaFileDao mediaFileDao;
    private SettingsService settingsService;

    public List<Playlist> getReadablePlaylists() {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        String username = securityService.getCurrentUsername(request);
        return playlistService.getReadablePlaylistsForUser(username, "name asc");
    }

    //TODO:CLEANUP --> name asc
    
    public List<Playlist> getWritablePlaylists() {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        String username = securityService.getCurrentUsername(request);
        return playlistService.getWritablePlaylistsForUser(username, "name asc");
    }

    public PlaylistInfo getPlaylist(int id) {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();

        Playlist playlist = playlistService.getPlaylist(id);
        List<MediaFile> files = playlistService.getFilesInPlaylist(id);

        String username = securityService.getCurrentUsername(request);
        mediaFileService.populateStarredDate(files, username);
        return new PlaylistInfo(playlist, createEntries(files));
    }

    public List<Playlist> createNamedPlaylist(String playlistName, String playlistComment, boolean isPublic) {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();

        Date now = new Date();
        Playlist playlist = new Playlist();
        playlist.setUsername(securityService.getCurrentUsername(request));
        playlist.setCreated(now);
        playlist.setChanged(now);
        playlist.setPublic(isPublic);
        playlist.setComment(playlistComment);
        playlist.setName(playlistName);

        if (isPublic){playlist.setShareLevel(1);}
        
        playlistService.createPlaylist(playlist);
        return getReadablePlaylists();
    }

    public List<Playlist> createEmptyPlaylist() {
    	return createEmptyPlaylist(null, false);
    }
    
    public List<Playlist> createEmptyPlaylist(String playlistComment, boolean isPublic) {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        Locale locale = settingsService.getLocale();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);

        Date now = new Date();
        Playlist playlist = new Playlist();
        playlist.setUsername(securityService.getCurrentUsername(request));
        playlist.setCreated(now);
        playlist.setChanged(now);
        playlist.setPublic(isPublic);
        playlist.setComment(playlistComment);
        playlist.setName(dateFormat.format(now));

        if(isPublic){playlist.setShareLevel(1);}
        
        playlistService.createPlaylist(playlist);
        return getReadablePlaylists();
    }

    public void appendToPlaylist(int playlistId, List<Integer> mediaFileIds) {
        List<MediaFile> files = playlistService.getFilesInPlaylist(playlistId);
        for (Integer mediaFileId : mediaFileIds) {
            MediaFile file = mediaFileService.getMediaFile(mediaFileId);
            if (file != null) {
                files.add(file);
            }
        }
        playlistService.setFilesInPlaylist(playlistId, files);
    }

    public String savePlaylist(List<Integer> mediaFileIds) {
		
		HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
		
        Locale locale = settingsService.getLocale();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);

        Date now = new Date();
        Playlist playlist = new Playlist();
        playlist.setUsername(securityService.getCurrentUsername(request));
        playlist.setCreated(now);
        playlist.setChanged(now);
        playlist.setPublic(false);
        playlist.setName(dateFormat.format(now));

        playlistService.createPlaylist(playlist);
        appendToPlaylist(playlist.getId(), mediaFileIds);
        return playlist.getName();
		
    }

    public String savePlaylist(List<Integer> mediaFileIds, String PlaylistName) {
		
		HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
		
        Date now = new Date();
        Playlist playlist = new Playlist();
        playlist.setUsername(securityService.getCurrentUsername(request));
        playlist.setCreated(now);
        playlist.setChanged(now);
        playlist.setPublic(false);
        playlist.setName(PlaylistName);
        playlist.setShareLevel(0);

        playlistService.createPlaylist(playlist);
        appendToPlaylist(playlist.getId(), mediaFileIds);
        return playlist.getName();
    }    

    private List<PlaylistInfo.Entry> createEntries(List<MediaFile> files) {
        List<PlaylistInfo.Entry> result = new ArrayList<PlaylistInfo.Entry>();
        for (MediaFile file : files) {
            result.add(new PlaylistInfo.Entry(file.getId(), file.getRank(), file.getTitle(), file.getArtist(), file.getAlbumName(), file.getDurationString(), file.getStarredDate() != null));
        }

        return result;
    }

    public PlaylistInfo toggleStar(int id, int index, boolean forced) {
    	HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
    	String username = securityService.getCurrentUsername(request);
    	List<MediaFile> files = playlistService.getFilesInPlaylist(id);
    	MediaFile file = files.get(index);

    	boolean starred = mediaFileDao.getMediaFileStarredDate(file.getId(), username) != null;
    	if (starred) {
    		if (!forced) {
    			mediaFileDao.unstarMediaFile(file.getId(), username);
    		}
    	} else {
    		mediaFileDao.starMediaFile(file.getId(), username);
    	}
    	return getPlaylist(id);
    }

    public PlaylistInfo toggleAllStar(int id, boolean forced) {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        String username = securityService.getCurrentUsername(request);
        List<MediaFile> files = playlistService.getFilesInPlaylist(id);

        for (MediaFile file : files) {
            boolean starred = mediaFileDao.getMediaFileStarredDate(file.getId(), username) != null;
            if (starred) {
            	if (!forced) {
                    mediaFileDao.unstarMediaFile(file.getId(), username);
            	}
            } else {
                mediaFileDao.starMediaFile(file.getId(), username);
            }
        }
        return getPlaylist(id);
    }
    
    public PlaylistInfo remove(int id, int index) {
        List<MediaFile> files = playlistService.getFilesInPlaylist(id);
        files.remove(index);
        playlistService.setFilesInPlaylist(id, files);
        return getPlaylist(id);
    }

    public PlaylistInfo up(int id, int index) {
        List<MediaFile> files = playlistService.getFilesInPlaylist(id);
        if (index > 0) {
            MediaFile file = files.remove(index);
            files.add(index - 1, file);
            playlistService.setFilesInPlaylist(id, files);
        }
        return getPlaylist(id);
    }

    public PlaylistInfo down(int id, int index) {
        List<MediaFile> files = playlistService.getFilesInPlaylist(id);
        if (index < files.size() - 1) {
            MediaFile file = files.remove(index);
            files.add(index + 1, file);
            playlistService.setFilesInPlaylist(id, files);
        }
        return getPlaylist(id);
    }

    public void deletePlaylist(int id) {
        playlistService.deletePlaylist(id);
    }

    public PlaylistInfo updatePlaylist(int id, String name, String comment, boolean isPublic) {
        Playlist playlist = playlistService.getPlaylist(id);
        playlist.setName(name);
        playlist.setComment(comment);
        playlist.setPublic(isPublic);
        if (isPublic){
        	playlist.setShareLevel(1);
        }   else {
        	playlist.setShareLevel(0);
        	}
        
        //playlist.setShareLevel(shareLevel);
        
        playlistService.updatePlaylist(playlist);
        return getPlaylist(id);
    }

    public void setPlaylistService(org.madsonic.service.PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setMediaFileDao(MediaFileDao mediaFileDao) {
        this.mediaFileDao = mediaFileDao;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}