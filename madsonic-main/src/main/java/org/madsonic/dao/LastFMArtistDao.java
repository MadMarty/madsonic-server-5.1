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

 Copyright 2013 (C) Madevil
 */
package org.madsonic.dao;

import org.madsonic.Logger;
import org.madsonic.domain.LastFMArtist;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Provides database services for artists.
 *
 * @author Sindre Mehus
 */ 
public class LastFMArtistDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(LastFMArtistDao.class);
    
    private static final String COLUMNS = "id, artistname, mbid, since, genre, url, fanart, coverart1, coverart2, coverart3, coverart4, coverart5, toptag, topalbum, bio, summary, play_count";

    private final RowMapper rowMapper = new LastFMArtistMapper();

    public List<LastFMArtist> getAllArtists() {
        return query("select " + COLUMNS + " from lastfm_artist order by name", rowMapper);
    }    
    
    public LastFMArtist getArtist(String artistName) {
        return queryOne("select " + COLUMNS + " from lastfm_artist where lower(artistName)=?", rowMapper, artistName.toLowerCase());
    }

    public LastFMArtist getArtist(int mbid) {
        return queryOne("select " + COLUMNS + " from lastfm_artist where mbid=?", rowMapper, mbid);
    }

    public void CleanupArtist() {
        update("DELETE FROM lastfm_artist where mbid=''");
    }

    public synchronized void createOrUpdateLastFMArtist(LastFMArtist lastFMArtist) {
        String sql ="update lastfm_artist set " +
	                "artistname=?," +
	                "mbid=?," +
	                "since=?," +
	                "genre=?," +
	                "url=?," +
	                "fanart=?," +
	                "coverart1=?, " +
	                "coverart2=?, " +
	                "coverart3=?, " +
	                "coverart4=?, " +
	                "coverart5=?, " +
	                "toptag=?, " +
	                "topalbum=?, " +
	                "bio=?, " +
	                "summary=?, " +
	                "play_count=? " +
	                "where artistname=?";

        int n = update(sql, lastFMArtist.getArtistname(), lastFMArtist.getMbid(), lastFMArtist.getSince(), lastFMArtist.getGenre(), lastFMArtist.getUrl(), lastFMArtist.getFanart(), lastFMArtist.getCoverart1(), lastFMArtist.getCoverart2(), lastFMArtist.getCoverart3(), lastFMArtist.getCoverart4(), lastFMArtist.getCoverart5(), lastFMArtist.getToptag(), lastFMArtist.getTopalbum(), lastFMArtist.getBio(), lastFMArtist.getSummary(), lastFMArtist.getPlayCount(), lastFMArtist.getArtistname());
        
        if (n == 0) {

            update("insert into lastfm_artist (" + COLUMNS + ") values (" + questionMarks(COLUMNS) + ")", null,
            		lastFMArtist.getArtistname(), lastFMArtist.getMbid(), lastFMArtist.getSince(),
            		lastFMArtist.getGenre(), lastFMArtist.getUrl(), lastFMArtist.getFanart(), lastFMArtist.getCoverart1(),
            		lastFMArtist.getCoverart2(), lastFMArtist.getCoverart3(), lastFMArtist.getCoverart4(), lastFMArtist.getCoverart5(), lastFMArtist.getToptag(), lastFMArtist.getTopalbum(), 
            		lastFMArtist.getBio(), lastFMArtist.getSummary(), lastFMArtist.getPlayCount());
        }

      //  int id = queryForInt("select id from lastfm_artist where artistname=?", null, lastFMArtist.getArtistname());
     //   lastFMArtist.setId(id);
    }


    private static class LastFMArtistMapper implements ParameterizedRowMapper<LastFMArtist> {
        public LastFMArtist mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new LastFMArtist(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getString(7),
                    rs.getString(8),
                    rs.getString(9),
                    rs.getString(10),
                    rs.getString(11),
                    rs.getString(12),
                    rs.getString(13),
                    rs.getString(14),
                    rs.getString(15),
                    rs.getString(16),
                    rs.getInt(17));
        }
    }
}
