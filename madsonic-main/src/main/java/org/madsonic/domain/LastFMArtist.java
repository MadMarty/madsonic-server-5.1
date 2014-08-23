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
 * @author Sindre Mehus
 * @version $Id$
 */
public class LastFMArtist {

	private int id;
	private String artistname;
	private String mbid;
	private String since;
	private String genre;
	private String url;
	private String fanart;
	private String coverart1;
	private String coverart2;
	private String coverart3;
	private String coverart4;
	private String coverart5;
	private String toptag;
	private String topalbum;
	private String bio;
	private String summary;
	private int playCount;
	
    public LastFMArtist() {
    }
    
    public LastFMArtist(int id, String artistname, String mbid, String since, String genre, String url, String fanart, String coverart1, String coverart2, String coverart3, String coverart4, String coverart5, String toptag, String topalbum, String bio, String summary, int playCount) {
        this.id = id;
        this.setArtistname(artistname);
        this.setMbid(mbid);
        this.setSince(since);
        this.setGenre(genre);
        this.setUrl(url);
        this.setFanart(fanart);
        this.setCoverart1(coverart1);
        this.setCoverart2(coverart2);
        this.setCoverart3(coverart3);
        this.setCoverart4(coverart4);
        this.setCoverart5(coverart5);
        this.setToptag(toptag);
        this.setTopalbum(topalbum);
        this.setBio(bio);        
        this.setSummary(summary);
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

	public String getMbid() {
		return mbid;
	}

	public void setMbid(String mbid) {
		this.mbid = mbid;
	}

	public String getSince() {
		return since;
	}

	public void setSince(String since) {
		this.since = since;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFanart() {
		return fanart;
	}

	public void setFanart(String fanart) {
		this.fanart = fanart;
	}

	public String getCoverart1() {
		return coverart1;
	}

	public void setCoverart1(String coverart1) {
		this.coverart1 = coverart1;
	}

	public String getCoverart2() {
		return coverart2;
	}

	public void setCoverart2(String coverart2) {
		this.coverart2 = coverart2;
	}

	public String getCoverart3() {
		return coverart3;
	}

	public void setCoverart3(String coverart3) {
		this.coverart3 = coverart3;
	}

	public String getToptag() {
		return toptag;
	}

	public void setToptag(String toptag) {
		this.toptag = toptag;
	}

	public String getTopalbum() {
		return topalbum;
	}

	public void setTopalbum(String topalbum) {
		this.topalbum = topalbum;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public int getPlayCount() {
		return playCount;
	}

	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}

	public String getCoverart4() {
		return coverart4;
	}

	public void setCoverart4(String coverart4) {
		this.coverart4 = coverart4;
	}

	public String getCoverart5() {
		return coverart5;
	}

	public void setCoverart5(String coverart5) {
		this.coverart5 = coverart5;
	}
}
