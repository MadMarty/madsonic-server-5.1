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
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import org.madsonic.Logger;
import org.madsonic.domain.MusicFolderStatistics;

public class MusicFolderStatisticsDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(MusicFolderStatisticsDao.class);
    
    private static final String COLUMNS = "id, music_folder_id, artist_all, artist_album, albums, genre, music_files, music_files_size, video_files, video_files_size, podcast_files, podcast_files_size, audiobooks_files, audiobooks_files_size";
    
    private final MusicFolderStatsRowMapper rowMapper = new MusicFolderStatsRowMapper();
    
    public List<MusicFolderStatistics> getAllFolderStatistics() {
        String sql = "select " + COLUMNS + " from statistic_server";
        return query(sql, rowMapper);
    }

    public void updateMusicFolderStats(MusicFolderStatistics mfstat) {
    	
        String sql ="update statistic_server set " +
	        	    "artist_all=?, " + 
	        	    "artist_album=?, " + 
	        	    "albums=?, " + 
	        	    "genre=?, " + 
	        	    "music_files=?, " + 
	        	    "music_files_size=?, " + 
	        	    "video_files=?, " + 
	        		"video_files_size=?, " + 
	        	    "podcast_files=?, " + 
	        		"podcast_files_size=?, " + 
	        	    "audiobooks_files=?, " + 
	        		"audiobooks_files_size=? " +     
	        		"where music_folder_id=?";

        int n = update(sql, 
        		mfstat.getArtistCount(), mfstat.getAlbumArtistCount(), mfstat.getAlbumCount(), mfstat.getGenreCount(),
        		mfstat.getSongCount(), mfstat.getSongSize(), mfstat.getVideoCount(), mfstat.getVideoSize(),
        		mfstat.getPodcastCount(), mfstat.getPodcastSize(), mfstat.getAudiobookCount(), mfstat.getAudiobookSize(), mfstat.getMusicFolderId());
        
        if (n == 0) {
        	
			update("insert into statistic_server (" + COLUMNS + ") values (" + questionMarks(COLUMNS) + ") " , null,
					mfstat.getMusicFolderId(), mfstat.getArtistCount(), mfstat.getAlbumArtistCount(), mfstat.getAlbumCount(), mfstat.getGenreCount(),
	        		mfstat.getSongCount(), mfstat.getSongSize(), mfstat.getVideoCount(), mfstat.getVideoSize(),
	        		mfstat.getPodcastCount(), mfstat.getPodcastSize(), mfstat.getAudiobookCount(), mfstat.getAudiobookSize());
        }
    }
    
    private static class MusicFolderStatsRowMapper implements ParameterizedRowMapper<MusicFolderStatistics> {
        public MusicFolderStatistics mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MusicFolderStatistics ( rs.getInt(1),
							           		   rs.getInt(2),
							           		   rs.getInt(3),
							           		   rs.getInt(4),
							           		   rs.getInt(5),
							           		   rs.getInt(6),
							           		   rs.getInt(7),
							           		   rs.getLong(8),
							           		   rs.getInt(9),
							           		   rs.getLong(10),
							           		   rs.getInt(11),
							           		   rs.getLong(12),
							            	   rs.getInt(13),							           		   
							            	   rs.getLong(14));
        }
    }
}