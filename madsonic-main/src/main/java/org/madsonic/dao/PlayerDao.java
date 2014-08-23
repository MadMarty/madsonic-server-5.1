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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import org.madsonic.Logger;
import org.madsonic.domain.CoverArtScheme;
import org.madsonic.domain.Player;
import org.madsonic.domain.PlayerTechnology;
import org.madsonic.domain.PlayQueue;
import org.madsonic.domain.TranscodeScheme;

/**
 * Provides player-related database services.
 *
 * @author Sindre Mehus
 */
public class PlayerDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(PlayerDao.class);
    private static final String COLUMNS = "id, name, type, username, ip_address, auto_control_enabled, " +
            "last_seen, cover_art_scheme, transcode_scheme, dynamic_ip, technology, client_id";

    private static final int[] DATATYPES = {Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BOOLEAN,
    	Types.TIMESTAMP, Types.VARCHAR, Types.VARCHAR, Types.BOOLEAN, Types.VARCHAR, Types.VARCHAR};
    
    private static final int[] DATATYPES2 = {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BOOLEAN,
    	Types.TIMESTAMP, Types.VARCHAR, Types.VARCHAR, Types.BOOLEAN, Types.VARCHAR, Types.VARCHAR,Types.INTEGER};

    private PlayerRowMapper rowMapper = new PlayerRowMapper();
    private Map<String, PlayQueue> playlists = Collections.synchronizedMap(new HashMap<String, PlayQueue>());

    /**
     * Returns all players.
     *
     * @return Possibly empty list of all users.
     */
    public List<Player> getAllPlayers() {
        String sql = "select " + COLUMNS + " from player";
        return query(sql, rowMapper);
    }

    /**
     * Returns all players owned by the given username and client ID.
     *
     * @param username The name of the user.
     * @param clientId The third-party client ID (used if this player is managed over the
     *                 Subsonic REST API). May be <code>null</code>.
     * @return All relevant players.
     */
    public List<Player> getPlayersForUserAndClientId(String username, String clientId) {
        if (clientId != null) {
            String sql = "select " + COLUMNS + " from player where username=? and client_id=?";
            return query(sql, rowMapper, username, clientId);
        } else {
            String sql = "select " + COLUMNS + " from player where username=? and client_id is null";
            return query(sql, rowMapper, username);
        }
    }

    /**
     * Returns the player with the given ID.
     *
     * @param id The unique player ID.
     * @return The player with the given ID, or <code>null</code> if no such player exists.
     */
    public Player getPlayerById(String id) {
        String sql = "select " + COLUMNS + " from player where id=?";
        return queryOne(sql, rowMapper, id);
    }

    /**
     * Creates a new player.
     *
     * @param player The player to create.
     */
    public synchronized void createPlayer(Player player) {
        int id = getJdbcTemplate().queryForInt("select max(id) from player") + 1;
        player.setId(String.valueOf(id));
        
//    	//TODO:review date for new HSQLDB VERSION
//        
//    	final String zerodatetime = "1970-01-01 00:00:00.000000000";
//    
//    	Date date = (player.getLastSeen() == null) ? new Date() : player.getLastSeen();
//		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//		
//    	java.sql.Date playerLastSeen_sqlDate = new java.sql.Date(date.getTime());
        
        String sql = "insert into player (" + COLUMNS + ") values (" + questionMarks(COLUMNS) + ")";
        secureupdate(sql, DATATYPES, player.getId(), player.getName(), player.getType(), player.getUsername(),
                player.getIpAddress(), player.isAutoControlEnabled(),
                player.getLastSeen(), player.getCoverArtScheme().name(),
                player.getTranscodeScheme().name(), player.isDynamicIp(),
                player.getTechnology().name(), player.getClientId());
        addPlaylist(player);

        LOG.info("Created player " + id + '.');
    }

    /**
     * Deletes the player with the given ID.
     *
     * @param id The player ID.
     */
    public void deletePlayer(String id) {
        String sql = "delete from player where id=?";
        update(sql, id);
        playlists.remove(id);
    }


    /**
     * Delete players that haven't been used for the given number of days, and which is not given a name
     * or is used by a REST client.
     *
     * @param days Number of days.
     */
    public void deleteOldPlayers(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -days);
        String sql = "delete from player where name is null and client_id is null and (last_seen is null or last_seen < ?)";
        int n = update(sql, cal.getTime());
        if (n > 0) {
            LOG.info("Deleted " + n + " player(s) that haven't been used after " + cal.getTime());
        }
    }

    /**
     * Updates the given player.
     *
     * @param player The player to update.
     */
    public void updatePlayer(Player player) {
        String sql = "update player set " +
                "name = ?," +
                "type = ?," +
                "username = ?," +
                "ip_address = ?," +
                "auto_control_enabled = ?," +
                "last_seen = ?, " +
                "cover_art_scheme = ?," +
                "transcode_scheme = ?, " +
                "dynamic_ip = ?, " +
                "technology = ?, " +
                "client_id = ? " +
                "where id = ?";
        		
        secureupdate(sql, DATATYPES2, player.getName(), player.getType(), player.getUsername(),
                player.getIpAddress(), player.isAutoControlEnabled(),
                player.getLastSeen(), player.getCoverArtScheme().name(),
                player.getTranscodeScheme().name(), player.isDynamicIp(),
                player.getTechnology(), player.getClientId(), player.getId());
    }

    private void addPlaylist(Player player) {
        PlayQueue playQueue = playlists.get(player.getId());
        if (playQueue == null) {
            playQueue = new PlayQueue();
            playlists.put(player.getId(), playQueue);
        }
        player.setPlayQueue(playQueue);
    }

    private class PlayerRowMapper implements ParameterizedRowMapper<Player> {
        public Player mapRow(ResultSet rs, int rowNum) throws SQLException {
            Player player = new Player();
            int col = 1;
            player.setId(rs.getString(col++));
            player.setName(rs.getString(col++));
            player.setType(rs.getString(col++));
            player.setUsername(rs.getString(col++));
            player.setIpAddress(rs.getString(col++));
            player.setAutoControlEnabled(rs.getBoolean(col++));
            player.setLastSeen(rs.getTimestamp(col++));
            player.setCoverArtScheme(CoverArtScheme.valueOf(rs.getString(col++)));
            player.setTranscodeScheme(TranscodeScheme.valueOf(rs.getString(col++)));
            player.setDynamicIp(rs.getBoolean(col++));
            player.setTechnology(PlayerTechnology.valueOf(rs.getString(col++)));
            player.setClientId(rs.getString(col++));

            addPlaylist(player);
            return player;
        }
    }
}
