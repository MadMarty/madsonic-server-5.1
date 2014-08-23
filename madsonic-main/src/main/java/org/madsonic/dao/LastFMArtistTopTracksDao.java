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
import org.madsonic.domain.LastFMArtistTopTrack;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Provides database services for artists.
 *
 * @author Sindre Mehus
 */
public class LastFMArtistTopTracksDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(LastFMArtistTopTracksDao.class);
    
    private static final String COLUMNS = "id, artistname, alternate, artistmbid, songname, songmbid, coverart, rank, play_count";

    private final RowMapper rowMapper = new LastFMArtistTopTracksMapper();

    public List<LastFMArtistTopTrack> getTopTrack(String artistName, String alternate) {
        return query("select " + COLUMNS + " from lastfm_artist_toptracks where lower(artistname)=? or lower(alternate)=?", rowMapper, artistName.toLowerCase(), alternate.toLowerCase());
    }
    
    public LastFMArtistTopTrack getTopTrack(String mbid) {
        return queryOne("select " + COLUMNS + " from lastfm_artist_toptracks where mbid=?", rowMapper, mbid);
    }
        
    public List<LastFMArtistTopTrack> getAllTopTrack(int count) {
        return query("select " + COLUMNS + " from lastfm_artist_toptracks where rank < ?", rowMapper, count);
    }
    
    public void CleanupArtistTopTracks() {
        update("DELETE FROM lastfm_artist_toptracks");
        update("UPDATE media_file set rank=0"); // where rank < 31
        update("UPDATE artist set TOPPLAY_FOUND=0, TOPPLAY_COUNT=0");
    }    
    
    public synchronized void createOrUpdateLastFMArtistTopTrack(LastFMArtistTopTrack lastFMArtistTopTracks) {
        String sql ="update lastfm_artist_toptracks set " +
	                "artistname=?," +
	                "alternate=?," +
	                "artistmbid=?," +
	                "songname=?," +
	                "songmbid=?," +
	                "coverart=?, " +
	                "rank=?, " +
	                "play_count=? " +
	                "where artistname=? and songname=?";

        int n = update(sql, lastFMArtistTopTracks.getArtistname(), lastFMArtistTopTracks.getAlternate(), lastFMArtistTopTracks.getArtistmbid(),lastFMArtistTopTracks.getSongname(), lastFMArtistTopTracks.getSongmbid(), lastFMArtistTopTracks.getCoverart(), lastFMArtistTopTracks.getRank(), lastFMArtistTopTracks.getPlayCount(), lastFMArtistTopTracks.getArtistname(), lastFMArtistTopTracks.getSongname());
        
        if (n == 0) {

            update("insert into lastfm_artist_toptracks (" + COLUMNS + ") values (" + questionMarks(COLUMNS) + ")", null,
            		lastFMArtistTopTracks.getArtistname(), lastFMArtistTopTracks.getAlternate(), lastFMArtistTopTracks.getArtistmbid(), lastFMArtistTopTracks.getSongname(), lastFMArtistTopTracks.getSongmbid(), lastFMArtistTopTracks.getCoverart(), lastFMArtistTopTracks.getRank(), lastFMArtistTopTracks.getPlayCount() );
        }

    }

    private static class LastFMArtistTopTracksMapper implements ParameterizedRowMapper<LastFMArtistTopTrack> {
        public LastFMArtistTopTrack mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new LastFMArtistTopTrack(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getString(7),
                    rs.getInt(8),
                    rs.getInt(9));
        }
    }
}
