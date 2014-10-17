/*
 This file is part of Madsonic.

 Madsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Madsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License		
 along with Madsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2013 (C) Madevil
 */
 package org.madsonic.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.madsonic.Logger;
import org.madsonic.dao.ArtistDao;
import org.madsonic.dao.LastFMArtistDao;
import org.madsonic.dao.LastFMArtistSimilarDao;
import org.madsonic.dao.LastFMArtistTopTracksDao;
import org.madsonic.dao.MediaFileDao;
import org.madsonic.domain.Artist;
import org.madsonic.domain.LastFMArtist;
import org.madsonic.domain.LastFMArtistSimilar;
import org.madsonic.domain.LastFMArtistTopTrack;
import org.madsonic.domain.MediaFile; 
import org.madsonic.domain.MediaFileComparator;
import org.madsonic.domain.MediaFileRankComparator;
import org.madsonic.domain.SearchCriteria;
import org.madsonic.domain.SearchResult;
import org.madsonic.lastfm.Album;
import org.madsonic.lastfm.Image;
import org.madsonic.lastfm.ImageSize;
import org.madsonic.lastfm.PaginatedResult;
import org.madsonic.lastfm.Track;
import org.madsonic.util.StringUtil;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.jfree.util.Log;

public class LastFMService {
	
    private static final Logger LOG = Logger.getLogger(LastFMService.class);
	
    private SecurityService securityService;
    private SettingsService settingsService;
    private MediaFileService mediaFileService;	
    private LastFMArtistDao lastFMArtistDao;
    private MediaFileDao mediaFileDao;  
    private ArtistDao artistDao;    
    private SearchService searchService;
    
    private static LastFMArtistSimilarDao lastFMArtistSimilarDao;   
    private static LastFMArtistTopTracksDao lastFMArtistTopTracksDao;      
    
    // Use here your own Last.FM API key
    private static String api_key = "8b396e869b58f63e65d352d1a71874f2";
    
    Map<String, String> storedCorrectionMap = new HashMap<String, String>();
    
    public void setLastFMResultSize(int size) {
    	settingsService.setLastFMResultSize(size);
        settingsService.save();
    }

    public int getLastFMResultSize() {
    	return settingsService.getLastFMResultSize();
    }
    
    public String getCorrection(String ArtistName){
    	
	 	String stripedArtist = stripNonValidXMLCharacters( ArtistName );
	 	
    	String RequestedArtist = null;

    	if (stripedArtist != null && stripedArtist != ""){
    		try {
    			String RequestedArtistfromMap = storedCorrectionMap.get(stripedArtist)  ;

    			if (RequestedArtistfromMap == null) {
    				RequestedArtist = (org.madsonic.lastfm.Artist.getCorrection(stripedArtist, api_key)).getName();
    				
        			if (!stripedArtist.equalsIgnoreCase(RequestedArtist)){
        				storedCorrectionMap.put(stripedArtist, RequestedArtist);
        			}
    			} else {
    				RequestedArtist = RequestedArtistfromMap;
    			}
    		} 
    		catch (NullPointerException ex) {
    			System.out.println("## ERROR: updateTopTrackArtist - ArtistName '" + stripedArtist + "'");
    		}
    	}
    	return RequestedArtist;

    }
    
    public LastFMArtist getArtist(String artistname) {
    	
	try {
		 	if (artistname != null || artistname.length() > 0 ){
				String tmpArtistName = org.madsonic.lastfm.Artist.getCorrection(artistname, api_key).getName();
				if (artistname != tmpArtistName) {
					LOG.debug("## ArtistAutoCorrect: " + artistname + " -> " + tmpArtistName);
				}
		    	return lastFMArtistDao.getArtist(tmpArtistName);
		 	}
	    } catch (Exception x) {
    	// Ignore Exception
    	return null;
    }
	return null;
    }
    
    private String escape(String s) {
    	if (s == null){
    		return null;
    	}
        Pattern p= Pattern.compile("([-&\\|!\\(\\){}\\[\\]\\^\"\\~\\*\\?:\\\\])");
        s=p.matcher(s).replaceAll("\\\\$1");
        return s;
    }
    
    public List<MediaFile> getSearchresult(String artist, String title, int userGroupId){
    	
        StringBuilder query = new StringBuilder();
        query.append("artist:"+ escape(artist)).append(" AND ");
        query.append("title:" + escape(title)).append(" AND ");
        query.append("mediaType:music");

        SearchCriteria criteria = new SearchCriteria();
        criteria.setQuery(query.toString().trim());
        
        criteria.setOffset(0);
        criteria.setCount(1);

        SearchResult result = searchService.search(criteria, SearchService.IndexType.SONG, userGroupId); 
        
        return result.getMediaFiles();        
    }
    
    public List<MediaFile> getTopTrack(String artist, int limit, int userGroupId) {
		return null;
    }
    
    public void updateTopTrackStats(String artist, int toplastFMfound, int topPlayedfound) {

        Artist artistCandidate ; 
        String RequestedArtist = getCorrection(artist);
 		artistCandidate = artistDao.getArtist(RequestedArtist);
    	
		artistCandidate.setTopPlayFound(toplastFMfound);
		artistCandidate.setTopPlayCount(topPlayedfound);
		
		artistDao.createOrUpdateArtist(artistCandidate);
	}       

    public List<MediaFile> updateTopTrackEntries(String artist, int limit, int userGroupId) {
    	
    	//TODO: check & scan with service
    	
	    List<MediaFile> topTrackSongs = new ArrayList<MediaFile>();
        HashSet<MediaFile> hs = new HashSet<MediaFile>();
    	List<LastFMArtistTopTrack> topTracks = getTopTrackArtist(artist);
    	
    	if (topTracks.size() < 1){
        	updateTopTrackArtist(artist);
        	if (topTracks.size() < 1){
        		
        	}
        	topTracks = getTopTrackArtist(artist);
    	}
    	
    	for ( LastFMArtistTopTrack track : topTracks){
    		MediaFile requested = null;
    		List<MediaFile> searchResult = getSearchresult(track.getArtistname(), track.getSongname(), userGroupId );
    		
    		if (searchResult != null && searchResult.size() != 0) {
    			requested = mediaFileService.getMediaFile(searchResult.get(0).getId(), userGroupId);
    		}
	        if (requested != null) {
	        	if (requested.getRank() != track.getRank()) {
		        	requested.setRank(track.getRank());
		            mediaFileDao.createOrUpdateMediaFile(requested);
	        	}
	        	hs.add(requested);
	        	
	        	if (hs.size() > limit) {
	        		break;
	        	}
	        } 
    	}
        // Filter out duplicates
	     topTrackSongs.clear();
	     topTrackSongs.addAll(hs);
	    
	    // Sort by Rank 
        Comparator<MediaFile> comparator = new MediaFileRankComparator();
        Set<MediaFile> set = new TreeSet<MediaFile>(comparator);
        set.addAll(topTrackSongs);
        topTrackSongs = new ArrayList<MediaFile>(set);

		return topTrackSongs;
    }
    
    public int updateTopTrackArtist(String ArtistName){
    	
    	// Update TopTracks in table
    	
    	if (ArtistName != null && ArtistName != ""){
    		
		try {
    			
            Artist artistCandidate ; 
            String RequestedArtist = getCorrection(ArtistName);
   	 		artistCandidate = artistDao.getArtist(RequestedArtist);
        	
        	boolean artistFound = artistCandidate == null ? false : true;
            int toplastFMfound = artistCandidate == null ? 0 : artistCandidate.getTopPlayCount();
    		
        	LastFMArtistTopTrack topTrack = new LastFMArtistTopTrack();
    	 	Collection<Track> TopTracks = org.madsonic.lastfm.Artist.getTopTracks(RequestedArtist, api_key, getLastFMResultSize());
    	 	
    	 	if (TopTracks.size() > toplastFMfound ) {
    	 		artistCandidate.setTopPlayCount(TopTracks.size());
    	 		artistDao.createOrUpdateArtist(artistCandidate);
    	 	}
    	 	
    	 	int rank = 1;
    	 	for (Track track : TopTracks ) {
    	        topTrack.setArtistname(track.getArtist());
    	        
    	        if (RequestedArtist != track.getArtist()){
    	          topTrack.setAlternate(RequestedArtist);
    	        }
    	        
           //TODO: move to last.fm table
    	        
    	   //   topTrack.setArtistmbid(track.getArtistMbid());
    	        topTrack.setSongname(track.getName());
    	        topTrack.setSongmbid(track.getMbid());
    	        topTrack.setRank(rank);
    	        topTrack.setPlayCount(track.getPlaycount());
    	    	lastFMArtistTopTracksDao.createOrUpdateLastFMArtistTopTrack(topTrack);
    	    	rank++;
    	 	}

//    	 	getTopTrack(RequestedArtist, 1, 0);
    		LOG.debug("## TopTrack update results: " + TopTracks.size() + " -> " + RequestedArtist);
    	 	return TopTracks.size();
			
	    	} catch (Exception ex) {}
	 	}  	
	 	return 0;
    }
    
    public List<LastFMArtistTopTrack> getAllTopTrack(int count) {
    	return lastFMArtistTopTracksDao.getAllTopTrack(count);
    }
    
    public List<LastFMArtistTopTrack> getTopTrackArtist(String ArtistName){
    	String alternate = null;
    	List<LastFMArtistTopTrack> getTopTrackArtist = new ArrayList<LastFMArtistTopTrack>();
    	try {
    		alternate = (org.madsonic.lastfm.Artist.getCorrection(ArtistName, api_key)).getName();
        } catch (NullPointerException ex) {
	        System.out.println("## ERROR: ArtistName '" + ArtistName + "'");
        }
    	if (alternate != null) {
        	return lastFMArtistTopTracksDao.getTopTrack(alternate, ArtistName);
    	} 
		return getTopTrackArtist;
    }    
    
    public List<String> getSimilarArtist(String ArtistName){
    	return lastFMArtistSimilarDao.getSimilarArtist(ArtistName);
    }
    
    public void CleanupArtist(){
    	lastFMArtistDao.CleanupArtist();
    }
    
    public void CleanupArtistTopTracks(){
    	lastFMArtistTopTracksDao.CleanupArtistTopTracks();
    }    
    
	public void getArtistImages(List<Artist> artistList) {
	   	
		LOG.info("## ArtistCount: " + artistList.size());
	 	MediaFile mediaFileArtist;
	 	
	 	for (Artist artist : artistList) {
	 		try{
	 			int id = mediaFileService.getIDfromArtistname(artist.getName()) == null ? -1 : mediaFileService.getIDfromArtistname(artist.getName());
			 	mediaFileArtist = mediaFileService.getMediaFile(id);
			 	
			 	if (mediaFileArtist == null){
			       continue;
			 	}
			 	LOG.debug("## Scan for Artist: " + artist.getName() );
		 		getArtistImage(mediaFileArtist, api_key);

		        } catch (NullPointerException ex) {
			        System.out.println("## ERROR: " + artist.getName());
		        }
	 	}
		LOG.info("## LastFM Scan Finished");
	}
    
	public void getArtistBio(List<Artist> artistList) {
	   	
		LOG.info("## ArtistCount: " + artistList.size());
	 	
	 	MediaFile mediaFileArtist;
	 	
	 	//LastFMArtist 
	 	for (Artist artist : artistList) {
	 		try{
	 			int id = mediaFileService.getIDfromArtistname(artist.getName()) == null ? -1 : mediaFileService.getIDfromArtistname(artist.getName());
			 	mediaFileArtist = mediaFileService.getMediaFile(id);
			 	
			 	if (mediaFileArtist == null){
			       continue;
			 	}
			 	LOG.debug("## Scan for ArtistBio: " + artist.getName() );
			 	
//		 		getArtistBio(mediaFileArtist, api_key);
//		 		mediaFileService.createOrUpdateMediaFile(mediaFileArtist);

		        } catch (NullPointerException ex) {
			        System.out.println("## ERROR: " + artist.getName());
		        }
	 	}
		LOG.info("## LastFM Scan Finished");
	}	
	
    public String stripNonValidXMLCharacters(String in) {
        StringBuilder out = new StringBuilder(); // Used to hold the output.
        char current; // Used to reference the current character.

        if (in == null || ("".equals(in))) return ""; // vacancy test.
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
            if ((current == 0x9) ||
                (current == 0xA) ||
                (current == 0xB) ||                
                (current == 0xD) ||
                (current == 0x1f) ||                
                ((current >= 0x20) && (current <= 0xD7FF)) ||
                ((current >= 0xE000) && (current <= 0xFFFD)) ||
                ((current >= 0x10000) && (current <= 0x10FFFF)))
                out.append(current);
        }
        return out.toString();
    }
	
    //----------------- Import ------------------
	public void getArtistInfo(List<Artist> artistList) {

		LOG.debug("## ArtistCount: " + artistList.size());
	 	Locale LastFMLocale = new Locale(settingsService.getLocale().toString()) ; 	
		LOG.debug("## LastFM Locale: " + LastFMLocale.toString());	 	
		
	 	for (Artist artist : artistList) {
	 		try{
			 	if (artist.getArtistFolder() != null) {

			 	LastFMArtist lastFMartist = new LastFMArtist();
			 	org.madsonic.lastfm.Artist tmpArtist = null;
			 	
			 	//String escapedArtist = stripNonValidXMLCharacters(StringEscapeUtils.escapeXml(artist.getArtistFolder()) );
			 	String stripedArtist = stripNonValidXMLCharacters( artist.getArtistFolder() );
			 	String RequestedArtist = (org.madsonic.lastfm.Artist.getCorrection(stripedArtist, api_key)).getName();

				//todo:error
			 	try {
			 			tmpArtist = org.madsonic.lastfm.Artist.getInfo(RequestedArtist, LastFMLocale, null, api_key);
			 		}
			 		catch (Exception e) {
				 		Log.error("## FATAL Error! Artist Fetch! " + tmpArtist.getName());
				}
				
			 	lastFMartist.setArtistname(tmpArtist.getName());
			 	lastFMartist.setMbid(tmpArtist.getMbid());
			 	lastFMartist.setUrl(tmpArtist.getUrl());
			 	lastFMartist.setSince(tmpArtist.getSince());
			 	lastFMartist.setPlayCount(tmpArtist.getPlaycount());

			 	Collection<Album> TopAlbum = org.madsonic.lastfm.Artist.getTopAlbums(RequestedArtist, api_key, 3);

			 	String CollAlbum = null;
			 	for (Album album : TopAlbum) {
			 		if (album != null) {
			 			if (CollAlbum == null) {
			 				CollAlbum = album.getName();
			 			}else {
			 				CollAlbum = CollAlbum + "|" + album.getName();
			 			}
		 			}			 		
			 	}
			 	lastFMartist.setTopalbum(CollAlbum);

			 	
			 	Collection<String> GenreTags =	tmpArtist.getTags();
			 	String CollTag = null; 
			 	for(String TopTag : GenreTags) {
			 		if (TopTag != null) {
			 			if (CollTag == null) {
			 				CollTag = TopTag;
			 			}else {
			 				CollTag = CollTag + "|" + TopTag;
			 			}
		 			}
			 	}
			 	lastFMartist.setToptag(CollTag);

			 	
			 	for(String TopTag : GenreTags) {
			 		if (TopTag != null) {
			 			lastFMartist.setGenre(TopTag); break; }
			 	}
//			 	String[] sep = CollTag.split("\\|");
//			 	List list = Arrays.asList(sep);
			 	
			 	String tmpSum = tmpArtist.getWikiSummary();
			 	tmpSum = StringUtil.removeMarkup(tmpSum);
			 	
//			 	String tmpText = tmpArtist.getWikiText();
//			 	tmpText = StringUtil.removeMarkup(tmpText);
//			 	lastFMartist.setBio(tmpText);
			 	lastFMartist.setSummary(tmpSum);
			 	
//			 	Collection<Tag> TopTags =	 org.madsonic.lastfm.Artist.getTopTags(tmpArtist.getName(), api_key, 1);
			 	Collection<org.madsonic.lastfm.Artist> Similar = org.madsonic.lastfm.Artist.getSimilar(tmpArtist.getName(), 6, api_key);
			 	
			 	for (org.madsonic.lastfm.Artist x : Similar) {
			 		
			 		LastFMArtistSimilar s = new LastFMArtistSimilar();
			 		
			 		s.setArtistName(tmpArtist.getName());
			 		s.setArtistMbid(tmpArtist.getMbid());
			 		s.setSimilarName(x.getName());
			 		s.setSimilarMbid(x.getMbid());
			 		
			 		lastFMArtistSimilarDao.createOrUpdateLastFMArtistSimilar(s);
			 	}

//				/**
//				 * new Artist image importer workaround
//				 */
//			 	if (tmpArtist != null) {
//			 		lastFMartist.setCoverart1(tmpArtist.getImageURL(ImageSize.EXTRALARGE));
//			 	}
			 	
			 	// deprecated
			 	
//			 	PaginatedResult <Image> artistImage = org.madsonic.lastfm.Artist.getImages(RequestedArtist, 1, 5, api_key);
//			 	Collection <Image> Imgs = artistImage.getPageResults();
//
//			 	int counter = 0;
//			 	for (Image Img : Imgs)
//			 	{	 switch(counter)
//		             { case 0: lastFMartist.setCoverart1(Img.getImageURL(ImageSize.LARGESQUARE));break;
//		               case 1: lastFMartist.setCoverart2(Img.getImageURL(ImageSize.LARGESQUARE));break;
//		               case 2: lastFMartist.setCoverart3(Img.getImageURL(ImageSize.LARGESQUARE));break;
//		               case 3: lastFMartist.setCoverart4(Img.getImageURL(ImageSize.LARGESQUARE));break;
//		               case 4: lastFMartist.setCoverart5(Img.getImageURL(ImageSize.LARGESQUARE));break;
//		               }
//			 		counter++;
//			 	}
		 	
			 	
			 	if (lastFMartist.getArtistname() != null) {

			 		LOG.info("## LastFM ArtistInfo Update: " + lastFMartist.getArtistname());			 		
				 	lastFMArtistDao.createOrUpdateLastFMArtist(lastFMartist);
			 	}
			 	}

		        } catch (NullPointerException ex) {
			        System.out.println("## ERROR: " + artist.getName());
		        }
	 	}
		LOG.info("## LastFM ArtistScan Finished");	 	
	 	
	}

	public void getArtistInfo(LastFMArtist lastFMartist, String api_key){
	 	
		try {
						
			/// LastFM API
			org.madsonic.lastfm.Artist Artist = org.madsonic.lastfm.Artist.getCorrection(lastFMartist.getArtistname(), api_key);
			
		 	lastFMartist.setArtistname(Artist.getName());
		 	lastFMartist.setMbid(Artist.getMbid());
			
	        } catch (Exception x) {
	            LOG.warn("## Failed to Update ArtistCover: " + lastFMartist.getArtistname(), x);
	        }

 	}
	
	public void getArtistImage(MediaFile mediaFileArtist, String api_key){
	 	
		if (mediaFileArtist.getCoverArtPath() == null || mediaFileArtist == null) {
			try {
				/// LastFM API
				String artistName = org.madsonic.lastfm.Artist.getCorrection(mediaFileArtist.getArtist(), api_key).getName();
		    	org.madsonic.lastfm.Artist artist = org.madsonic.lastfm.Artist.getInfo(artistName, api_key);
		    	setCoverArtImage (mediaFileArtist.getId(), artist.getImageURL(ImageSize.MEGA), true);
				LOG.info("## Update ArtistCover: " + mediaFileArtist.getArtist());
	        } catch (Exception x) {
	            LOG.warn("## Failed to Update ArtistCover: " + mediaFileArtist.getArtist(), x);
	        }
		}
 	}

	public void getArtistBio(LastFMArtist lastFMartist, String api_key){
	 	
		try {
			/// LastFM API
			String artist = org.madsonic.lastfm.Artist.getCorrection(lastFMartist.getArtistname(), api_key).getName();
		 	String summary = getSummary(artist, api_key);
		 	lastFMartist.setSummary(summary);
		 	System.out.println("summary: ");
	        System.out.println(summary);

	        } catch (Exception x) {
	            LOG.warn("## Failed to Update ArtistCover: " + lastFMartist.getArtistname(), x);
	        }

 	}
	
    public static String getInfo(String artistName, String apiKey){
//    	org.madsonic.lastfm.Artist ArtistTest1 = org.madsonic.lastfm.Artist.getInfo(org.madsonic.lastfm.Artist.getCorrection("AC-DC", apiKey).getName(), apiKey);
    	org.madsonic.lastfm.Artist temp = org.madsonic.lastfm.Artist.getInfo(artistName, apiKey);
        return temp.getWikiSummary(); //.getWikiSummary(); //getWikiText(); //Also .getWikiSummary(), .getWikiLastChanged(), etc...
    }	
	
    public static String getSummary(String artistName, String apiKey){
    	org.madsonic.lastfm.Artist temp = org.madsonic.lastfm.Artist.getInfo(artistName, apiKey);
        return temp.getWikiSummary(); //.getWikiSummary(); //getWikiText(); //Also .getWikiSummary(), .getWikiLastChanged(), etc...
    }		

    public String setCoverArtImage(int id, String url, boolean isArtist) {
        try {
            MediaFile mediaFile = mediaFileService.getMediaFile(id);
            
            if (mediaFile.isAlbum() || mediaFile.isAlbumSet() ){
            	isArtist = false;
            }
            
            saveCoverArt(mediaFile.getPath(), url , isArtist);
            return null;
        } catch (Exception x) {
            LOG.warn("Failed to save cover art for media " + id, x);
            return x.toString();
        }
    }

    private void saveCoverArt(String path, String url, boolean isArtist) throws Exception {
        InputStream input = null;
        HttpClient client = new DefaultHttpClient();

        try {
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 20 * 1000); // 20 seconds
            HttpConnectionParams.setSoTimeout(client.getParams(), 20 * 1000); // 20 seconds
            HttpGet method = new HttpGet(url);

            org.apache.http.HttpResponse response = client.execute(method);
            input = response.getEntity().getContent();

            // Attempt to resolve proper suffix.
            String suffix = "jpg";
            if (url.toLowerCase().endsWith(".gif")) {
                suffix = "gif";
            } else if (url.toLowerCase().endsWith(".png")) {
                suffix = "png";
            }

            String coverName = "cover.";
            
            if (isArtist == true) {
            	coverName = "artist.";
            }
            
            // Check permissions.         
            File newCoverFile = new File(path, coverName + suffix);
            
            
            if (!securityService.isWriteAllowed(newCoverFile)) {
                throw new Exception("Permission denied: " + StringUtil.toHtml(newCoverFile.getPath()));
            }

            // If file exists, create a backup.
            backup(newCoverFile, new File(path, coverName + "backup." + suffix));

            // Write file.
            IOUtils.copy(input, new FileOutputStream(newCoverFile));

            MediaFile mediaFile = mediaFileService.getMediaFile(path);

            // Rename existing cover file if new cover file is not the preferred.
            try {
                File coverFile = mediaFileService.getCoverArt(mediaFile);
                if (coverFile != null) {
                    if (!newCoverFile.equals(coverFile)) {
                        coverFile.renameTo(new File(coverFile.getCanonicalPath() + ".old"));
                        LOG.info("Renamed old image file " + coverFile);
                    }
                }
            } catch (Exception x) {
                LOG.warn("Failed to rename existing cover file.", x);
            }

            mediaFileService.refreshMediaFile(mediaFile);

        } finally {
            IOUtils.closeQuietly(input);
            client.getConnectionManager().shutdown();
        }
    }

    private void backup(File newCoverFile, File backup) {
        if (newCoverFile.exists()) {
            if (backup.exists()) {
                backup.delete();
            }
            if (newCoverFile.renameTo(backup)) {
                LOG.info("Backed up old image file to " + backup);
            } else {
                LOG.warn("Failed to create image file backup " + backup);
            }
        }
    }
 
    public boolean getLastFMTopTrackSearch() {
    	return settingsService.getLastFMTopTrackSearch();
    }
    
    public void setLastFMTopTrackSearch(boolean b) {
    	settingsService.setLastFMTopTrackSearch(b);
        settingsService.save();
    }
    
    public void setMediaFileDao(MediaFileDao mediaFileDao) {
        this.mediaFileDao = mediaFileDao;
    }    
    
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }    
    
    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }
    
    public void setLastFMArtistDao(LastFMArtistDao lastFMArtistDao) {
        this.lastFMArtistDao = lastFMArtistDao;
    }
    
    public void setArtistDao(ArtistDao artistDao) {
        this.artistDao = artistDao;
    }       
    
    public void setLastFMArtistSimilarDao(LastFMArtistSimilarDao lastFMArtistSimilarDao) {
        this.lastFMArtistSimilarDao = lastFMArtistSimilarDao;
    }   
    
    public void setLastFMArtistTopTracksDao(LastFMArtistTopTracksDao lastFMArtistTopTracksDao) {
        this.lastFMArtistTopTracksDao = lastFMArtistTopTracksDao;
    }

	
}
