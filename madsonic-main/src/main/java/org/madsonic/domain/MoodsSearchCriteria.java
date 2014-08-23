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
 * Defines criteria used when generating random playlists.
 *
 * @author Sindre Mehus
 * @see org.madsonic.service.SearchService#getRandomSongs
 */
public class MoodsSearchCriteria {
    private final int count;
    private final String mood;
    private final String [] moods;
    private final Integer fromYear;
    private final Integer toYear;
    private final Integer musicFolderId;
    private final Integer userGroupId;

    /**
     * Creates a new instance.
     *
     * @param count         Maximum number of songs to return.
     * @param mood          Only return songs of the given mood. May be <code>null</code>.
     * @param moods         Only return songs of the given moods. May be <code>null</code>.
     * @param fromYear      Only return songs released after (or in) this year. May be <code>null</code>.
     * @param toYear        Only return songs released before (or in) this year. May be <code>null</code>.
     * @param musicFolderId Only return songs from this music folder. May be <code>null</code>.
     * @param userGroupId   Only return songs from this group. May be <code>null</code>.
     */
    public MoodsSearchCriteria(int count, String mood, String[] moods, Integer fromYear, Integer toYear, Integer musicFolderId, Integer userGroupId ) {
        this.count = count;
        this.mood = mood;
        this.moods = moods;
        this.fromYear = fromYear;
        this.toYear = toYear;
        this.musicFolderId = musicFolderId;
        this.userGroupId = userGroupId;
    }

    public int getCount() {
        return count;
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

	public String getMood() {
		return mood;
	}

	
}
