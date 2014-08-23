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
import org.madsonic.domain.Genre;
import org.madsonic.domain.MediaFile;
import org.madsonic.domain.MusicFolder;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.madsonic.domain.MediaFile.MediaType;
import static org.madsonic.domain.MediaFile.MediaType.*;

/**
 * Provides database services for media files.
 *
 * @author Sindre Mehus
 */
public class MediaFileDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(MediaFileDao.class);
    private static final String COLUMNS = "id, path, folder, type, override, format, data, title, album, album_name, artist, album_artist, disc_number, " +
            "track_number, year, genre, mood, bit_rate, variable_bit_rate, duration_seconds, file_size, width, height, cover_art_path, " +
            "parent_path, play_count, last_played, comment, created, changed, last_scanned, first_scanned, children_last_updated, present, version, rank";
    
    private static final String GENRE_COLUMNS = "name, song_count, album_count, artist_count";

    public static final int VERSION = 4;

    private final RowMapper rowMapper = new MediaFileMapper();
    private final RowMapper musicFileInfoRowMapper = new MusicFileInfoMapper();
    private final RowMapper genreRowMapper = new GenreMapper();


    /**
     * Returns the media file for the given path.
     *
     * @param path The path.
     * @return The media file or null.
     */
    public MediaFile getMediaFile(String path) {
        return queryOne("select " + COLUMNS + " from media_file where path=?", rowMapper, path);
    }

    /**
     * Returns the media file for the given ID.
     *
     * @param id The ID.
     * @return The media file or null.
     */
    public MediaFile getMediaFile(int id) {
        return queryOne("select " + COLUMNS + " from media_file where id=?", rowMapper, id);
    }

    @Deprecated
    public MediaFile getMediaFile(String artist, String alternate, String title) {
        return queryOne("select " + COLUMNS + " from (SELECT " + COLUMNS + " FROM media_file WHERE lower(title)=? ) WHERE lower(artist)=? or lower(artist)=?", rowMapper, title.toLowerCase(), artist.toLowerCase(), alternate.toLowerCase() );
    }
    
    @Deprecated
    public MediaFile getMediaFileTitle(String title) {
        return queryOne("select " + COLUMNS + " from media_file where lower(title)=?", rowMapper, title.toLowerCase());
    }
    /**
     * Returns the media file that are direct children of the given path.
     *
     * @param path The path.
     * @return The list of children.
     */
    public List<MediaFile> getChildrenOf(String path) {
        return query("select " + COLUMNS + " from media_file where parent_path=? and present", rowMapper, path);
    }
    
    @Deprecated
    public List<MediaFile> getTopTracks(String artist) {
        return query("select " + COLUMNS + " from media_file where lower(artist)=? and rank > 0 order by rank", rowMapper, artist.toLowerCase());
    }    

    public List<MediaFile> getTopTracks(String artist, int user_group_id ) {
        return query("select " + COLUMNS + " from media_file where lower(artist)=? and rank > 0 and folder in " +
    	"(select path from music_folder where id in (select music_folder_id from user_group_access where user_group_id=? and enabled)) " +
        "order by rank", rowMapper, artist.toLowerCase(), user_group_id );
    }    
    
    public List<MediaFile> getFilesInPlaylist(int playlistId) {
        return query("select " + prefix(COLUMNS, "media_file") + " from playlist_file, media_file where " +
                "playlist_file.media_file_id = media_file.id and " +
                "playlist_file.playlist_id = ? and " +
                "media_file.present order by playlist_file.id", rowMapper, playlistId);  
    }

    public List<MediaFile> getSongsForAlbum(String artist, String album) {
        return query("select " + COLUMNS + " from media_file where album_artist=? and album=? and present and type in (?,?,?) order by track_number", rowMapper,
                artist, album, MUSIC.name(), AUDIOBOOK.name(), PODCAST.name());
    }

//    public List<MediaFile> getSongsForAlbum(String artist, String albumid3) {
//        return query("select " + COLUMNS + " from media_file where album_artist=? and album=? and present and type in (?,?,?) order by track_number", rowMapper,
//                artist, albumid3, MUSIC.name(), AUDIOBOOK.name(), PODCAST.name());
//    }    
    
    public Integer getIdForTrack(String Artist, String TrackName){
    	
    	String artistname = Artist.toUpperCase();
    	
    	artistname = artistname.replace(":", "");
    	artistname = artistname.replace("-", "");
    	artistname = artistname.replace(" ", "");

        return queryForInt("SELECT id FROM media_file where TYPE='MUSIC' and present and upper(replace(replace(replace(artist,'-',''),':',''),' ','')) =? and lower(title)=?", 0, artistname, TrackName.toLowerCase());
   	}    
    
    public Integer getIdsForAlbums(String albumName){
        return queryForInt("SELECT id FROM media_file where TYPE='ALBUM' and present and lower(album)=?", 0, albumName.toLowerCase());
   	}
    
    public Integer getIdsForAlbums(String artist, String albumName){
        return queryForInt("SELECT id FROM media_file where TYPE='ALBUM' and present and lower(artist)=? and lower(album_name)=?", 0, artist.toLowerCase(), albumName.toLowerCase());
   	}
    
    public List<MediaFile> getVideos(int size, int offset) {
        return query("select " + COLUMNS + " from media_file where type=? and present order by FIRST_SCANNED desc limit ? offset ?", rowMapper,
                VIDEO.name(), size, offset);
    }

    /**
     * Creates or updates a media file.
     *
     * @param file The media file to create/update.
     */
    public synchronized void createOrUpdateMediaFile(MediaFile file) {
        String sql = "update media_file set " +
                "folder=?," +
                "type=?," +
                "override=?," +
                "format=?," +
                "data=?," +                
                "title=?," +
                "album=?," +
                "album_name=?," +
                "artist=?," +
                "album_artist=?," +
                "disc_number=?," +
                "track_number=?," +
                "year=?," +
                "genre=?," +
                "mood=?," +
                "bit_rate=?," +
                "variable_bit_rate=?," +
                "duration_seconds=?," +
                "file_size=?," +
                "width=?," +
                "height=?," +
                "cover_art_path=?," +
                "parent_path=?," +
                "play_count=?," +
                "last_played=?," +
                "comment=?," +
                "changed=?," +
                "last_scanned=?," +
                "children_last_updated=?," +
                "present=?, " +
                "version=?, " +
                "rank=? " +
                "where path=?";

        int n = update(sql,
                file.getFolder(), file.getMediaType().name(), file.isMediaTypeOverride(), file.getFormat(), file.getData(), file.getTitle(), file.getAlbumName(), file.getAlbumSetName(), file.getArtist(),
                file.getAlbumArtist(), file.getDiscNumber(), file.getTrackNumber(), file.getYear(), file.getGenre(), file.getMood(), file.getBitRate(),
                file.isVariableBitRate(), file.getDurationSeconds(), file.getFileSize(), file.getWidth(), file.getHeight(),
                file.getCoverArtPath(), file.getParentPath(), file.getPlayCount(), file.getLastPlayed(), file.getComment(),
                file.getChanged(), file.getLastScanned(), file.getChildrenLastUpdated(), file.isPresent(), VERSION, file.getRank(), file.getPath());

        if (n == 0) {

            // Copy values from obsolete table music_file_info.
            MediaFile musicFileInfo = getMusicFileInfo(file.getPath());
            if (musicFileInfo != null) {
                file.setComment(musicFileInfo.getComment());
                file.setLastPlayed(musicFileInfo.getLastPlayed());
                file.setPlayCount(musicFileInfo.getPlayCount());
            }

     	   DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    	   Date date = new Date();
//    	   System.out.println(dateFormat.format(date));            
            
            update("insert into media_file (" + COLUMNS + ") values (" + questionMarks(COLUMNS) + ")", null,
                    file.getPath(), file.getFolder(), file.getMediaType().name(), file.isMediaTypeOverride(), file.getFormat(), file.getData(), file.getTitle(), file.getAlbumName(), file.getAlbumSetName(), file.getArtist(),
                    file.getAlbumArtist(), file.getDiscNumber(), file.getTrackNumber(), file.getYear(), file.getGenre(), file.getMood(), file.getBitRate(),
                    file.isVariableBitRate(), file.getDurationSeconds(), file.getFileSize(), file.getWidth(), file.getHeight(),
                    file.getCoverArtPath(), file.getParentPath(), file.getPlayCount(), file.getLastPlayed(), file.getComment(),
                    file.getCreated(), file.getChanged(), file.getLastScanned(), dateFormat.format(date),
                    file.getChildrenLastUpdated(), file.isPresent(), VERSION, file.getRank());
        }

        int id = queryForInt("select id from media_file where path=?", null, file.getPath());
        file.setId(id);
        
    }

    private MediaFile getMusicFileInfo(String path) {
        return queryOne("select play_count, last_played, comment from music_file_info where path=?", musicFileInfoRowMapper, path);
    }

    public Integer getAlbumCount(String artist) {
    	return queryForInt("select count(*) as albumCount from album where lower(artist) =?", 0, artist.toLowerCase());
    }

    public Integer getSongCount(String artist) {
    	// return 1; //    	
		return queryForInt("select count(*)  from media_file where type='MUSIC' and lower(artist) =?", 0, artist.toLowerCase());
    }
    
    public Integer getPlayCount(String artist) {
    	return queryForInt("select sum(play_count) from media_file where type='MUSIC' and lower(artist) =?", 0, artist.toLowerCase());
    }

//TODO: getIDfromArtistname

    public Integer getIDfromArtistname(String ArtistName) {

  	//	return queryForInt("select max(id) from media_file where artist=?", null, ArtistName);
    //	return queryForInt("select max(id) from media_file where upper(artist)=? and type='ARTIST'", null, ArtistName.toUpperCase());
    	
    	String artistname = ArtistName.toUpperCase();
    	
    	artistname = artistname.replace(":", "");
    	artistname = artistname.replace("-", "");
    	artistname = artistname.replace(" ", "");

    	return queryForInt("select max(id) from media_file where upper(replace(replace(replace(artist,'-',''),':',''),' ','')) =?  and type='ARTIST'", null, artistname );
    }
    
    public void deleteMediaFile(String path) {
        update("update media_file set present=false, children_last_updated=? where path=?", new Date(0L), path);
    }

    public List<String> getGenre() {
    	return queryForStrings("select name from genre order by name");
    }

//    public List<String> getGenresEX() {
//    	return queryForStrings("select distinct genre from media_file where genre is not null and present");
//    }

//    public List<String> getLowerGenres() {
//    	return queryForStrings("select distinct lower(genre) from media_file where lower(genre) is not null and present");
//    }

    public int getGenreSongCount() {
        return queryForInt("select MAX (song_count) from genre", 0);
    }

    public int getGenreArtistCount() {
        return queryForInt("select MAX (artist_count) from genre", 0);
    }
    
    public int getGenreAlbumCount() {
        return queryForInt("select MAX (album_count) from genre", 0);
    }
        
    
    public List<Genre> getGenres(boolean sortByAlbum) {
        String orderBy = sortByAlbum ? "album_count" : "song_count";
        return query("select " + GENRE_COLUMNS + " from genre order by " + orderBy + " desc", genreRowMapper);
    }

    public List<Genre> getGenreSongList() {
        return query("select " + GENRE_COLUMNS + " from genre where song_count > 0 order by name asc", genreRowMapper);
    }
 
    public List<Genre> getGenreArtistList() {
        return query("select " + GENRE_COLUMNS + " from genre where artist_count > 0 order by name asc", genreRowMapper);
    }    

    public List<Genre> getGenreAlbumList() {
        return query("select " + GENRE_COLUMNS + " from genre where album_count > 0 order by name asc", genreRowMapper);
    }    
    
    public List<Genre> getGenres() {
        return query("select " + GENRE_COLUMNS + " from genre order by name asc", genreRowMapper);
    }

    
    public void updateGenres(List<Genre> genres) {
        update("delete from genre");
        for (Genre genre : genres) {
            update("insert into genre(" + GENRE_COLUMNS + ") values(?, ?, ?, ?)",
                    genre.getName(), genre.getSongCount(), genre.getAlbumCount(), genre.getArtistCount());
        }
    }
    
    @Deprecated
    public void updateGenres(Map<String, Integer> genres) {
        update("delete from genre");
        for (Map.Entry<String, Integer> entry : genres.entrySet()) {
            update("insert into genre values(?, ?)", entry.getKey(), entry.getValue());
        }
    }

    public List<String> getArtistGenres() {
    	return queryForStrings("select distinct genre from media_file where genre is not null and present and TYPE='ARTIST'");
    }    
    
    public List<String> getArtistGenresforFolder() {
    	return queryForStrings("select distinct genre from media_file where genre is not null and present and TYPE='ARTIST'");
    }  
    
    public List<String> getArtistGenresforFolder(int musicFolderid, int user_group_id) {
        return queryForStrings("select distinct lower(genre) from media_file where lower(genre) is not null and present and type='ARTIST' and folder in " +
				"(select path from music_folder where id in (select music_folder_id from user_group_access where music_folder_id=? and user_group_id=? and enabled))",musicFolderid, user_group_id);
    }    
    
    public List<String> getMoods() {
    	return queryForStrings("select distinct(mood) from media_file where mood is not null and present");
    }    

	public List<String> getLowerMoods() {
    	return queryForStrings("select distinct lower(mood) from media_file where lower(mood) is not null and present");
	}    

    public List<String> getGenres(int user_group_id) {
        return queryForStrings("select distinct genre from media_file where genre is not null and present and folder in " +
				"(select path from music_folder where id in (select music_folder_id from user_group_access where user_group_id=? and enabled))", user_group_id);
    }	
	
    public List<String> getLowerGenres(int user_group_id) {
        return queryForStrings("select distinct lower(genre) from media_file where lower(genre) is not null and present and folder in " +
				"(select path from music_folder where id in (select music_folder_id from user_group_access where user_group_id=? and enabled))", user_group_id);
    }

    /**
     * Returns the most frequently played albums.
     *
     * @param offset Number of albums to skip.
     * @param count  Maximum number of albums to return.
     * @return The most frequently played albums.
     */
    public List<MediaFile> getMostFrequentlyPlayedAlbums(int offset, int count, int user_group_id) {
        return query("select " + COLUMNS + " from media_file where type=? and play_count > 0 and present  and folder in " +
        		"(select path from music_folder where id in (select music_folder_id from user_group_access where user_group_id=? and enabled)) " +
        		"order by play_count desc limit ? offset ?", rowMapper, ALBUM.name(), user_group_id, count, offset);
    }

    /**
     * Returns the most recently played albums.
     *
     * @param offset Number of albums to skip.
     * @param count  Maximum number of albums to return.
     * @return The most recently played albums.
     */
    public List<MediaFile> getMostRecentlyPlayedAlbums(int offset, int count, int user_group_id) {
        return query("select " + COLUMNS + " from media_file where type=? and last_played is not null and present  and folder in " +
        		"(select path from music_folder where id in (select music_folder_id from user_group_access where user_group_id=? and enabled)) " +
                "order by last_played desc limit ? offset ?", rowMapper, ALBUM.name(), user_group_id, count, offset);
    }

    /**
     * Returns the most recently added albums.
     *
     * @param offset Number of albums to skip.
     * @param count  Maximum number of albums to return.
     * @return The most recently added albums.
     */
    public List<MediaFile> getNewestAlbums(int offset, int count, int user_group_id) {
        return query("select " + COLUMNS + " from media_file where type=? and present and folder in " + 
        		"(select path from music_folder where id in (select music_folder_id from user_group_access where user_group_id=? and enabled)) " +
        		"order by created desc limit ? offset ?",
                rowMapper, ALBUM.name(), user_group_id, count, offset);
    }
   
    public List<MediaFile> getNewestAlbums(MusicFolder musicFolder, int offset, int count, int user_group_id) {
    	
    	if (musicFolder != null) {
            return query("select " + COLUMNS + " from media_file where type=? and present and folder in " + 
            		"(select path from music_folder where id in (select music_folder_id from user_group_access where user_group_id=? and enabled)) " +
            		"and folder=?  order by created desc limit ? offset ?",
                    rowMapper, ALBUM.name(), user_group_id, musicFolder.getPath().toString(), count, offset);
    	}
    	
        return query("select " + COLUMNS + " from media_file where type=? and present and folder in " + 
        		"(select path from music_folder where id in (select music_folder_id from user_group_access where user_group_id=? and enabled)) " +
        		" order by created desc limit ? offset ?",
                rowMapper, ALBUM.name(), user_group_id, count, offset);
    }
     
    /**
     * Returns albums in alphabetical order.
     *
     * @param offset   Number of albums to skip.
     * @param count    Maximum number of albums to return.
     * @param byArtist Whether to sort by artist name
     * @return Albums in alphabetical order.
     */
    public List<MediaFile> getAlphabetialAlbums(int offset, int count, boolean byArtist, int user_group_id) {
        String orderBy = byArtist ? "artist, album" : "album";
        return query("select " + COLUMNS + " from media_file where type=? and artist != '' and present and folder in " +
        		"(select path from music_folder where id in (select music_folder_id from user_group_access where user_group_id=? and enabled)) " +
        		"order by " + orderBy + " limit ? offset ?",
                rowMapper, ALBUM.name(), user_group_id, count, offset);
    }

    /**
     * Returns albums within a year range.
     *
     * @param offset Number of albums to skip.
     * @param count  Maximum number of albums to return.
     * @param fromYear The first year in the range.
     * @param toYear The last year in the range.
     * @return Albums in the year range.
     */
    public List<MediaFile> getAlbumsByYear(int offset, int count, int fromYear, int toYear, int user_group_id) {
        return query("select " + COLUMNS + " from media_file where type=? and present and year between ? and ? and folder in " +
         		"(select path from music_folder where id in (select music_folder_id from user_group_access where user_group_id=? and enabled)) order by year limit ? offset ?",
                rowMapper, ALBUM.name(), fromYear, toYear, user_group_id, count, offset);
    }

    /**
     * Returns albums in a genre.
     *
     * @param offset Number of albums to skip.
     * @param count  Maximum number of albums to return.
     * @param genre The genre name.
     * @return Albums in the genre.
     */
    public List<MediaFile> getAlbumsByGenre(int offset, int count, String genre, int user_group_id ) {
        return query("select " + COLUMNS + " from media_file where type=? and present and genre=? and folder in " +
         		"(select path from music_folder where id in (select music_folder_id from user_group_access where user_group_id=? and enabled)) limit ? offset ?",
                rowMapper, ALBUM.name(), genre, user_group_id, count, offset);
    }

    
    public List<MediaFile> getArtistsByGenre(String genre, int offset, int count, int user_group_id) {
        return query("select " + COLUMNS + " from media_file where type in (?,?) and lower(genre)=? and present and folder in " +
         		"(select path from music_folder where id in (select music_folder_id from user_group_access where user_group_id=? and enabled)) limit ? offset ?",        		
                rowMapper, ARTIST.name(), MULTIARTIST.name() , genre.toLowerCase(), user_group_id, count, offset);
    }
    
    /**
     * Returns all Artists as albumview.
     *
     * @param offset   Number of albums to skip.
     * @param count    Maximum number of albums to return.
     * @param username Returns albums starred by this user.
     * @return all Artists.
     */    
	public List<MediaFile> getArtists(int offset, int count, String username, int user_group_id) {
    return query("select " + COLUMNS + " from media_file where type = ? and present and media_file.folder in " +
 	   	   "(select path from music_folder where id in (select music_folder_id from user_group_access where user_group_id=? and enabled)) " +
    		"order by artist limit ? offset ? ",
	        rowMapper, ARTIST.name(), user_group_id, count, offset);
	}	


    public List<MediaFile> getSongsByGenre(String genre, int offset, int count) {
        return query("select " + COLUMNS + " from media_file where type in (?,?,?) and genre=? and present limit ? offset ?",
                rowMapper, MUSIC.name(), PODCAST.name(), AUDIOBOOK.name(), genre, count, offset);
    }

	
    /**
     * Returns the most recently starred albums.
     *
     * @param offset   Number of albums to skip.
     * @param count    Maximum number of albums to return.
     * @param username Returns albums starred by this user.
     * @return The most recently starred albums for this user.
     */
	public List<MediaFile> getStarredAlbums(int offset, int count, String username) {
	return query("select " + prefix(COLUMNS, "media_file") + " from media_file " +
		   "RIGHT JOIN starred_media_file ON media_File.id = starred_media_file.media_File_ID " +
		   "WHERE media_file.present and media_file.type in (?,?) AND starred_media_file.username=? " +
		   "ORDER BY starred_media_file.created desc limit ? offset ?",
			rowMapper, ALBUM.name(), ALBUMSET.name(), username, count, offset);
	}	
	

	public List<MediaFile> getStarredLastAlbums(int offset, int count, int user_group_id) {
	return query("select " + prefix(COLUMNS, "media_file") + " from media_file " +
		   "RIGHT JOIN starred_media_file ON media_File.id = starred_media_file.media_File_ID " +
		   "WHERE media_file.present and media_file.type in (?,?) and media_file.folder in " +
	   	   "(select path from music_folder where id in (select music_folder_id from user_group_access where user_group_id=? and enabled)) " +
		   "ORDER BY starred_media_file.created desc limit ? offset ?",
			rowMapper, ALBUM.name(), ALBUMSET.name(), user_group_id, count, offset);
	}	
	
	
    /**
     * Returns the most recently starred Artists.
     *
     * @param offset   Number of files to skip.
     * @param count    Maximum number of files to return.
     * @param username Returns files starred by this user.
     * @return The most recently starred Artists for this user.
     */	
	public List<MediaFile> getStarredArtists(int offset, int count, String username) {
	return query("select " + prefix(COLUMNS, "media_file") + " from media_file " +
		   "RIGHT JOIN starred_media_file ON media_File.id = starred_media_file.media_File_ID " +
		   "WHERE media_file.present and media_file.type = ? AND starred_media_file.username=? " +
		   "ORDER BY starred_media_file.created desc limit ? offset ?",
			rowMapper, ARTIST.name(), username, count, offset);
	}		
	
    /**
     * Returns the most recently starred directories.
     *
     * @param offset   Number of directories to skip.
     * @param count    Maximum number of directories to return.
     * @param username Returns directories starred by this user.
     * @return The most recently starred directories for this user.
     */
	public List<MediaFile> getStarredDirectories(int offset, int count, String username) {
	return query("select " + prefix(COLUMNS, "media_file") + " from media_file " +
		   "RIGHT JOIN starred_media_file ON media_File.id = starred_media_file.media_File_ID " +
		   "WHERE media_file.present and media_file.type in (?,?,?) AND starred_media_file.username=? " +
		   "ORDER BY starred_media_file.created desc limit ? offset ?",
			rowMapper, DIRECTORY.name(), ARTIST.name(), MULTIARTIST.name(), username, count, offset);
	}

	
	public List<MediaFile> getStarredLastArtists(int offset, int count, int user_group_id) {
		return query("select id, path, folder, type, override, format, data, title, album, album_name, artist, album_artist, disc_number, " +
		"track_number, year, genre, mood, bit_rate, variable_bit_rate, duration_seconds, file_size, width, height, cover_art_path, " +
		"parent_path, play_count, last_played, comment, media_file.created, changed, last_scanned, first_scanned, children_last_updated, present, version, rank " +
		"from media_file RIGHT JOIN starred_media_file ON media_File.id = starred_media_file.media_File_ID " +
		"WHERE media_file.present and media_file.type in (?,?,?) and media_file.folder in " +
		"(select path from music_folder where id in (select music_folder_id from user_group_access where user_group_id=? and enabled)) ",  
		rowMapper, DIRECTORY.name(), ARTIST.name(), MULTIARTIST.name(), user_group_id);
		}	
	
    /**
     * Returns the most recently starred files.
     *
     * @param offset   Number of albums to skip.
     * @param count    Maximum number of albums to return.
     * @param username Returns albums starred by this user.
     * @return The most recently starred files for this user.
     */
	public List<MediaFile> getStarredFiles(int offset, int count, String username) {
        return query("select " + prefix(COLUMNS, "media_file") + " from media_file " +
               "RIGHT JOIN starred_media_file on media_File.id = starred_media_file.media_File_ID " +
			   "WHERE media_file.present and media_file.type in (?,?,?) AND starred_media_file.username=? " +
			   "ORDER BY starred_media_file.created desc limit ? offset ?",
                rowMapper, MUSIC.name(), PODCAST.name(), AUDIOBOOK.name(), username, count, offset);
    }

	public List<MediaFile> getStarredLinks(int offset, int count, String username) {
        return query("select " + prefix(COLUMNS, "media_file") + " from media_file " +
               "RIGHT JOIN starred_media_file on media_File.id = starred_media_file.media_File_ID " +
			   "WHERE media_file.present and media_file.type = ? AND starred_media_file.username=? " +
			   "ORDER BY starred_media_file.created desc limit ? offset ?",
                rowMapper, URL.name(), username, count, offset);
    }

	public List<MediaFile> getStarredSets(int offset, int count, String username) {
        return query("select " + prefix(COLUMNS, "media_file") + " from media_file " +
               "RIGHT JOIN starred_media_file on media_File.id = starred_media_file.media_File_ID " +
			   "WHERE media_file.present and media_file.type = ? AND starred_media_file.username=? " +
			   "ORDER BY starred_media_file.created desc limit ? offset ?",
                rowMapper, VIDEOSET.name(), username, count, offset);
    }	
	
	public List<MediaFile> getStarredVideos(int offset, int count, String username) {
        return query("select " + prefix(COLUMNS, "media_file") + " from media_file " +
               "RIGHT JOIN starred_media_file on media_File.id = starred_media_file.media_File_ID " +
			   "WHERE media_file.present and media_file.type = ? AND starred_media_file.username=? " +
			   "ORDER BY starred_media_file.created desc limit ? offset ?",
                rowMapper, VIDEO.name(), username, count, offset);
    }	
	
	public List<MediaFile> getStarredLastFiles(int offset, int count, int user_group_id) {
        return query("select " + prefix(COLUMNS, "media_file") + " from media_file " +
               "RIGHT JOIN starred_media_file on media_File.id = starred_media_file.media_File_ID " +
			   "WHERE media_file.present and media_file.type in (?,?,?,?) and media_file.folder in " +
		   	   "(select path from music_folder where id in (select music_folder_id from user_group_access where user_group_id=? and enabled)) " +
			   "ORDER BY starred_media_file.created desc limit ? offset ?",
                rowMapper, MUSIC.name(), PODCAST.name(), AUDIOBOOK.name(), VIDEO.name(), user_group_id, count, offset);
    }
	
    public void starMediaFile(int id, String username) {
        unstarMediaFile(id, username);
        update("insert into starred_media_file(media_file_id, username, created) values (?,?,?)", id, username, new Date());
    }

    public void unstarMediaFile(int id, String username) {
        update("delete from starred_media_file where media_file_id=? and username=?", id, username);
    }

    // ############### Advanced Statistics #####################
    //TODO: Advanced Statistics

    public void setPlayCountForUser(String username, MediaFile mediaFile) {
    	
        if (mediaFile != null) {
            Date now = new Date();
            update("insert into statistic_user values(?, ?, ?, ?)", null, username, mediaFile.getId(), now);
        }
    }    
    
    public List<MediaFile> getLastPlayedCountForUser(int offset, int count, String username) {
        return query("select " + prefix(COLUMNS, "media_file") + " FROM media_File " +
				"inner JOIN statistic_user ON media_File.id = statistic_user.media_File_ID " +
				"and media_file.present and media_file.type in (?,?,?,?) and statistic_user.username=? " +
				"GROUP BY " + prefix(COLUMNS, "media_file") + " " + 
				"ORDER BY MAX(statistic_user.played) DESC limit ? offset ?",	
				rowMapper, MUSIC.name(), PODCAST.name(), AUDIOBOOK.name(), VIDEO.name(), username, count, offset);
    }

    public List<MediaFile> getLastPlayedCountForAllUser(int offset, int count, int user_group_id) {
        return query("select " + prefix(COLUMNS, "media_file") + " FROM media_File " +
				"inner JOIN statistic_user ON media_File.id = statistic_user.media_File_ID " +
				"and media_file.present and media_file.type in (?,?,?,?) and media_file.folder in " +
        		"(select path from music_folder where id in (select music_folder_id from user_group_access where user_group_id=? and enabled)) " +
				"GROUP BY " + prefix(COLUMNS, "media_file") + " " + 
				"ORDER BY MAX(statistic_user.played) DESC limit ? offset ?",	
				rowMapper, MUSIC.name(), PODCAST.name(), AUDIOBOOK.name(), VIDEO.name(), user_group_id, count, offset);
    }        
        
    public List<MediaFile> getTopPlayedCountForUser(int offset, int count, String username) {
		return query("select " + prefix(COLUMNS, "media_file") + " from media_file where media_file.id in " +
				"(SELECT media_File.id FROM media_File " + 
				"RIGHT JOIN statistic_user ON media_File.id = statistic_user.media_File_ID " +
				"WHERE media_file.present and media_file.type in (?,?,?,?) and statistic_user.username=? and media_file.Play_Count>1 " +
				"GROUP BY media_File.id) " +
				"ORDER BY play_count desc limit ? offset ?",
                rowMapper, MUSIC.name(), PODCAST.name(), AUDIOBOOK.name(), VIDEO.name(), username, count, offset);
    }

    public List<MediaFile> getTopPlayedCountForAllUser(int offset, int count, int user_group_id) {
		return query("select " + prefix(COLUMNS, "media_file") + " from media_file where media_file.id in " +
				"(SELECT media_File.id FROM media_File " + 
				"RIGHT JOIN statistic_user ON media_File.id = statistic_user.media_File_ID " +
				"WHERE media_file.present and media_file.type in (?,?,?,?) and media_file.Play_Count>2 and media_file.folder in " +
        		"(select path from music_folder where id in (select music_folder_id from user_group_access where user_group_id=? and enabled)) " +
				"GROUP BY media_File.id) " +
				"ORDER BY play_count desc limit ? offset ?",
                rowMapper, MUSIC.name(), PODCAST.name(), AUDIOBOOK.name(), VIDEO.name(),user_group_id, count, offset);
    }	

    public void cleanupStatistics() {
        update("delete from statistic_user");
    }	

    // ###############################


    public List<MediaFile> getHistory(int offset, int count, int user_group_id, String media_file_type) {
 
	return query("select " + prefix(COLUMNS, "media_file") + " from media_file where present and type = ? " +
    "and media_file.folder in (select path from music_folder where id in (select music_folder_id from user_group_access where user_group_id=? and enabled))" + 
    "ORDER BY first_scanned DESC limit ? offset ?", rowMapper, media_file_type, user_group_id,  count, offset);
    }
    
   // ################################ 
    
    public Date getMediaFileStarredDate(int id, String username) {
        return queryForDate("select created from starred_media_file where media_file_id=? and username=?", null, id, username);
    }

    public void markPresent(String path, Date lastScanned) {
        update("update media_file set present=?, last_scanned=? where path=?", true, lastScanned, path);
    }

    public void markNonPresent(Date lastScanned) {
        int minId = queryForInt("select top 1 id from media_file where last_scanned != ? and present", 0, lastScanned);
        int maxId = queryForInt("select max(id) from media_file where last_scanned != ? and present", 0, lastScanned);

        final int batchSize = 1000;
        Date childrenLastUpdated = new Date(0L);  // Used to force a children rescan if file is later resurrected.
        for (int id = minId; id <= maxId; id += batchSize) {
            update("update media_file set present=false, children_last_updated=? where id between ? and ? and last_scanned != ? and present",
                    childrenLastUpdated, id, id + batchSize, lastScanned);
        }
    }

    public void expunge() {
        int minId = queryForInt("select top 1 id from media_file where not present", 0);
        int maxId = queryForInt("select max(id) from media_file where not present", 0);

        final int batchSize = 1000;
        for (int id = minId; id <= maxId; id += batchSize) {
            update("delete from media_file where id between ? and ? and not present", id, id + batchSize);
        }
        update("checkpoint");
    }

    
    private static class MediaFileMapper implements ParameterizedRowMapper<MediaFile> {
        public MediaFile mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MediaFile(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    MediaType.valueOf(rs.getString(4)),
                    rs.getBoolean(5),
                    rs.getString(6),
                    rs.getString(7),
                    rs.getString(8),                    
                    rs.getString(9),
                    rs.getString(10),
                    rs.getString(11),
                    rs.getString(12),
                    rs.getInt(13) == 0 ? null : rs.getInt(13),
                    rs.getInt(14) == 0 ? null : rs.getInt(14),
                    rs.getInt(15) == 0 ? null : rs.getInt(15),
                    rs.getString(16),
                    rs.getString(17),
                    rs.getInt(18) == 0 ? null : rs.getInt(18),
                    rs.getBoolean(19),
                    rs.getInt(20) == 0 ? null : rs.getInt(20),
                    rs.getLong(21) == 0 ? null : rs.getLong(21),
                    rs.getInt(22) == 0 ? null : rs.getInt(22),
                    rs.getInt(23) == 0 ? null : rs.getInt(23),
                    rs.getString(24),
                    rs.getString(25),
                    rs.getInt(26),
                    rs.getTimestamp(27),
                    rs.getString(28),
                    rs.getTimestamp(29),
                    rs.getTimestamp(30),
                    rs.getTimestamp(31),
                    rs.getTimestamp(32),
                    rs.getTimestamp(33),
                    rs.getBoolean(34),
                    rs.getInt(35),
                    rs.getInt(36));
        }
    }

    private static class MusicFileInfoMapper implements ParameterizedRowMapper<MediaFile> {
        public MediaFile mapRow(ResultSet rs, int rowNum) throws SQLException {
            MediaFile file = new MediaFile();
            file.setPlayCount(rs.getInt(1));
            file.setLastPlayed(rs.getTimestamp(2));
            file.setComment(rs.getString(3));
            return file;
        }
    }

    private static class GenreMapper implements ParameterizedRowMapper<Genre> {
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Genre(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getInt(4));
        }
    }
}
