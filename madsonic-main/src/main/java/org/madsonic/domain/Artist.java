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
public class Artist {

    private int id;
    private String name;
    private String genre;
    private String coverArtPath;
    private String artistFolder;
    private int albumCount;
    private int playCount;
    private int songCount;
    private int topPlayFound;
    private int topPlayCount;
    private Date lastScanned;
    private boolean present;

    public Artist() {
    }

    public Artist(int id, String name, String genre, String coverArtPath, String artistFolder, int albumCount, int playCount, int songCount, int topPlayFound, int topPlayCount, Date lastScanned, boolean present) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.coverArtPath = coverArtPath;
        this.artistFolder = artistFolder;
        this.albumCount = albumCount;
        this.playCount = playCount;
        this.songCount = songCount;
        this.topPlayFound = topPlayFound;
        this.topPlayCount = topPlayCount;
        this.lastScanned = lastScanned;
        this.present = present;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverArtPath() {
        return coverArtPath;
    }

    public void setCoverArtPath(String coverArtPath) {
        this.coverArtPath = coverArtPath;
    }

    public int getAlbumCount() {
        return albumCount;
    }

    public void setAlbumCount(int albumCount) {
        this.albumCount = albumCount;
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

	/**
	 * @return the artistFolder
	 */
	public String getArtistFolder() {
		return artistFolder;
	}

	/**
	 * @param artistFolder the artistFolder to set
	 */
	public void setArtistFolder(String artistFolder) {
		this.artistFolder = artistFolder;
	}

	/**
	 * @return the playCount
	 */
	public int getPlayCount() {
		return playCount;
	}

	/**
	 * @param playCount the playCount to set
	 */
	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}

	/**
	 * @return the songCount
	 */
	public int getSongCount() {
		return songCount;
	}

	/**
	 * @param songCount the songCount to set
	 */
	public void setSongCount(int songCount) {
		this.songCount = songCount;
	}
	
    public void incrementPlay(int n) {
        playCount += n;
    }

    public void incrementSongs(int n) {
        songCount += n;
    }

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	/**
	 * @return the topPlayFound
	 */
	public int getTopPlayFound() {
		return topPlayFound;
	}

	/**
	 * @param topPlayFound the topPlayFound to set
	 */
	public void setTopPlayFound(int topPlayFound) {
		this.topPlayFound = topPlayFound;
	}

	/**
	 * @return the topPlayCount
	 */
	public int getTopPlayCount() {
		return topPlayCount;
	}

	/**
	 * @param topPlayCount the topPlayCount to set
	 */
	public void setTopPlayCount(int topPlayCount) {
		this.topPlayCount = topPlayCount;
	}	
	
	
	
}
