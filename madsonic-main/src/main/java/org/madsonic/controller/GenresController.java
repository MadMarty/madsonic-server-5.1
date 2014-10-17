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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.madsonic.Logger;
import org.madsonic.controller.HomeController.Album;
import org.madsonic.domain.Genre;
import org.madsonic.domain.GenreSearchCriteria;
import org.madsonic.domain.MediaFile;
import org.madsonic.domain.User;
import org.madsonic.domain.UserSettings;
import org.madsonic.service.MediaFileService;
import org.madsonic.service.SearchService;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * Controller for the genres browser 
 *
 */
public class GenresController extends ParameterizableViewController {

    private static final String DEFAULT_LIST_TYPE = "artists";	
	
    private MediaFileService mediaFileService;
    private SecurityService securityService;
    private SettingsService settingsService;
    private SearchService searchService;

    private long cacheTimestamp;
    
    private int ARTIST_COUNT = 64;
    private int ALBUM_COUNT = 64;
    private int SONG_COUNT = 15;
    
    private Integer cachedGenreArtistCount;
    private Integer cachedGenreAlbumCount;
    private Integer cachedGenreSongCount;
    
    private List <org.madsonic.domain.Genre> cachedGenreArtist;
    private List <org.madsonic.domain.Genre> cachedGenreAlbum;
    private List <org.madsonic.domain.Genre> cachedGenreSongs;
    
    private static final Logger LOG = Logger.getLogger(GenresController.class);
    
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

	    String listType = DEFAULT_LIST_TYPE; 
	    String genreType = null; 
	    
        User user = securityService.getCurrentUser(request);
        String username = user.getUsername();
        
        UserSettings userSettings = settingsService.getUserSettings(username);
        int userGroupId = securityService.getCurrentUserGroupId(request);
	    
        Map<String, Object> map = new HashMap<String, Object>();
        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);

		if (request.getParameter("listType") != null) {
            listType = String.valueOf(request.getParameter("listType"));
        }        
		genreType = String.valueOf(request.getParameter("genre"));
		
        // ARTIST
		if (cachedGenreArtist == null || cacheTimestamp < settingsService.getLastScanned().getTime()) {
    		cachedGenreArtist = mediaFileService.getGenreArtistList();
    		cachedGenreArtistCount = mediaFileService.getGenreArtistCount();
        	cacheTimestamp = settingsService.getLastScanned().getTime();
        	LOG.debug("## cachedGenreArtist recached");  
        }
		
        List <Genre> genreArtistList = new ArrayList<Genre>();
        int artistFiles = cachedGenreArtistCount;
        
        for (org.madsonic.domain.Genre _genre : cachedGenreArtist) {
        	Genre genre = new Genre();
        	genre.setName(_genre.getName());
        	genre.setArtistCount(_genre.getArtistCount());
        	genre.setOccurrence((int) (_genre.getArtistCount()*100/artistFiles*0.40));
        	genreArtistList.add(genre);
        }
          
        //ALBUM
		if (cachedGenreAlbum == null || cacheTimestamp < settingsService.getLastScanned().getTime()) {
			cachedGenreAlbum = mediaFileService.getGenreArtistList();
    		cachedGenreAlbumCount = mediaFileService.getGenreAlbumCount();
        	cacheTimestamp = settingsService.getLastScanned().getTime();
        	LOG.debug("## cachedGenreAlbum recached");  
        }
        
        List <Genre> genreAlbumList = new ArrayList<Genre>();
        int albumFiles = cachedGenreAlbumCount;
        
        for (org.madsonic.domain.Genre _genre : cachedGenreAlbum) {
        	Genre genre = new Genre();
        	genre.setName(_genre.getName());
        	genre.setAlbumCount(_genre.getAlbumCount());
        	genre.setOccurrence((int) (_genre.getAlbumCount()*100/albumFiles*0.40));
        	genreAlbumList.add(genre);
        }
        
        //SONGS
		if (cachedGenreSongs == null || cacheTimestamp < settingsService.getLastScanned().getTime()) {
			cachedGenreSongs = mediaFileService.getGenreSongList();
			cachedGenreSongCount = mediaFileService.getGenreSongCount();
        	cacheTimestamp = settingsService.getLastScanned().getTime();
        	LOG.debug("## cachedGenreSongs recached");  
        }
		
        List <Genre> genreSongList = new ArrayList<Genre>();
        int songFiles = cachedGenreSongCount;
        
        for (org.madsonic.domain.Genre _genre : cachedGenreSongs) {
        	Genre genre = new Genre();
        	genre.setName(_genre.getName());
        	genre.setSongCount(_genre.getSongCount());        	
        	genre.setOccurrence((int) (_genre.getSongCount()*100/songFiles*0.40));
        	genreSongList.add(genre);
        }
        
        List<Album> albums = Collections.emptyList();
        List<MediaFile> songs = Collections.emptyList();
        
        if ("artists".equals(listType)) {
           albums = getArtistsByGenre(0, ARTIST_COUNT, genreType, userGroupId);
            
        } else if ("albums".equals(listType)) {
           albums = getAlbumsByGenre(0, ALBUM_COUNT, genreType, userGroupId);
            
        } else if ("songs".equals(listType)) {
        	songs = getSongsByGenre(SONG_COUNT, genreType, userGroupId);
        } 
        
		map.put("genreArtistList", genreArtistList);
		map.put("genreAlbumList", genreAlbumList);
		map.put("genreSongList", genreSongList);
		
		map.put("listType", listType);
		map.put("genreType", genreType);
		
        map.put("albums", albums);
        map.put("songs", songs);
        
        return result;
    }
    
    public List<MediaFile> getSongsByGenre(int count, String genreType, int userGroupId) {
    	List<MediaFile> firstAlbum = new ArrayList<MediaFile>();
    	GenreSearchCriteria criteria = new GenreSearchCriteria (count, genreType, null, null, null, null, userGroupId);
    	firstAlbum.addAll(searchService.getRandomSongs(criteria));
    	return firstAlbum;
    }

    public List<Album> getArtistsByGenre(int offset, int count, String genre, int user_group_id) throws IOException {
    	List<Album> result = new ArrayList<Album>();
    	for (MediaFile file : mediaFileService.getArtistsByGenre(offset, count, genre, user_group_id)) {
    		Album album = createArtistAlbum(file);
    		if (album != null) {
    			// set parent id 
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

    private List<Album> getAlbumsByGenre(int offset, int count, String genre, int user_group_id) {
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

    private Album createAlbum(MediaFile file) {
        Album album = new Album();
        album.setId(file.getId());
        album.setPath(file.getPath());
        try {
            resolveArtistAndAlbumTitle(album, file);
            resolveCoverArt(album, file);
        } catch (Exception x) {
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
    		// LOG.warn("Failed to create albumTitle list entry for " + file.getPath(), x);
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

    public class Genre {

        private String name;
        private int occurrence;
        private int artistCount;
        private int albumCount;
        private int songCount;
        
        public Genre(String name, int occurrence, int artistCount, int albumCount, int songCount) {
            this.name = name;
            this.occurrence = occurrence;
            this.artistCount = artistCount;
            this.albumCount = albumCount;
            this.songCount = songCount;            
        }

		public Genre() {}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}		
		
		public int getOccurrence() {
			return occurrence;
		}

		public void setOccurrence(int occurrence) {
			this.occurrence = occurrence;
		}

		public int getArtistCount() {
			return artistCount;
		}

		public void setArtistCount(int artistCount) {
			this.artistCount = artistCount;
		}

		public int getAlbumCount() {
			return albumCount;
		}

		public void setAlbumCount(int albumCount) {
			this.albumCount = albumCount;
		}

		public int getSongCount() {
			return songCount;
		}

		public void setSongCount(int songCount) {
			this.songCount = songCount;
		}
    }
    
    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
 
    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }    
    
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
    
    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

	public List <org.madsonic.domain.Genre> getCachedGenreSongs() {
		return cachedGenreSongs;
	}

	public void setCachedGenreSongs(List <org.madsonic.domain.Genre> cachedGenreSongs) {
		this.cachedGenreSongs = cachedGenreSongs;
	}

	public Integer getCachedGenreSongCount() {
		return cachedGenreSongCount;
	}

	public void setCachedGenreSongCount(Integer cachedGenreSongCount) {
		this.cachedGenreSongCount = cachedGenreSongCount;
	}
    
}