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

/**
 * Defines criteria used when generating pandora playlist.
 *
 * @author Madevil
 * @see org.madsonic.service.SearchService#getRandomSongs
 */
public class MultiSearchCriteria {
	
	private final Integer count;
	
    private final String [] artists;
    private final String [] albums;
    private final String [] genres;
    private final String [] moods;
    private final Integer fromYear;
    private final Integer toYear;
    private final Integer musicFolderId;
    private final Integer userGroupId;

    /**
     * Creates a new instance.
     *
     * @param count 		Only return count. May be <code>null</code>.
     * @param artists		Only return songs of the given artist. May be <code>null</code>.
     * @param albums        Only return songs of the given album. May be <code>null</code>.
     * @param genres        Only return songs of the given genres. May be <code>null</code>.
     * @param moods         Only return songs of the given moods. May be <code>null</code>.
     * @param fromYear      Only return songs released after (or in) this year. May be <code>null</code>.
     * @param toYear        Only return songs released before (or in) this year. May be <code>null</code>.
     * @param musicFolderId Only return songs from this music folder. May be <code>null</code>.
     * @param userGroupId   Only return songs from this group. May be <code>null</code>.
     */
    public MultiSearchCriteria(int count, String[] artists, String[] albums, String[] genres, String[] moods, Integer fromYear, Integer toYear, Integer musicFolderId, Integer userGroupId) {

    	this.count = count;
        this.artists = artists;
        this.albums = albums;
        this.genres = genres;
        this.moods = moods;
        this.fromYear = fromYear;
        this.toYear = toYear;
        this.musicFolderId = musicFolderId;
        this.userGroupId = userGroupId;
    }

	public String [] getGenres() {
		return genres;
	}

	public Integer getFromYear() {
        return fromYear;
    }

    public Integer getToYear() {
        return toYear;
    }

    public Integer getMusicFolderId() {
        return musicFolderId;
    }

	public Integer getUserGroupId() {
		return userGroupId;
	}

	public String [] getMoods() {
		return moods;
	}

	public String [] getArtists() {
		return artists;
	}

	public String [] getAlbums() {
		return albums;
	}

	public Integer getCount() {
		return count;
	}
}
