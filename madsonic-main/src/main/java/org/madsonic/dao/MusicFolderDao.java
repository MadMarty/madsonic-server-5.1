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

import java.io.File;

import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import org.madsonic.Logger;
import org.madsonic.domain.MusicFolder;

/**
 * Provides database services for music folders.
 *
 * @author Sindre Mehus
 */
public class MusicFolderDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(MusicFolderDao.class);
    private static final String COLUMNS = "id, path, name, enabled, changed, index, type, groupby";
    private static final int[] DATATYPES = {Types.VARCHAR, Types.VARCHAR, Types.BOOLEAN, Types.TIMESTAMP, Types.INTEGER, Types.INTEGER, Types.INTEGER};
    
    private final MusicFolderRowMapper rowMapper = new MusicFolderRowMapper();

    /**
     * Returns all music folders.
     *
     * @return Possibly empty list of all music folders.
     */
    public List<MusicFolder> getAllMusicFolders() {
        String sql = "select " + COLUMNS + " from music_folder";
        return query(sql, rowMapper);
    }


    public List<MusicFolder> getAllEnabledMusicFolders() {
        String sql = "select " + COLUMNS + " from music_folder where enabled = 'true'";
        return query(sql, rowMapper);
    }
    
    /**
     * Returns all music folders for the given user_group_id
     *
     * @return Possibly empty list of music folders.
     */	
	public List<MusicFolder> getAllMusicFolders(int usergroupId) {
		String sql ="select " + COLUMNS + " from music_folder " +
				"where id in (select music_folder_id from user_group_access where user_group_id = ? and enabled = 'true')";
        return query(sql, rowMapper, usergroupId);
	}

    /**
     * Returns all music folders for the given user_group_id and group level
     *
     * @return Possibly empty list of music folders.
     */	
	public List<MusicFolder> getAllMusicFolders(int usergroupId, int group) {
		String sql ="select " + COLUMNS + " from music_folder " +
				"where id in (select music_folder_id from user_group_access where user_group_id = ? and enabled = 'true' and groupby = ?)";
        return query(sql, rowMapper, usergroupId, group);
	}
	
    public MusicFolder getMusicFolder(String musicFolderPath) {
        String sql = "select " + COLUMNS + " from music_folder where path = ? and enabled = 'true'";
        return queryOne(sql, rowMapper, musicFolderPath);
    }	
	
    /**
     * Creates a new music folder.
     *
     * @param musicFolder The music folder to create.
     */
    public void createMusicFolder(MusicFolder musicFolder) {

//    	java.sql.Timestamp changed = new java.sql.Timestamp(musicFolder.getChanged().getTime());
//    	Date date = (player.getLastSeen() == null) ? new Date() : player.getLastSeen();
//		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//		
//    	java.sql.Date playerLastSeen_sqlDate = new java.sql.Date(date.getTime());
//    	(java.sql.Timestamp) changed 
    	
        String sql = "insert into music_folder (" + COLUMNS + ") values (null, ?, ?, ?, ?, ?, ?, ?)";
        secureupdate(sql, DATATYPES, musicFolder.getPath(), musicFolder.getName(), musicFolder.isEnabled(), musicFolder.getChanged(), 
        		    musicFolder.getIndex(), musicFolder.getType(), musicFolder.getGroup() );
        LOG.info("Created music folder " + musicFolder.getPath());
    }

	public List<MusicFolder> getAllMusicFoldders(int usergroupId) {
		String sql ="select " + COLUMNS + " from music_folder " +
				"where id in (select music_folder_id from user_group_access where user_group_id = ? and enabled = 'true')";
        return query(sql, rowMapper, usergroupId);
	}
	
    
    public int getMusicFolderId(String MusicFolderPath){
		String sql ="select id from music_folder where path=?";
	    return queryForInt(sql, 0, MusicFolderPath);    
    }

	public MusicFolder getMusicFolder(int id) {
		String sql ="select " + COLUMNS + " from music_folder where id = ?";
        return queryOne(sql, rowMapper, id);
	}
    
//	public void insertGroup(int music_folder_id) {
//	    
//	}	
    
    /**
     * Deletes the music folder with the given ID.
     *
     * @param id The music folder ID.
     */
    public void deleteMusicFolder(Integer id) {
        String sql = "delete from music_folder where id=?";
        update(sql, id);
        LOG.info("Deleted music folder with ID " + id);
    }

    public void EnableAllMusicFolder() {
        String sql = "update music_folder set ENABLED = 'true'";
        update(sql);
        LOG.info("Enabled music folders");
    }
	
    public void DisableAllMusicFolder() {
        String sql = "update music_folder set ENABLED = 'false'";
        update(sql);
        LOG.info("Disable music folders");
    }
	
    /**
     * Updates the given music folder.
     *
     * @param musicFolder The music folder to update.
     */
    public void updateMusicFolder(MusicFolder musicFolder) {
        String sql = "update music_folder set path=?, name=?, enabled=?, changed=?, index=?, type=?, groupby=? where id=?";
        update(sql, musicFolder.getPath().getPath(), musicFolder.getName(),
                musicFolder.isEnabled(), musicFolder.getChanged(), musicFolder.getIndex(), musicFolder.getType(), musicFolder.getGroup(), musicFolder.getId());
    }

    private static class MusicFolderRowMapper implements ParameterizedRowMapper<MusicFolder> {
        public MusicFolder mapRow(ResultSet rs, int rowNum) throws SQLException {
             return new MusicFolder(rs.getInt(1),
            		new File(rs.getString(2)), 
            		rs.getString(3), 
            		rs.getBoolean(4), 
            		rs.getTimestamp(5),
            		rs.getInt(6),
            		rs.getInt(7),
             		rs.getInt(8));
        }
    }

}
