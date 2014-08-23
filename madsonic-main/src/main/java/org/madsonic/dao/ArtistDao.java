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

import org.madsonic.Logger;
import org.madsonic.domain.Artist;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Provides database services for artists.
 *
 * @author Sindre Mehus
 */
public class ArtistDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(ArtistDao.class);
     
    private static final String COLUMNS = "id, name, genre, cover_art_path, artist_folder, album_count, play_count, song_count, topplay_found, topplay_count, last_scanned, present";

    private final RowMapper rowMapper = new ArtistMapper();
    private final RowMapper rowMapper2 = new ArtistMapper2();

    public int getAllArtist() {
        return queryForInt("SELECT count(name) FROM artist", 0 );
    }

    public List<Artist> getAllArtists() {
        return query("select " + COLUMNS + " from artist where present order by name", rowMapper);
    }    
    
    public List<Artist> getAllAlbumArtists() {
        return query("select " + COLUMNS + " from artist where present and song_count > 2 order by name", rowMapper);
    }    
 
    public List<Artist> getGroupedAlbumArtists() {
        return query("select distinct(name), id, genre, cover_art_path, artist_folder, album_count, play_count, song_count, TOPPLAY_FOUND, TOPPLAY_COUNT, last_scanned, present from artist where present and album_count > 0 and song_count > 2 order by name", rowMapper2);
    }    

    public List<Artist> getNewGroupedAlbumArtists() {
        return query("select distinct(name), id, genre, cover_art_path, artist_folder, album_count, play_count, song_count, TOPPLAY_FOUND, TOPPLAY_COUNT, last_scanned, present from artist where present and album_count > 1 and song_count > 2 and name not in (SELECT ARTISTNAME FROM lastfm_artist) order by name", rowMapper2);
    }    
    
    
    /**
     * Returns the artist with the given name.
     *
     * @param artistName The artist name.
     * @return The artist or null.
     */
    public Artist getArtist(String artistName) {
        return queryOne("select " + COLUMNS + " from artist where name=?", rowMapper, artistName);
    }

    public Artist getLowerArtist(String artistName) {
        return queryOne("select " + COLUMNS + " from artist where lower(name)=?", rowMapper, artistName.toLowerCase());
    }

    public Artist getArtistFolder(String artistName) {
        return queryOne("select " + COLUMNS + " from artist where lower(artist_folder)=?", rowMapper, artistName.toLowerCase());
    }    
    
    /**
     * Returns the artist with the given ID.
     *
     * @param id The artist ID.
     * @return The artist or null.
     */
    public Artist getArtist(int id) {
        return queryOne("select " + COLUMNS + " from artist where id=?", rowMapper, id);
    }

    /**
     * Creates or updates an artist.
     *
     * @param artist The artist to create/update.
     */
    public synchronized void createOrUpdateArtist(Artist artist) {
        String sql = "update artist set " +
                "genre=?," +
                "cover_art_path=?," +
                "artist_folder=?," +
                "album_count=?," +
                "play_count=?," +
                "song_count=?," +
                "topplay_found=?," +
                "topplay_count=?," +
                "last_scanned=?," +
                "present=? " +
                "where name=?";

        int n = update(sql, artist.getGenre(), artist.getCoverArtPath(), artist.getArtistFolder(), artist.getAlbumCount(), artist.getPlayCount(), artist.getSongCount(), artist.getTopPlayFound(), artist.getTopPlayCount(), artist.getLastScanned(), artist.isPresent(), artist.getName());

        if (n == 0) {

            update("insert into artist (" + COLUMNS + ") values (" + questionMarks(COLUMNS) + ")", null,
                    artist.getName(), artist.getGenre(), artist.getCoverArtPath(), artist.getArtistFolder(), artist.getAlbumCount(), artist.getPlayCount(), artist.getSongCount(), artist.getTopPlayFound(), artist.getTopPlayCount(), artist.getLastScanned(), artist.isPresent());
        }

        int id = queryForInt("select id from artist where name=?", null, artist.getName());
        artist.setId(id);
    }

    /**
     * Returns artists in alphabetical order.
     *
     * @param offset Number of artists to skip.
     * @param count  Maximum number of artists to return.
     * @return Artists in alphabetical order.
     */
    public List<Artist> getAlphabetialArtists(int offset, int count) {
        return query("select " + COLUMNS + " from artist where present and song_count > 2 order by name limit ? offset ?", rowMapper, count, offset);
    }

    /**
     * Returns the most recently starred artists.
     *
     * @param offset   Number of artists to skip.
     * @param count    Maximum number of artists to return.
     * @param username Returns artists starred by this user.
     * @return The most recently starred artists for this user.
     */
    public List<Artist> getStarredArtists(int offset, int count, String username) {
        return query("select " + prefix(COLUMNS, "artist") + " from starred_artist, artist where artist.id = starred_artist.artist_id and " +
                "artist.present and starred_artist.username=? order by starred_artist.created desc limit ? offset ?",
                rowMapper, username, count, offset);
    }

    public void reset() {
        update("update artist set song_count=0, play_count=0");
    }
    
 
    public List<String> getArtistsForFolder(String artistName) {
    	return queryForStrings("select distinct lower(name) from artist where lower(name)=? or lower(artist_folder)=? and song_count > 1", artistName.toLowerCase(), artistName.toLowerCase());
    	// or lower(artist_folder)=? 
    }
    
     
    public void markPresent(String artistName, Date lastScanned) {
        update("update artist set present=?, last_scanned=? where name=?", true, lastScanned, artistName);
    }

    public void markNonPresent(Date lastScanned) {
        int minId = queryForInt("select top 1 id from artist where last_scanned != ? and present", 0, lastScanned);
        int maxId = queryForInt("select max(id) from artist where last_scanned != ? and present", 0, lastScanned);

        final int batchSize = 1000;
        for (int id = minId; id <= maxId; id += batchSize) {
            update("update artist set present=false where id between ? and ? and last_scanned != ? and present", id, id + batchSize, lastScanned);
        }
    }

    public void expunge() {
        int minId = queryForInt("select top 1 id from artist where not present", 0);
        int maxId = queryForInt("select max(id) from artist where not present", 0);

        final int batchSize = 1000;
        for (int id = minId; id <= maxId; id += batchSize) {
            update("delete from artist where id between ? and ? and not present", id, id + batchSize);
        }
    }

    public void starArtist(int artistId, String username) {
        unstarArtist(artistId, username);
        update("insert into starred_artist(artist_id, username, created) values (?,?,?)", artistId, username, new Date());
    }

    public void unstarArtist(int artistId, String username) {
        update("delete from starred_artist where artist_id=? and username=?", artistId, username);
    }

    public Date getArtistStarredDate(int artistId, String username) {
        return queryForDate("select created from starred_artist where artist_id=? and username=?", null, artistId, username);
    }

    private static class ArtistMapper implements ParameterizedRowMapper<Artist> {
        public Artist mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Artist(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getInt(6),
                    rs.getInt(7),
                    rs.getInt(8),
                    rs.getInt(9),
                    rs.getInt(10),                    
                    rs.getTimestamp(11),
                    rs.getBoolean(12));
        }
    }
    
    private static class ArtistMapper2 implements ParameterizedRowMapper<Artist> {
        public Artist mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Artist(
                    rs.getInt(2),
                    rs.getString(1),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getInt(6),
                    rs.getInt(7),
                    rs.getInt(8),
                    rs.getInt(9),
                    rs.getInt(10),                    
                    rs.getTimestamp(11),
                    rs.getBoolean(12));
        }
    }    
}
