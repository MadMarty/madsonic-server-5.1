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

import org.madsonic.Logger;
import org.madsonic.domain.Album;
import org.madsonic.domain.CoverArtScheme;
import org.madsonic.domain.Genre;
import org.madsonic.domain.MediaFile;
import org.madsonic.domain.MusicFolder;
import org.madsonic.domain.User;
import org.madsonic.domain.UserSettings;
import org.madsonic.service.HotService;
import org.madsonic.service.MediaFileService;
import org.madsonic.service.MediaScannerService;
import org.madsonic.service.RatingService;
import org.madsonic.service.SearchService;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

import static org.springframework.web.bind.ServletRequestUtils.*;
/**
 * Controller for the home page.
 *
 * @author Sindre Mehus
 */
public class HomeController extends ParameterizableViewController {

    private static final Logger LOG = Logger.getLogger(HomeController.class);

    private static final int DEFAULT_LIST_ROWS    =   2;
    private static final int DEFAULT_LIST_COLUMNS =   5;
    private static final int DEFAULT_LIST_SIZE    =   DEFAULT_LIST_ROWS * DEFAULT_LIST_COLUMNS;
    
    private static final int MAX_LIST_SIZE        =  500;
    private static final int MAX_LIST_OFFSET      = 5000;
    
	private static final int MAX_LIST_ROWS        =  100;
    private static final int MAX_LIST_COLUMNS     =   50;
    
	private static final int DEFAULT_LIST_OFFSET  =    0;
    private static final int DEFAULT_TOPLIST_SIZE =  100;
    private static final int DEFAULT_TIPLIST_SIZE =   50;
    
    private static final String DEFAULT_LIST_TYPE =   "random";
	
    private SettingsService settingsService;
    private MediaScannerService mediaScannerService;
    private RatingService ratingService;
    private SecurityService securityService;
    private MediaFileService mediaFileService;
    private SearchService searchService;
    private HotService hotService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

    	
    	
    	
        User user = securityService.getCurrentUser(request);
        if (user.isAdminRole() && settingsService.isGettingStartedEnabled()) {
            return new ModelAndView(new RedirectView("gettingStarted.view"));
        }
        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());
        int musicFolderId = userSettings.getSelectedMusicFolderId();
        
        MusicFolder selectedMusicFolder = settingsService.getMusicFolderById(musicFolderId);
        
        int userGroupId = securityService.getCurrentUserGroupId(request);
        
        String listType = DEFAULT_LIST_TYPE;
        int listRows    = DEFAULT_LIST_ROWS;
        int listColumns = DEFAULT_LIST_COLUMNS;
        int listOffset  = DEFAULT_LIST_OFFSET;
        int listTopSize = DEFAULT_TOPLIST_SIZE;
        int listTipSize = DEFAULT_TIPLIST_SIZE;
		
        if (request.getParameter("listType") != null) {
            listType = String.valueOf(request.getParameter("listType"));
        }
  
        if (request.getParameter("listRows") != null) {
            listRows = Math.max(0, Math.min(Integer.parseInt(request.getParameter("listRows")), MAX_LIST_ROWS));
        }
        if (request.getParameter("listColumns") != null) {
            listColumns = Math.max(0, Math.min(Integer.parseInt(request.getParameter("listColumns")), MAX_LIST_COLUMNS));
        }
        if (request.getParameter("listOffset") != null) {
            listOffset = Math.max(0, Math.min(Integer.parseInt(request.getParameter("listOffset")), MAX_LIST_OFFSET));
        }

        int listSize = listRows * listColumns;
        if (request.getParameter("listSize") != null) {
            listSize = Math.max(0, Math.min(Integer.parseInt(request.getParameter("listSize")), MAX_LIST_SIZE));
        }

        Map<String, Object> map = new HashMap<String, Object>();
        
        List<Album> albums = Collections.emptyList();
        if ("highest".equals(listType)) {
            albums = getHighestRated(listOffset, listSize, userGroupId);
        } else if ("frequent".equals(listType)) {
            albums = getMostFrequent(listOffset, listSize, userGroupId);
        } else if ("recent".equals(listType)) {
            albums = getMostRecent(listOffset, listSize, userGroupId);
        } else if ("newest".equals(listType)) {
            albums = getNewest(selectedMusicFolder, listOffset, listSize, userGroupId);
        } else if ("tip".equals(listType)) {
            albums = getRandomHot(listTipSize, userGroupId);
		} else if ("hot".equals(listType)) {
            albums = getHot(selectedMusicFolder, listOffset, listSize, userGroupId);
        } else if ("new".equals(listType)) {
            albums = getNewest(selectedMusicFolder, listOffset, listTopSize, userGroupId);
        } else if ("top".equals(listType)) {
            albums = getHighestRated(listOffset, listTopSize, userGroupId);
		} else if ("random".equals(listType)) {
            albums = getRandom(selectedMusicFolder, listSize, userGroupId);
        } else if ("starredArtist".equals(listType)) {
            albums = getStarredArtist(listOffset, listSize, user.getUsername());
        } else if ("allArtist".equals(listType)) {
            albums = getArtist(listOffset, listSize, user.getUsername(),userGroupId );
        } else if ("starred".equals(listType)) {
            albums = getStarred(listOffset, listSize, user.getUsername());
        } else if ("alphabetical".equals(listType)) {
            albums = getAlphabetical(listOffset, listSize, true, userGroupId);
        } else if ("decade".equals(listType)) {
            List<Integer> decades = createDecades();
            map.put("decades", decades);
            int decade = getIntParameter(request, "decade", decades.get(0));
            map.put("decade", decade);
            albums = getByYear(listOffset, listSize, decade, decade + 9, userGroupId);
            
        } else if ("genre".equals(listType)) {
        	
//          List<String> genres = mediaFileService.getGenres(userGroupId);
            List<Genre> genres = mediaFileService.getGenres(true);
            
            map.put("genres", genres);
            if (!genres.isEmpty()) {
                String genre = getStringParameter(request, "genre", genres.get(0).getName());
                map.put("genre", genre);
                albums = getByGenre(listOffset, listSize, genre, userGroupId); 
                }
        } 
        map.put("albums", albums);
        map.put("welcomeTitle", settingsService.getWelcomeTitle());
        map.put("welcomeSubtitle", settingsService.getWelcomeSubtitle());
        map.put("welcomeMessage", settingsService.getWelcomeMessage());
        map.put("isIndexBeingCreated", mediaScannerService.isScanning());
        map.put("listType", listType);
        if ("new".equals(listType)) {
            map.put("listSize", listTopSize);
        }
        else if ("top".equals(listType)) {
            map.put("listSize", listTopSize);
    	}
        else if ("tip".equals(listType)) {
            map.put("listSize", listTipSize);
    	}
        else {
            map.put("listSize", listSize);
        }
        map.put("listOffset", listOffset);
		map.put("listRows", listRows);
		map.put("listColumns", listColumns);
		
        map.put("coverArtSize", CoverArtScheme.MEDIUM.getSize());
        map.put("customScrollbar", userSettings.isCustomScrollbarEnabled());
        map.put("showTopTrack",  user.isLastFMRole());
        
        String header = "";
        if (settingsService.showHomeRandom()) {header = "random";}
        if (settingsService.showHomeNewAdded()){header = header + " newest";}
        if (settingsService.showHomeHotRated()){header = header + " hot";}
        if (settingsService.showHomeAllArtist()){header = header + " allArtist";}
        if (settingsService.showHomeStarredArtist()){header = header + " starredArtist";}
        if (settingsService.showHomeStarredAlbum()){header = header + " starred";}
        if (settingsService.showHomeAlbumTip()){header = header + " tip";}
        if (settingsService.showHomeTopRated()){header = header + " highest";}
        if (settingsService.showHomeMostPlayed()){header = header + " frequent";}
        if (settingsService.showHomeLastPlayed()){header = header + " recent";}
        if (settingsService.showHomeDecade()){header = header + " decade";}
        if (settingsService.showHomeGenre()){header = header + " genre";}
        if (settingsService.showHomeName()){header = header + " alphabetical";}
        if (settingsService.showHomeTop100()){header = header + " top";}
        if (settingsService.showHomeNew100()){header = header + " new";}    
        map.put("homeHeader", header);
        
        map.put("showHomePagerTop", settingsService.showHomePagerTop());  
        map.put("showHomePagerBottom", settingsService.showHomePagerBottom());  
        
        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    private List<Album> getHighestRated(int offset, int count, int user_group_id) {
        List<Album> result = new ArrayList<Album>();
        for (MediaFile mediaFile : ratingService.getHighestRated(offset, count, user_group_id)) {
            Album album = createAlbum(mediaFile);
            if (album != null) {
                album.setRating((int) Math.round(ratingService.getAverageRating(mediaFile) * 10.0D));
                // set parent id for url
                try {
	                MediaFile parent = mediaFileService.getParentOf(mediaFile);
	                album.setParentId(parent.getId());
                } catch (Exception x) {
                }                
                result.add(album);
            }
        }
        return result;
    }

    private List<Album> getRandomHot(int count, int user_group_id) {
	List<Album> result = new ArrayList<Album>();
	
		int randomOffset = 0;
		for ( int loop = 1; loop <= count; loop ++ ) {
			Random random = new Random(System.currentTimeMillis());
			randomOffset = random.nextInt(hotService.getCountHotFlag() == 0 ? 1 : hotService.getCountHotFlag());			
		        for (MediaFile mediaFile : hotService.getRandomHotRated(randomOffset, 1, user_group_id)) {
		            Album album = createAlbum(mediaFile);
		            if (album != null) {
				        try { 
		                      // set parent id for url
		                      MediaFile parent = mediaFileService.getParentOf(mediaFile);
		                      album.setParentId(parent.getId());
	
				        	  album.setPlayCount(mediaFile.getPlayCount());
		                	  
		                      Date created = mediaFile.getCreated();
		                      if (created == null) {
		                          created = mediaFile.getChanged();
		                      }
		                      album.setCreated(created);
				        	  album.setRating((int) Math.round(ratingService.getAverageRating(mediaFile) * 10.0D));

				            } catch (Exception x) {}
				        
				        boolean albumFound = false;
				        for (Album albumEntry : result){
				        	
				        	if (albumEntry.albumTitle == album.albumTitle){
				        		albumFound = true;
				        		break;
				        	}
				        }
				        if (albumFound == false){
							result.add(album);
				        }
				        if (result.size() > 4 ){
				        	return result;
				        }
					 }
		        }
		}
		
		if (result.size() < 1) {
			result = Collections.emptyList();
		}	

		return result;
	}
	
	List<Album> getHot(MusicFolder musicFolder, int offset, int count, int user_group_id) {
	List<Album> result = new ArrayList<Album>();
        for (MediaFile mediaFile : hotService.getHotRated(musicFolder, offset, count, user_group_id)) {
            Album album = createAlbum(mediaFile);
		if (album != null) {
			result.add(album);
		}
	}
	return result;
}
   private List<Album> getMostFrequent(int offset, int count, int user_group_id) {
        List<Album> result = new ArrayList<Album>();
        for (MediaFile mediaFile : mediaFileService.getMostFrequentlyPlayedAlbums(offset, count, user_group_id )) {
            Album album = createAlbum(mediaFile);
            if (album != null) {
                album.setPlayCount(mediaFile.getPlayCount());
                
                // set parent id for url
                try {
	                MediaFile parent = mediaFileService.getParentOf(mediaFile);
	                album.setParentId(parent.getId());
                } catch (Exception x) {
                }
                result.add(album);
            }
        }
        return result;
    }

    private List<Album> getMostRecent(int offset, int count, int user_group_id) {
        List<Album> result = new ArrayList<Album>();
        for (MediaFile mediaFile : mediaFileService.getMostRecentlyPlayedAlbums(offset, count, user_group_id)) {
            Album album = createAlbum(mediaFile);
            if (album != null) {
                album.setLastPlayed(mediaFile.getLastPlayed());
                // set parent id for url
                MediaFile parent = mediaFileService.getParentOf(mediaFile);
                album.setParentId(parent.getId());
                
                result.add(album);
            }
        }
        return result;
    }

    private List<Album> getNewest(MusicFolder musicFolder, int offset, int count, int userGroupId) throws IOException {
        List<Album> result = new ArrayList<Album>();
		for (MediaFile file : mediaFileService.getNewestAlbums(musicFolder, offset, count, userGroupId)) {
            Album album = createAlbum(file);
            if (album != null) {
                Date created = file.getCreated();
                if (created == null) {
                    created = file.getChanged();
                }
                album.setCreated(created);
                
                // set parent id for url
                try {
                    MediaFile parent = mediaFileService.getParentOf(file);
                    album.setParentId(parent.getId());
                } catch (Exception x) {
                    LOG.warn("## Failed to get ParentId for list entry " + file.getPath(), x);
                }
                result.add(album);
            }
        }
        return result;
    }

    private List<Album> getStarred(int offset, int count, String username) throws IOException {
        List<Album> result = new ArrayList<Album>();
        for (MediaFile file : mediaFileService.getStarredAlbums(offset, count, username)) {
            Album album = createAlbum(file);
            if (album != null) {
                // set parent id for url
            	try {
                MediaFile parent = mediaFileService.getParentOf(file);
                album.setParentId(parent.getId());            	
                } catch (Exception x) {
                }
                result.add(album);
            }
        }
        return result;
    }

    public List<Album> getArtist(int offset, int count, String username, int user_group_id) throws IOException {
        List<Album> result = new ArrayList<Album>();
        for (MediaFile file : mediaFileService.getArtists(offset, count, username, user_group_id)) {
            Album album = createArtistAlbum(file);
            if (album != null) {
                // set parent id for url
            	try {
            		MediaFile parent = mediaFileService.getParentOf(file);
            		album.setParentId(file.getId());            	
                } catch (Exception x) {
            	}
                result.add(album);
            }
        }
        return result;
    }    
    
    private List<Album> getStarredArtist(int offset, int count, String username) throws IOException {
        List<Album> result = new ArrayList<Album>();
        for (MediaFile file : mediaFileService.getStarredArtists(offset, count, username)) {
            Album album = createArtistAlbum(file);
            if (album != null) {
                // set parent id for url
            	try {
	            	MediaFile parent = mediaFileService.getParentOf(file);
	                album.setParentId(file.getId());     
                } catch (Exception x) {
                }
                result.add(album);
            }
        }
        return result;
    }
    
    
    
    
    
    
    
    private List<Album> getRandom(MusicFolder musicFolder, int count, int user_group_id) throws IOException {
        List<Album> result = new ArrayList<Album>();
        
        
        
        
        for (MediaFile file : searchService.getRandomAlbums(musicFolder, count, user_group_id)) {
            Album album = createAlbum(file);
            if (album != null) {
                // set parent id for url
            	try {
                MediaFile parent = mediaFileService.getParentOf(file);
                album.setParentId(parent.getId());
                } catch (Exception x) {
                }
                result.add(album);
            }
        }
        return result;
    }

    private  List<Album> getByYear(int offset, int count, int fromYear, int toYear, int user_group_id) {
        List<Album> result = new ArrayList<Album>();
        for (MediaFile file : mediaFileService.getAlbumsByYear(offset, count, fromYear, toYear, user_group_id)) {
            Album album = createAlbum(file);
            if (album != null) {
            	try {
                    MediaFile parent = mediaFileService.getParentOf(file);
                    album.setParentId(parent.getId());
		            album.setYear(file.getYear());
                    } catch (Exception x) {
                    }
                    result.add(album);
            }
        }
        return result;
    }

    private List<Integer> createDecades() {
        List<Integer> result = new ArrayList<Integer>();
        int decade = Calendar.getInstance().get(Calendar.YEAR) / 10;
        for (int i = 0; i < 10; i++) {
            result.add((decade - i) * 10);
        }
        return result;
    }

    private List<Album> getByGenre(int offset, int count, String genre, int user_group_id) {
        List<Album> result = new ArrayList<Album>();
        for (MediaFile file : mediaFileService.getAlbumsByGenre(offset, count, genre, user_group_id)) {
            Album album = createAlbum(file);
            if (album != null) {
            	try {
                    MediaFile parent = mediaFileService.getParentOf(file);
                    album.setParentId(parent.getId());
                    } catch (Exception x) {
                    }
                    result.add(album);            	
            }
        }
        return result;
	}	
		
    private List<Album> getAlphabetical(int offset, int count, boolean byArtist, int user_group_id) throws IOException {
        List<Album> result = new ArrayList<Album>();
        for (MediaFile file : mediaFileService.getAlphabetialAlbums(offset, count, byArtist, user_group_id)) {
            Album album = createAlbum(file);
            if (album != null) {
                result.add(album);
            }
        }
        return result;
    }

    private Album createAlbum(MediaFile file) {
        Album album = new Album();
        album.setId(file.getId());
        album.setPath(file.getPath());
        try {
            resolveArtistAndAlbumTitle(album, file);
            resolveCoverArt(album, file);
        } catch (Exception x) {
            LOG.warn("Failed to create albumTitle list entry for " + file.getPath(), x);
            return null;
        }
        return album;
    }
    
    private Album createArtistAlbum(MediaFile file) {
        Album album = new Album();
        album.setId(file.getId());
        album.setPath(file.getPath());
        try {
            album.setArtist(file.getArtist());
            album.setAlbumSetName(file.getGenre());
            album.setArtistFlag(file.isSingleArtist());
            
            resolveCoverArt(album, file);
        } catch (Exception x) {
            LOG.warn("Failed to create albumTitle list entry for " + file.getPath(), x);
            return null;
        }
        return album;
    }

    private void resolveArtistAndAlbumTitle(Album album, MediaFile file) throws IOException {
        album.setArtist(file.getArtist());
        album.setAlbumTitle(file.getAlbumName());
        album.setAlbumSetName(file.getAlbumSetName());
        album.setAlbumYear(file.getYear());
    }

    private void resolveCoverArt(Album album, MediaFile file) {
        album.setCoverArtPath(file.getCoverArtPath());
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setMediaScannerService(MediaScannerService mediaScannerService) {
        this.mediaScannerService = mediaScannerService;
    }

    public void setRatingService(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    public void setHotService(HotService hotService) {
        this.hotService = hotService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Contains info for a single album.
     */
    public static class Album {
        private String path;
        private String coverArtPath;
        private String artist;
        private String albumTitle;
        private String albumSetName;
        private Integer albumYear;
        private Date created;
        private Date lastPlayed;
        private Integer playCount;
        private Integer rating;
        private int id;
        private int parentId;
        private boolean artistFlag;
        private Integer year;
        
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getCoverArtPath() {
            return coverArtPath;
        }

        public void setCoverArtPath(String coverArtPath) {
            this.coverArtPath = coverArtPath;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getAlbumTitle() {
            return albumTitle;
        }
		
        public String getAlbumSetName() {
            return albumSetName;
        }
		
        public void setAlbumTitle(String albumTitle) {
            this.albumTitle = albumTitle;
        }

        public void setAlbumSetName(String albumSetName) {
            this.albumSetName = albumSetName;
        }
		
		
        public Integer getAlbumYear() {
            return albumYear;
        }

        public void setAlbumYear(Integer albumYear) {
            this.albumYear = albumYear;
        }

        public Date getCreated() {
            return created;
        }

        public void setCreated(Date created) {
            this.created = created;
        }

        public Date getLastPlayed() {
            return lastPlayed;
        }

        public void setLastPlayed(Date lastPlayed) {
            this.lastPlayed = lastPlayed;
        }

        public Integer getPlayCount() {
            return playCount;
        }

        public void setPlayCount(Integer playCount) {
            this.playCount = playCount;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

		public Integer getParentId() {
			return parentId;
		}

		public void setParentId(Integer parentId) {
			this.parentId = parentId;
		}

		public boolean isArtistFlag() {
			return artistFlag;
		}

		public void setArtistFlag(boolean artistFlag) {
			this.artistFlag = artistFlag;
		}

		public Integer getYear() {
			return year;
		}

		public void setYear(Integer year) {
			this.year = year;
		}
    }
}
