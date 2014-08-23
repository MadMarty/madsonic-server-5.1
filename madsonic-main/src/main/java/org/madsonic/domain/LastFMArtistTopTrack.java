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

 Copyright 2014 (C) Madevil
 */
package org.madsonic.domain;

/**
 * @author Madevil
 */
public class LastFMArtistTopTrack {

	private int id;
	private String artistname;
	private String alternate;	
	private String artistmbid;
	private String songname;
	private String songmbid;
	private String coverart;
	private int rank;
	private int playCount;
	
    public LastFMArtistTopTrack() {
    }
    
    public LastFMArtistTopTrack(int id, String artistname, String alternate, String artistmbid, String songname, String songmbid, String coverart, int rank, int playCount) {
        this.id = id;
        this.setArtistname(artistname);
        this.setAlternate(alternate);
        this.setArtistmbid(artistmbid);
        this.setSongname(songname);
        this.setSongmbid(songmbid);
        this.setCoverart(coverart);
        this.setRank(rank);
        this.setPlayCount(playCount);
    }

	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

	public String getArtistname() {
		return artistname;
	}

	public void setArtistname(String artistname) {
		this.artistname = artistname;
	}

	public String getAlternate() {
		return alternate;
	}

	public void setAlternate(String alternate) {
		this.alternate = alternate;
	}

	public String getArtistmbid() {
		return artistmbid;
	}

	public void setArtistmbid(String artistmbid) {
		this.artistmbid = artistmbid;
	}

	public int getPlayCount() {
		return playCount;
	}

	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}

	public String getSongname() {
		return songname;
	}

	public void setSongname(String songname) {
		this.songname = songname;
	}

    public String getSongmbid() {
		return songmbid;
	}

	public void setSongmbid(String songmbid) {
		this.songmbid = songmbid;
	}

	private void setCoverart(String coverart) {
    	this.coverart = coverart;
	}

	public String getCoverart() {
		return coverart;
	}   
	
	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

}
