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
 * Defines criteria used when generating random playlists.
 *
 * @author Sindre Mehus
 * @see org.madsonic.service.SearchService#getRandomSongs
 */
public class GenreSearchCriteria {
    private final int count;
    private final String genre;
    private final String [] genres;
    private final Integer fromYear;
    private final Integer toYear;
    private final Integer musicFolderId;
    private final Integer userGroupId;

    /**
     * Creates a new instance.
     *
     * @param count         Maximum number of songs to return.
     * @param genre         Only return songs of the given genre. May be <code>null</code>.
     * @param genres        Only return songs of the given genres. May be <code>null</code>.
     * @param fromYear      Only return songs released after (or in) this year. May be <code>null</code>.
     * @param toYear        Only return songs released before (or in) this year. May be <code>null</code>.
     * @param musicFolderId Only return songs from this music folder. May be <code>null</code>.
     * @param userGroupId   Only return songs from this group. May be <code>null</code>.
     */
    public GenreSearchCriteria(int count, String genre, String[] genres, Integer fromYear, Integer toYear, Integer musicFolderId, Integer userGroupId ) {
        this.count = count;
        this.genre = genre;
        this.genres = genres;
        this.fromYear = fromYear;
        this.toYear = toYear;
        this.musicFolderId = musicFolderId;
        this.userGroupId = userGroupId;
    }

    public int getCount() {
        return count;
    }

    public String getGenre() {
        return genre;
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

	
}
