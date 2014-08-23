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
package org.madsonic.domain;

import org.madsonic.Logger;

/**
 * Contains media libaray statistics, including the number of artists, albums and songs.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.1 $ $Date: 2005/11/17 18:29:03 $
 */
public class MusicFolderStatistics {

    private static final Logger LOG = Logger.getLogger(MusicFolderStatistics.class);

    private int id;
    private int musicFolderId;

    private int artistCount;
    private int albumArtistCount;
    private int albumCount;
    private int genreCount;
    
    private int songCount;
    private int videoCount;
    private int podcastCount;
    private int audiobookCount;

    private long songSize;
    private long videoSize;
    private long podcastSize;
    private long audiobookSize;

    public MusicFolderStatistics(int id, 
    							 int musicFolderId, 
    							 int artistCount, 
    							 int albumArtistCount, 
    							 int albumCount, 
    							 int genreCount,
					    		 int songCount, 
					    		 long songSize,  
					    		 int videoCount, 
					    		 long videoSize,  
					    		 int podcastCount, 
					    		 long podcastSize,  
					    		 int audiobookCount, 
					    		 long audiobookSize) {

    	this.id = id; 
    	this.musicFolderId = musicFolderId; 
    	
    	this.artistCount = artistCount;
        this.albumArtistCount = albumArtistCount;
        this.albumCount = albumCount;
        this.genreCount = genreCount;

        this.songCount = songCount;
        this.videoCount = videoCount;
        this.podcastCount = podcastCount;
        this.audiobookCount = audiobookCount;
        
        this.songSize = songSize;
        this.videoSize = videoSize;
        this.podcastSize = podcastSize;
        this.audiobookSize = audiobookSize;
        
    }

    public MusicFolderStatistics() {
    }

    public void reset() {
    	
        artistCount = 0;
        albumArtistCount = 0;
        albumCount = 0;
    	genreCount = 0;

    	songCount = 0;
        videoCount = 0;
        podcastCount = 0;
        audiobookCount = 0;
        
    	songSize = 0;
        videoSize = 0;
        podcastSize = 0;
        audiobookSize = 0;
    }

    public void incrementArtists(int n) {
        artistCount += n;
    }

    public void incrementAlbumArtists(int n) {
    	albumArtistCount += n;
    }    
    
    public void incrementAlbums(int n) {
        albumCount += n;
    }

    public void incrementSongs(int n) {
        songCount += n;
    }

    public void incrementPodcast(int n) {
        podcastCount += n;
    }

    public void incrementAudiobook(int n) {
        audiobookCount += n;
    }

    public void incrementVideo(int n) {
        videoCount += n;
    }
    
    public void incrementSongsSize(Long n) {
        songSize += n;
    }

    public void incrementPodcastSize(Long n) {
        podcastSize += n;
    }

    public void incrementAudiobookSize(Long n) {
        audiobookSize += n;
    }

    public void incrementVideoSize(Long n) {
        videoSize += n;
    }
    
    public int getArtistCount() {
        return artistCount;
    }

    public int getAlbumCount() {
        return albumCount;
    }

    public void setAlbumCount(int n) {
        albumCount = n;
    }

    
    public int getSongCount() {
        return songCount;
    }

	public int getAlbumArtistCount() {
		return albumArtistCount;
	}

	public void setAlbumArtistCount(int albumArtistCount) {
		this.albumArtistCount = albumArtistCount;
	}

	public int getGenreCount() {
		return genreCount;
	}

	public void setGenreCount(int genreCount) {
		this.genreCount = genreCount;
	}

	public int getVideoCount() {
		return videoCount;
	}

	public void setVideoCount(int videoCount) {
		this.videoCount = videoCount;
	}

	public int getPodcastCount() {
		return podcastCount;
	}

	public void setPodcastCount(int podcastCount) {
		this.podcastCount = podcastCount;
	}

	public int getAudiobookCount() {
		return audiobookCount;
	}

	public void setAudiobookCount(int audiobookCount) {
		this.audiobookCount = audiobookCount;
	}

	public long getSongSize() {
		return songSize;
	}

	public void setSongSize(long songSize) {
		this.songSize = songSize;
	}

	public long getVideoSize() {
		return videoSize;
	}

	public void setVideoSize(long videoSize) {
		this.videoSize = videoSize;
	}

	public long getPodcastSize() {
		return podcastSize;
	}

	public void setPodcastSize(long podcastSize) {
		this.podcastSize = podcastSize;
	}

	public long getAudiobookSize() {
		return audiobookSize;
	}

	public void setAudiobookSize(long audiobookSize) {
		this.audiobookSize = audiobookSize;
	}

	public int getMusicFolderId() {
		return musicFolderId;
	}

	public void setMusicFolderId(int musicFolderId) {
		this.musicFolderId = musicFolderId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
