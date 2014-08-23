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

import java.util.Date;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public class Album {

    private int id;
    private String path;
    private String name;
    private String nameid3;
    private String SetName;
    private String artist;
    private String albumartist;
    private int songCount;
    private int durationSeconds;
    private String coverArtPath;
    private int playCount;
    private Date lastPlayed;
    private String comment;
    private Date created;
    private Date lastScanned;
    private boolean present;
    private int mediaFileId;
    private String genre;
    private int year;
    
    public Album() {
    }

    public Album(int id, String path, String name, String nameid3, String SetName, String artist, String albumartist, int songCount, int durationSeconds, String coverArtPath,
            int playCount, Date lastPlayed, String comment, Date created, Date lastScanned, boolean present, int mediaFileId, String genre, int year) {
        this.id = id;
        this.path = path;
        this.name = name;
        this.nameid3 =nameid3;
        this.SetName = SetName;
        this.artist = artist;
        this.albumartist = albumartist;
        this.songCount = songCount;
        this.durationSeconds = durationSeconds;
        this.coverArtPath = coverArtPath;
        this.playCount = playCount;
        this.lastPlayed = lastPlayed;
        this.comment = comment;
        this.created = created;
        this.lastScanned = lastScanned;
        this.present = present;
        this.mediaFileId = mediaFileId;
        this.genre = genre;
        this.year = year;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getSetName() {
        return SetName;
    }

    public void setSetName(String name) {
        this.SetName = name;
    }
    
    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumartist() {
		return albumartist;
	}

	public void setAlbumartist(String albumartist) {
		this.albumartist = albumartist;
	}

	public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getCoverArtPath() {
        return coverArtPath;
    }

    public void setCoverArtPath(String coverArtPath) {
        this.coverArtPath = coverArtPath;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public Date getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(Date lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastScanned() {
        return lastScanned;
    }

    public void setLastScanned(Date lastScanned) {
        this.lastScanned = lastScanned;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

	public int getMediaFileId() {
		return mediaFileId;
	}

	public void setMediaFileId(int mediaFileId) {
		this.mediaFileId = mediaFileId;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getNameid3() {
		return nameid3;
	}

	public void setNameid3(String nameid3) {
		this.nameid3 = nameid3;
	}

	public void setRating(int round) {
		// TODO Auto-generated method stub
	}
}
