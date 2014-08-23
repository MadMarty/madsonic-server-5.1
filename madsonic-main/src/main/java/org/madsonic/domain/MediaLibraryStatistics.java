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
import org.madsonic.util.StringUtil;

/**
 * Contains media libaray statistics, including the number of artists, albums and songs.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.1 $ $Date: 2005/11/17 18:29:03 $
 */
public class MediaLibraryStatistics {

    private static final Logger LOG = Logger.getLogger(MediaLibraryStatistics.class);

    private int artistCount;
    private int albumArtistCount;
    private int albumCount;
    private int genreCount;
    private int songCount;
    private int videoCount;
    private int podcastCount;
    private int audiobookCount;
    private long totalLengthInBytes;
    private long totalDurationInSeconds;

    public MediaLibraryStatistics(int artistCount, int albumArtistCount, int albumCount, int genreCount, int songCount, int videoCount, int podcastCount, int audiobookCount, long totalLengthInBytes, long totalDurationInSeconds) {

    	this.artistCount = artistCount;
        this.albumArtistCount = albumArtistCount;
        this.albumCount = albumCount;
        this.genreCount = genreCount;
        this.songCount = songCount;
        this.videoCount = videoCount;
        this.podcastCount = podcastCount;
        this.audiobookCount = audiobookCount;
        this.totalLengthInBytes = totalLengthInBytes;
        this.totalDurationInSeconds = totalDurationInSeconds;
    }

    public MediaLibraryStatistics() {
    }

    public void reset() {
    	
        artistCount = 0;
        albumArtistCount = 0;
        albumCount = 0;
    	albumCount = 0;
        songCount = 0;
        videoCount = 0;
        podcastCount = 0;
        audiobookCount = 0;
        totalLengthInBytes = 0;
        totalDurationInSeconds = 0;
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
    
    public void incrementTotalLengthInBytes(long n) {
        totalLengthInBytes += n;
    }

    public void incrementTotalDurationInSeconds(long n) {
        totalDurationInSeconds += n;
    }

    public int getArtistCount() {
        return artistCount;
    }

    public int getAlbumCount() {
        return albumCount;
    }

    public int getSongCount() {
        return songCount;
    }

    public long getTotalLengthInBytes() {
        return totalLengthInBytes;
    }

    public long getTotalDurationInSeconds() {
        return totalDurationInSeconds;
    }

    public String format() {
        return artistCount + " " + albumArtistCount + " " + albumCount + " " + genreCount + " " + songCount + " " + videoCount + " " + podcastCount + " " + audiobookCount + " " + totalLengthInBytes + " " + totalDurationInSeconds;
    }

    public static MediaLibraryStatistics parse(String s) {
        try {
            String[] strings = StringUtil.split(s);
            return new MediaLibraryStatistics(
                    Integer.parseInt(strings[0]),
                    Integer.parseInt(strings[1]),
                    Integer.parseInt(strings[2]),
                    Integer.parseInt(strings[3]),
                    Integer.parseInt(strings[4]),
                    Integer.parseInt(strings[5]),
                    Integer.parseInt(strings[6]),
                    Integer.parseInt(strings[7]),
                    Long.parseLong(strings[8]),
                    Long.parseLong(strings[9]));
        } catch (Exception e) {
            LOG.warn("Failed to parse media library statistics: " + s);
            return new MediaLibraryStatistics();
        }
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
}
