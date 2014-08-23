package org.madsonic.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.madsonic.controller.HomeController.Album;
import org.madsonic.domain.MediaFile;
import org.madsonic.domain.User;
import org.madsonic.domain.UserSettings;
import org.madsonic.service.MediaFileService;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;


/**
 * Controller for the genres browser 
 *
 */
public class GenresController extends ParameterizableViewController {

    private static final String DEFAULT_LIST_TYPE = "albums";	
	
    private MediaFileService mediaFileService;
    private SecurityService securityService;
    private SettingsService settingsService;

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
        List <Genre> genreArtistList = new ArrayList<Genre>();
        int artistFiles = mediaFileService.getGenreArtistCount();
        
        for (org.madsonic.domain.Genre _genre : mediaFileService.getGenreArtistList()) {
        	Genre genre = new Genre();
        	genre.setName(_genre.getName());
        	genre.setOccurrence((int) (_genre.getArtistCount()*100/artistFiles*0.40));
        	genreArtistList.add(genre);
        }
          
        //ALBUM
        List <Genre> genreAlbumList = new ArrayList<Genre>();
        int albumFiles = mediaFileService.getGenreAlbumCount();
        
        for (org.madsonic.domain.Genre _genre : mediaFileService.getGenreAlbumList()) {
        	Genre genre = new Genre();
        	genre.setName(_genre.getName());
        	genre.setOccurrence((int) (_genre.getAlbumCount()*100/albumFiles*0.40));
        	genreAlbumList.add(genre);
        }
        
        //SONGS
        List <Genre> genreSongList = new ArrayList<Genre>();
        int songFiles = mediaFileService.getGenreSongCount();
        
        for (org.madsonic.domain.Genre _genre : mediaFileService.getGenreSongList()) {
        	Genre genre = new Genre();
        	genre.setName(_genre.getName());
        	genre.setOccurrence((int) (_genre.getSongCount()*100/songFiles*0.40));
        	genreSongList.add(genre);
        }
        
        List<Album> albums = Collections.emptyList();
        
        if ("artists".equals(listType)) {
           albums = getArtistsByGenre(0, 64, genreType, userGroupId);
            
        } else if ("albums".equals(listType)) {
           albums = getAlbumsByGenre(0, 64, genreType, userGroupId);
            
        } else if ("songs".equals(listType)) {
        } 
        
		map.put("genreArtistList", genreArtistList);
		map.put("genreAlbumList", genreAlbumList);
		map.put("genreSongList", genreSongList);
		
		map.put("listType", listType);
		map.put("genreType", genreType);
		
        map.put("albums", albums);
		
        return result;
    }
    
    public List<Album> getArtistsByGenre(int offset, int count, String genre, int user_group_id) throws IOException {
        List<Album> result = new ArrayList<Album>();
        for (MediaFile file : mediaFileService.getArtistsByGenre(offset, count, genre, user_group_id)) {
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
//            LOG.warn("Failed to create albumTitle list entry for " + file.getPath(), x);
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

        public Genre(String name, int occurrence) {
            this.name = name;
            this.occurrence = occurrence;
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
    }
    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
    
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
    
    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }
    
}