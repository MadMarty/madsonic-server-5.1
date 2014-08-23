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
import org.madsonic.domain.LastFMArtistSimilar;

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
public class LastFMArtistSimilarDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(LastFMArtistSimilarDao.class);
    
    private static final String COLUMNS = "id, artist_name, artist_mbid, similar_name, similar_mbid";

    private final RowMapper rowMapper = new LastFMArtistSimilarMapper();

//    public List<LastFMArtistSimilar> getAllSimilar() {
//        return query("select " + COLUMNS + " from lastfm_artist_similar order by name", rowMapper);
//    }    
    
    public LastFMArtistSimilar getSimilar(String artistName) {
        return queryOne("select " + COLUMNS + " from lastfm_artist_similar where lower(artist_name)=?", rowMapper, artistName.toLowerCase());
    }
    
    public List<String> getSimilarArtist(String ArtistName){
        return queryForStrings("select distinct SIMILAR_NAME from lastfm_artist_similar where lower(artist_name)=?", ArtistName.toLowerCase());
    }

    public LastFMArtistSimilar getSimilar(int mbid) {
        return queryOne("select " + COLUMNS + " from lastfm_artist_similar where mbid=?", rowMapper, mbid);
    }

   
    public synchronized void createOrUpdateLastFMArtistSimilar(LastFMArtistSimilar lastFMArtistSimilar) {
        String sql ="update lastfm_artist_similar set " +
	                "artist_name=?," +
	                "artist_mbid=?," +
	                "similar_name=?, " +
	                "similar_mbid=? " +
	                "where artist_name=? and similar_name=?";

        int n = update(sql, lastFMArtistSimilar.getArtistName(), lastFMArtistSimilar.getArtistMbid(), lastFMArtistSimilar.getSimilarName(), lastFMArtistSimilar.getSimilarMbid(), lastFMArtistSimilar.getArtistName(), lastFMArtistSimilar.getSimilarName());
        
        if (n == 0) {

            update("insert into lastfm_artist_similar (" + COLUMNS + ") values (" + questionMarks(COLUMNS) + ")", null,
            		lastFMArtistSimilar.getArtistName(), lastFMArtistSimilar.getArtistMbid(), lastFMArtistSimilar.getSimilarName(), lastFMArtistSimilar.getSimilarMbid());
        }

      //  int id = queryForInt("select id from lastfm_artist where artistname=?", null, lastFMArtist.getArtistname());
     //   lastFMArtist.setId(id);
    }


    private static class LastFMArtistSimilarMapper implements ParameterizedRowMapper<LastFMArtistSimilar> {
        public LastFMArtistSimilar mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new LastFMArtistSimilar(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5));
        }
    }
}
