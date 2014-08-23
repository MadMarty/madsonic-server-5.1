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

public class LastFMAlbumSimilar {

	private String albumName;
	private String albumMbid;	
	private int mediaFileId;	

	public LastFMAlbumSimilar() {
    }
    
    public LastFMAlbumSimilar(String albumName, String albumMbid, int mediaFileId) {
        this.setAlbumName(albumName);
        this.setAlbumMbid(albumMbid);
        this.setMediaFileId(mediaFileId);
    }

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public String getAlbumMbid() {
		return albumMbid;
	}

	public void setAlbumMbid(String albumMbid) {
		this.albumMbid = albumMbid;
	}

	public int getMediaFileId() {
		return mediaFileId;
	}

	public void setMediaFileId(int mediaFileId) {
		this.mediaFileId = mediaFileId;
	}
}
