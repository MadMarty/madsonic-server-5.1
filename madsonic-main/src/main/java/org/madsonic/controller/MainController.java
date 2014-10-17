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

import org.madsonic.dao.ArtistDao;
import org.madsonic.domain.Artist;
import org.madsonic.domain.CoverArtScheme;
import org.madsonic.domain.LastFMArtist;
import org.madsonic.domain.LastFMArtistTopTrack;
import org.madsonic.domain.MediaFile;
import org.madsonic.domain.MediaFileComparator;
import org.madsonic.domain.Player;
import org.madsonic.domain.UserSettings;
import org.madsonic.service.HotService;
import org.madsonic.service.LastFMService;
import org.madsonic.service.MediaFileService;
import org.madsonic.service.RatingService;
import org.madsonic.service.PlayerService;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Controller for the main page.
 *
 * @author Sindre Mehus
 */
public class MainController extends ParameterizableViewController {

    private SecurityService securityService;
    private PlayerService playerService;
    private SettingsService settingsService;
    private RatingService ratingService;
    private MediaFileService mediaFileService;
    private HotService hotService; 
    private LastFMService lastFMService;
    
    private ArtistDao artistDao;

	@SuppressWarnings("unused")
	@Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        Player player = playerService.getPlayer(request, response);

        int userGroupId = securityService.getCurrentUserGroupId(request);        
        
        List<MediaFile> mediaFiles = null;
        try{
            mediaFiles = getMediaFiles(request, userGroupId);
	    } catch (SecurityException x) {
	        // Ignored
	    }

        // Redirect if root is podcast folder
        MediaFile requestedDir = mediaFiles.get(0);
        
        String podcastFolder =  settingsService.getPodcastFolder();
        String folderRequest = requestedDir.getPath(); 
        
        if (folderRequest.equalsIgnoreCase(podcastFolder)) {
            return new ModelAndView(new RedirectView("podcastReceiver.view?"));
        }   
        
        if (mediaFiles == null) {
            return new ModelAndView(new RedirectView("accessDenied.view"));
        }

        if (mediaFiles.isEmpty()) {
            return new ModelAndView(new RedirectView("notFound.view"));
        }

        MediaFile dir = mediaFiles.get(0);
        if (dir.isFile()) {
            dir = mediaFileService.getParentOf(dir);
        }

        // Redirect if root directory.
        if (mediaFileService.isRoot(dir)) {
            return new ModelAndView(new RedirectView("home.view?"));
        }
        
        List<MediaFile> children = mediaFiles.size() == 1 ? mediaFileService.getChildrenOf(dir, true, true, true) : getMultiFolderChildren(mediaFiles);

        String username = securityService.getCurrentUsername(request);
        UserSettings userSettings = settingsService.getUserSettings(username);

        mediaFileService.populateStarredDate(dir, username);
        mediaFileService.populateStarredDate(children, username);

		boolean lastFMTopTrackSearch = settingsService.getLastFMTopTrackSearch();
		
		if (lastFMTopTrackSearch != false ) {
			
	        Artist artistCandidate = null ; 
	        
	        String artist = lastFMService.getCorrection(dir.getArtist());
	        if (artist != null) artistCandidate = artistDao.getLowerArtist(artist);
	    	
	        boolean isArtist = dir.isSingleArtist() == true ? true : false;
	    	boolean artistFound = artistCandidate == null ? false : true;
	    	
	        int topPlayedfound = artistCandidate == null ? 0 : artistCandidate.getTopPlayFound();
	        int toplastFMfound = artistCandidate == null ? 0 : artistCandidate.getTopPlayCount();
			
	        // found single artist
	        if (isArtist) {
	        	
	            // found artist link
	        	if (artistFound) {
	        		
	            	// LASTFM 
	            	if (toplastFMfound == 0 && toplastFMfound != -1) {
	            		
	            		//update
	            		toplastFMfound = lastFMService.updateTopTrackArtist(artistCandidate.getName());
	            		
	            		// disable auto request
	            		if (toplastFMfound < 1) {
	            			toplastFMfound = -1;
	            		} else {
	            			// update topTrack & search mediaFiles
	                    	List<LastFMArtistTopTrack> topTracks = lastFMService.getTopTrackArtist(artistCandidate.getName());
	                    	lastFMService.updateTopTrackEntries(artistCandidate.getName(), settingsService.getLastFMResultSize(), userGroupId).size(); 
	                    	topPlayedfound = mediaFileService.getTopTracks(artist, userGroupId).size();
	            		}
	            		//set back
	            		artistCandidate.setTopPlayFound(topPlayedfound);
	            		artistCandidate.setTopPlayCount(toplastFMfound);
	            		
	            		// update back
	                	if (artistCandidate != null) artistDao.createOrUpdateArtist(artistCandidate);
	            	}
	        	}
	        }
	        map.put("isArtist", isArtist);
	        map.put("toplastFMfound", toplastFMfound);
	        map.put("topPlayedfound", topPlayedfound);
		}        
		
        map.put("dir", dir);
        map.put("ancestors", getAncestors(dir));
        map.put("children", children);
        map.put("artist", guessArtist(children));
        map.put("album", guessAlbum(children));
        map.put("player", player);
        map.put("sieblingCoverArtScheme", CoverArtScheme.SMALL);
        map.put("user", securityService.getCurrentUser(request));
        map.put("multipleArtists", isMultipleArtists(children));
        map.put("visibility", userSettings.getMainVisibility());
        map.put("buttonVisibility", userSettings.getButtonVisibility());        
        map.put("showAlbumYear", settingsService.isShowAlbumsYear());
        map.put("updateNowPlaying", request.getParameter("updateNowPlaying") != null);
        map.put("customScrollbar", userSettings.isCustomScrollbarEnabled()); 
        map.put("partyMode", userSettings.isPartyModeEnabled());
        map.put("brand", settingsService.getBrand());
		map.put("showQuickEdit", settingsService.isShowQuickEdit());
		map.put("lastFMTopTrackSearch", lastFMTopTrackSearch);

        try {
            MediaFile parent = mediaFileService.getParentOf(dir);
            map.put("parent", parent);
            map.put("navigateUpAllowed", !mediaFileService.isRoot(parent));
        } catch (SecurityException x) {
            // Happens if Podcast directory is outside music folder.
        }

        Integer userRating = ratingService.getRatingForUser(username, dir);
        Double averageRating = ratingService.getAverageRating(dir);

        if (userRating == null) {
            userRating = 0;
        }

        if (averageRating == null) {
            averageRating = 0.0D;
        }

        map.put("userRating", 10 * userRating);
        map.put("averageRating", Math.round(10.0D * averageRating));

        Integer hotRating = hotService.getAlbumHotFlag(dir);
        if (hotRating > 0) 
        {
        	map.put("hotRating", 1);
        }
        else
        {
        	map.put("hotRating", 0);
        }
        
        map.put("starred", mediaFileService.getMediaFileStarredDate(dir.getId(), username) != null);

        CoverArtScheme scheme = player.getCoverArtScheme();
        if (scheme != CoverArtScheme.OFF) {
            List<MediaFile> coverArts = getCoverArtsEx(dir, children);
            int size = coverArts.size() > 1 ? scheme.getSize() : scheme.getSize() * 2;
            
            map.put("coverArts", coverArts);
            map.put("coverArtSize", size);
            if (dir.getCoverArtPath() == null && dir.isSingleArtist()) {
                map.put("coverArtSize", scheme.getSize());
				
                if (settingsService.isShowGenericArtistArt()){
                    map.put("showGenericArtistArt", true);
                }else
                {   map.put("showGenericArtistArt", false);
                }
                
            }            
            if (coverArts.isEmpty() && dir.isAlbum()) {
                map.put("showGenericCoverArt", true);
            }
            if (coverArts.isEmpty() && dir.isDirectory()) {
                map.put("showGenericCoverArt", true);
            }
            setSieblingAlbums(dir, map);
        }
//        setPreviousAndNextAlbums(dir, map);

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private List<MediaFile> getMediaFiles(HttpServletRequest request, int user_group_id) {
        List<MediaFile> mediaFiles = new ArrayList<MediaFile>();
        for (String path : ServletRequestUtils.getStringParameters(request, "path")) {
            MediaFile mediaFile = mediaFileService.getMediaFile(path);
            if (mediaFile != null) {
                mediaFiles.add(mediaFile);
            }
        }
        for (int id : ServletRequestUtils.getIntParameters(request, "id")) {
            MediaFile mediaFile = mediaFileService.getMediaFile(id, user_group_id);
            if (mediaFile != null) {
                mediaFiles.add(mediaFile);
            }
        }
        return mediaFiles;
    }

    private String guessArtist(List<MediaFile> children) {
        for (MediaFile child : children) {
            if (child.isFile() || child.isAlbum() || child.isAlbumSet()) {
	            if (child.getArtist() != null) {
	                return child.getArtist();
	            }
            }
        }
        return null;
    }

    private String guessAlbum(List<MediaFile> children) {
        for (MediaFile child : children) {
            if (child.isFile() && child.getArtist() != null) {
                return child.getAlbumName();
            }
        }
        return null;
    }

    private List<MediaFile> getCoverArts(MediaFile dir, List<MediaFile> children) throws IOException {
        int limit = settingsService.getCoverArtLimit();
        if (limit == 0) {
            limit = Integer.MAX_VALUE;
        }

        List<MediaFile> coverArts = new ArrayList<MediaFile>();
        if (dir.isAlbum()) {
            coverArts.add(dir);
        } else {
            for (MediaFile child : children) {
                if (child.isAlbum()) {
                    coverArts.add(child);
                    if (coverArts.size() >= limit) {
                        break;
                    }
                }
            }
        }
        return coverArts;
    }

    private List<MediaFile> getCoverArtsEx(MediaFile dir, List<MediaFile> children) throws IOException {
    	int limit = settingsService.getCoverArtLimit();
    	if (limit == 0) {
    		limit = Integer.MAX_VALUE;
    	}

    	List<MediaFile> coverArts = new ArrayList<MediaFile>();

    	// ### Get ARTIST MAIN FOLDER
    	if (dir.getCoverArtPath() != null) {
    		coverArts.add(dir);
    	} 

    	// ### CHILDREN ####   
    	List<MediaFile> storedChildren = mediaFileService.getChildrenOf(children, true, true, true);

    	for (MediaFile child : storedChildren) {
    		if (child.getCoverArtPath() != null) {
    			coverArts.add(child);
    		}
    		else
    		{

    			// Subfolder detection	
    			List<MediaFile> substoredChildren = mediaFileService.getChildrenOf(child, true, true, true);
    			for (MediaFile subchild : substoredChildren) {

    				String x ;

    				x = subchild.getCoverArtPath() ;

    				if (x != null) {
    					coverArts.add(subchild);
    					break;
    				}

    			}
    		}
    		if (coverArts.size() > limit) {
    			break;
    		}
    	}
    	return coverArts;
    }

    private List<MediaFile> getMultiFolderChildren(List<MediaFile> mediaFiles) throws IOException {
        SortedSet<MediaFile> result = new TreeSet<MediaFile>(new MediaFileComparator(settingsService.isSortAlbumsByFolder(),settingsService.isSortFilesByFilename()));
        for (MediaFile mediaFile : mediaFiles) {
            if (mediaFile.isFile()) {
                mediaFile = mediaFileService.getParentOf(mediaFile);
            }
            result.addAll(mediaFileService.getChildrenOf(mediaFile, true, true, true));
        }
        return new ArrayList<MediaFile>(result);
    }

    private List<MediaFile> getAncestors(MediaFile dir) throws IOException {
        LinkedList<MediaFile> result = new LinkedList<MediaFile>();

        try {
            MediaFile parent = mediaFileService.getParentOf(dir);
            while (parent != null && !mediaFileService.isRoot(parent)) {
                result.addFirst(parent);
                parent = mediaFileService.getParentOf(parent);
            }
        } catch (SecurityException x) {
            // Happens if Podcast directory is outside music folder.
        }
        return result;
    }

    private void setPreviousAndNextAlbums(MediaFile dir, Map<String, Object> map) throws IOException {
        MediaFile parent = mediaFileService.getParentOf(dir);

        if (dir.isAlbum() && !mediaFileService.isRoot(parent)) {
            List<MediaFile> sieblings = mediaFileService.getChildrenOf(parent, false, true, true);

            int index = sieblings.indexOf(dir);
            if (index > 0) {
                map.put("previousAlbum", sieblings.get(index - 1));
            }
            if (index < sieblings.size() - 1) {
                map.put("nextAlbum", sieblings.get(index + 1));
            }
        }
    }

    private boolean isMultipleArtists(List<MediaFile> children) {
        // Collect unique artist names.
        Set<String> artists = new HashSet<String>();
        for (MediaFile child : children) {
            if (child.getArtist() != null) {
                artists.add(child.getArtist().toLowerCase());
            }
        }

        // If zero or one artist, it is definitely not multiple artists.
        if (artists.size() < 2) {
            return false;
        }

        // Fuzzily compare artist names, allowing for some differences in spelling, whitespace etc.
        List<String> artistList = new ArrayList<String>(artists);
        for (String artist : artistList) {
            if (StringUtils.getLevenshteinDistance(artist, artistList.get(0)) > 3) {
                return true;
            }
        }
        return false;
    }

    private void setSieblingAlbums(MediaFile dir, Map<String, Object> map) throws IOException {
    	
        MediaFile parent = null;
        if (!mediaFileService.isRoot(dir)) {
            parent = mediaFileService.getParentOf(dir);
    	}

        if (dir.isAlbum() && !mediaFileService.isRoot(parent) && ("podcast".equalsIgnoreCase(parent.toString()))) {
            List<MediaFile> sieblings = mediaFileService.getChildrenOf(parent, false, true, true);
            sieblings.remove(dir);

            int limit = settingsService.getCoverArtLimit();
            if (limit == 0) {
                limit = Integer.MAX_VALUE;
            }
            if (sieblings.size() > limit) {
                sieblings = sieblings.subList(0, limit);
            }
            map.put("sieblingAlbums", sieblings);
        }
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setRatingService(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    public void setHotService(HotService hotService) {
        this.hotService = hotService;
    }    

    public void setArtistDao(ArtistDao artistDao) {
        this.artistDao = artistDao;
    }      

    public void setLastFMService(LastFMService lastFMService) {
        this.lastFMService = lastFMService;
    }      
    
    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }
}
