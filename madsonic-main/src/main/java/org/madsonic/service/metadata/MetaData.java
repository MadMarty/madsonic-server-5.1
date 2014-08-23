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
package org.madsonic.service.metadata;

import org.jaudiotagger.tag.FieldKey;

/**
 * Contains meta-data (song title, artist, album etc) for a music file.
 * @author Sindre Mehus
 */
public class MetaData {

    private Integer discNumber;
    private Integer trackNumber;
    private String title;
    private String artist;
    private String albumartist;
    private String albumName;
    private String genre;
    private String mood;
    private Integer year;
    private Integer bitRate;
    private boolean variableBitRate;
    private Integer durationSeconds;
    private String lyrics;
	private boolean hasLyrics;
    private Integer width;
    private Integer height;

    private String MBTrackId;
    private String MBArtistId;
    private String MBReleaseArtistId;
    private String MBReleaseId;
    
    public Integer getDiscNumber() {
        return discNumber;
    }

    public void setDiscNumber(Integer discNumber) {
        this.discNumber = discNumber;
    }

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(Integer trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

	public String getAlbumArtist() {
		return albumartist;
	}
	
    public void setAlbumArtist(String albumartist) {
        this.albumartist = albumartist;
    }

	
    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

	public boolean hasLyrics() {
		return hasLyrics;
	}

	public void setHasLyrics(boolean hasLyrics) {
		this.hasLyrics = hasLyrics;
	}

    public Integer getBitRate() {
        return bitRate;
    }

    public void setBitRate(Integer bitRate) {
        this.bitRate = bitRate;
    }

    public boolean getVariableBitRate() {
        return variableBitRate;
    }

    public void setVariableBitRate(boolean variableBitRate) {
        this.variableBitRate = variableBitRate;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

	public String getMood() {
		return mood;
	}

	public void setMood(String mood) {
		this.mood = mood;
	}

	/**
	 * @return the mBTrackId
	 */
	public String getMBTrackId() {
		return MBTrackId;
	}

	/**
	 * @param mBTrackId the mBTrackId to set
	 */
	public void setMBTrackId(String mBTrackId) {
		MBTrackId = mBTrackId;
	}

	/**
	 * @return the mBArtistId
	 */
	public String getMBArtistId() {
		return MBArtistId;
	}

	/**
	 * @param mBArtistId the mBArtistId to set
	 */
	public void setMBArtistId(String mBArtistId) {
		MBArtistId = mBArtistId;
	}

	/**
	 * @return the mBReleaseArtistId
	 */
	public String getMBReleaseArtistId() {
		return MBReleaseArtistId;
	}

	/**
	 * @param mBReleaseArtistId the mBReleaseArtistId to set
	 */
	public void setMBReleaseArtistId(String mBReleaseArtistId) {
		MBReleaseArtistId = mBReleaseArtistId;
	}

	/**
	 * @return the mBReleaseId
	 */
	public String getMBReleaseId() {
		return MBReleaseId;
	}

	/**
	 * @param mBReleaseId the mBReleaseId to set
	 */
	public void setMBReleaseId(String mBReleaseId) {
		MBReleaseId = mBReleaseId;
	}

	public String getLyrics() {
		return lyrics;
	}

	public void setLyrics(String lyrics) {
		this.lyrics = lyrics;
	}
}
