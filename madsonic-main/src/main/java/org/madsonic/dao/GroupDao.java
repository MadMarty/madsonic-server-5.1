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
import java.util.ArrayList;
import java.util.List;

import org.madsonic.Logger;
import org.madsonic.dao.GroupDao;
import org.madsonic.domain.Group;
import org.madsonic.domain.InternetRadio;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * Provides database services for Access Control.
 *
 * @author Madevil
 */
public class GroupDao extends AbstractDao {


    private static final Logger LOG = Logger.getLogger(GroupDao.class);
    private static final String COLUMNS = "id, name, audio_default_bitrate, audio_max_bitrate, video_default_bitrate, video_max_bitrate";	
    
    private final static RowMapper rowMapper = new GroupMapper();
    
    public List<Group> getAllGroups() {
        String sql = "select " + COLUMNS + " from user_group";
        return query(sql, rowMapper);
    }

	public int getIdAllGroups(Group group) {
        String sql = "select id from user_group where name=?";
        return queryForInt(sql, 0, group.getName());
	}    
    
    public void createGroup(Group group) {
        String sql = "insert into user_group (" + COLUMNS + ") values (null, ?, 0, 0, 1000, 5000 )";
        update(sql, group.getName());
    }    

    public void updateGroup(Group group) {
        String sql = "update user_group set name=?, audio_default_bitrate=?, audio_max_bitrate=?, video_default_bitrate=?, video_max_bitrate=? where id=?";
        update(sql, group.getName(), group.getAudioDefaultBitrate(), group.getAudioMaxBitrate(), group.getVideoDefaultBitrate(),group.getVideoMaxBitrate(), group.getId());
    }    
    
	public void deleteGroup(Group group) {
        String sql = "delete from user_group where id=?";
        update(sql, group.getId());
	}     

	
	public int getUserGroupVideoDefault(int id) {
        String sql = "select video_default_bitrate from user_group where id=?";
        return queryForInt(sql, 1000, id);
	}	
	
	public int getUserGroupVideoMax(int id) {
        String sql = "select video_max_bitrate from user_group where id=?";
        return queryForInt(sql, 10000, id);
	}
	
	public void insertMusicFolderAccess(int music_folder_id) {
//	    update("insert into user_group_access (user_group_id, music_folder_id, enabled) (select distinct g.id as user_group_id, f.id as music_folder_id, 'true' as enabled from user_group g, music_folder f where music_folder_id=?)", music_folder_id);
	    update("insert into user_group_access (user_group_id, music_folder_id, enabled) (select distinct user_group.id as user_group_id, music_folder.id as music_folder_id, 'true' as enabled from public.user_group user_group, public.music_folder music_folder where music_folder.id = ?)", music_folder_id);
	}			

	public void insertGroupAccess(int user_group_id) {
//	    update("insert into user_group_access (user_group_id, music_folder_id, enabled) (select distinct g.id as user_group_id, f.id as music_folder_id, 'true' as enabled from user_group g, music_folder f where user_group_id=?)", user_group_id);
	    update("insert into user_group_access (user_group_id, music_folder_id, enabled) (select distinct user_group.id as user_group_id, music_folder.id as music_folder_id, 'true' as enabled from public.user_group user_group, public.music_folder music_folder where user_group.id = ?)", user_group_id);
	}			
	
	public void resetGroup() {
	    update("delete from user_group_access");
	    update("insert into user_group_access (user_group_id, music_folder_id, enabled) (select distinct user_group.id as user_group_id, music_folder.id as music_folder_id, 'true' as enabled from public.user_group user_group, public.music_folder music_folder)");
	}		
	
	private static class GroupMapper implements ParameterizedRowMapper<Group> {
		public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Group(
					rs.getInt(1),
					rs.getString(2),
					rs.getInt(3),
					rs.getInt(4),
					rs.getInt(5),
					rs.getInt(6));
		}
	}
    
}
