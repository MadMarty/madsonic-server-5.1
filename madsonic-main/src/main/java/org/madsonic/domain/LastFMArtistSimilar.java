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

public class LastFMArtistSimilar {

	private int id;
	private String artistName;
	private String artistMbid;	
	private String similarName;
	private String similarMbid;	

	public LastFMArtistSimilar() {
    }
    
    public LastFMArtistSimilar(int id, String artistName, String artistMbid, String similarName, String similarMbid) {
        this.id = id;
        this.artistName = artistName;
        this.artistMbid = artistMbid;
        this.similarName = similarName;
        this.similarMbid = similarMbid;
    }

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getArtistMbid() {
		return artistMbid;
	}

	public void setArtistMbid(String artistMbid) {
		this.artistMbid = artistMbid;
	}

	public String getSimilarName() {
		return similarName;
	}

	public void setSimilarName(String similarName) {
		this.similarName = similarName;
	}

	public String getSimilarMbid() {
		return similarMbid;
	}

	public void setSimilarMbid(String similarMbid) {
		this.similarMbid = similarMbid;
	}
}
