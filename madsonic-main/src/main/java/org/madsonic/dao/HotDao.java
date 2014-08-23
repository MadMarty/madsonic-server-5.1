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
package org.madsonic.dao;

import java.util.ArrayList;
import java.util.List;

import org.madsonic.domain.MediaFile;
import org.springframework.dao.EmptyResultDataAccessException;

/**
 * Provides database services for hot ratings.
 *
 * @author Madevil
 */
public class HotDao extends AbstractDao {

    /**
     * Sets the hot rating for a media file and a given user.
     *
     * @param username  The user name.
     * @param mediaFile The media file.
     */
    public void setHotFlag(String username, MediaFile mediaFile) {

        update("delete from hot_rating where username=? and path=?", username, mediaFile.getPath());
        update("insert into hot_rating values(?, ?, ?)", username, mediaFile.getPath(), mediaFile.getId());
    }

	public Integer getHotFlag(MediaFile mediaFile) {
        try {
            return getJdbcTemplate().queryForInt("select id from hot_rating where path=?", new Object[]{mediaFile.getPath()});
        } catch (EmptyResultDataAccessException x) {
            return 0;
        }
	}

    public void deleteHotFlag(MediaFile mediaFile) {
      update("delete from hot_rating where path=?", mediaFile.getPath());
    }

    /**
     * Returns paths for the highest rated music files.
     *
     * @param offset Number of files to skip.
     * @param count  Maximum number of files to return.
     * @return Paths for the highest rated music files.
     */
    public List<String> getHotRated(int offset, int count) {
        if (count < 1) {
            return new ArrayList<String>();
        }

        String sql = "select path from hot_rating " +
                "group by path " +
                "order by path desc limit " + count + " offset " + offset;
        return queryForStrings(sql);
    }    

    public List<String> getRandomHotRated(int offset, int count) {
        if (count < 1) {
            return new ArrayList<String>();
        }

        String sql = "select path from hot_rating group by path " +
                " limit " + count + " offset " + offset;
        return queryForStrings(sql);
    }  


	public Integer getCountHotFlag() {
        try {
            return getJdbcTemplate().queryForInt("SELECT count(*) FROM hot_rating");
        }   catch (EmptyResultDataAccessException x) {
            return 0;
        }
	}

	
}
