/*
 This file is part of Madsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Madsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Madsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2014 (C) Madevil
 */
package org.madsonic.dao.schema;

import org.madsonic.Logger;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Used for creating and evolving the database schema.
 * This class implements the database schema for Madsonic version 4.7.
 *
 * @author Madevil
 */
public class SchemaMadsonic extends Schema {

    private static final Logger LOG = Logger.getLogger(SchemaMadsonic.class);
    
    @Override
    public void execute(JdbcTemplate template) {

		////////////////////

        if (template.queryForInt("select count(*) from version where version = 101") == 0) {
            LOG.info("Updating database schema to version 101.");
            template.execute("insert into version values (101)");
			
			// Reset Usersetting: show_now_playing & show_chat
			if (columnExists(template, "show_chat", "user_settings")) {
				template.execute("update user_settings set show_chat = false, show_now_playing = false");
				LOG.info("Database Update 'user_settings.show_chat' was added successfully.");
				LOG.info("Database Update 'user_settings.show_now_playing' was added successfully.");
			}	
        }
		
		////////////////////

		// Add Statistic Table
        if (!tableExists(template, "statistic_user")) {
            LOG.info("Database table 'statistic_user' not found. Creating it.");
            template.execute("create table statistic_user (" +
                    "id identity," +
                    "username varchar not null," +
                    "media_file_id int not null," +
                    "played datetime not null," +
                    "foreign key (media_file_id) references media_file(id) on delete cascade,"+
                    "foreign key (username) references user(username) on delete cascade)");

            template.execute("create index idx_statistic_user_media_file_id on statistic_user(media_file_id)");
            template.execute("create index idx_statistic_user_username on statistic_user(username)");

            LOG.info("Database table 'statistic_user' was created successfully.");
        }		
            
		////////////////////

		// Add Hot Recommmed Table
		if (!tableExists(template, "hot_rating")) {
			LOG.info("Database table 'hot_rating' not found. Creating it.");
			template.execute("create table hot_rating (" +
							 "username varchar not null," +
							 "path varchar not null," +
							 "id int not null," +
							 "primary key (username, path)," +
							 "foreign key (username) references user(username) on delete cascade)");
		LOG.info("Database table 'hot_rating' was created successfully.");            
            
        }	
		
        if (template.queryForInt("select count(*) from version where version = 102") == 0) {
            LOG.info("Updating database schema to version 102.");
            template.execute("insert into version values (102)");
			
        if (!columnExists(template, "index", "music_folder")) {
            LOG.info("Database column 'music_folder.index' not found. Creating it.");
            template.execute("alter table music_folder add index int default 1 not null");
            LOG.info("Database column 'music_folder.index' was added successfully.");
        }			
        }		

		////////////////////
		
        if (template.queryForInt("select count(*) from version where version = 103") == 0) {
            LOG.info("Updating database schema to version 103.");
            template.execute("insert into version values (103)");
            template.execute("create index idx_starred_media_file_media_file_id_username on starred_media_file(media_file_id, username)");
            template.execute("create index idx_starred_media_file_created on starred_media_file(created)");
            LOG.info("Database index 'idx_starred_media_file_media_file_id_username' was added successfully.");
            LOG.info("Database index 'idx_starred_media_file_created' was added successfully.");
        }	
		
		////////////////////
		
        if (template.queryForInt("select count(*) from version where version = 104") == 0) {
            LOG.info("Updating database schema to version 104.");
            template.execute("insert into version values (104)");

			if (template.queryForInt("select count(*) from role where id = 12") == 0) {
				LOG.info("Role 'search' not found in database. Creating it.");
				template.execute("insert into role values (12, 'search')");
				// default for admin/stream role
				template.execute("insert into user_role " +
								 "select distinct u.username, 12 from user u, user_role ur " +
								 "where u.username = ur.username and ur.role_id = 8");
				LOG.info("Role 'search' was created successfully.");
			}
        }        
        ////////////////////
        
        if (template.queryForInt("select count(*) from version where version = 105") == 0) {
            LOG.info("Updating database schema to version 105.");
            template.execute("insert into version values (105)");

			// Added new Usersettings
			if (!columnExists(template, "customscrollbar", "user_settings")) {
				LOG.info("Database column 'user_settings.customscrollbar' not found. Creating it.");
				template.execute("alter table user_settings add customscrollbar boolean default true not null");
				LOG.info("Database column 'user_settings.customscrollbar' was added successfully.");
			}
        }
        
        ////////////////////

		// Add new User Role 'search' and add as default
        if (template.queryForInt("select count(*) from role where id = 12") == 0) {
            LOG.info("Role 'search' not found in database. Creating it.");
            template.execute("insert into role values (12, 'search')");
			// default for admin/stream role
            template.execute("insert into user_role " +
                             "select distinct u.username, 12 from user u, user_role ur " +
                             "where u.username = ur.username and ur.role_id = 8");
            LOG.info("Role 'search' was created successfully.");
        }

        ////////////////////

		// new transcoding settings
        if (template.queryForInt("select count(*) from version where version = 106") == 0) {
            LOG.info("Updating database schema to version 106.");
            template.execute("insert into version values (106)");

		// wtv transcoding 
	    if (template.queryForInt("select count(*) from transcoding2 where name = 'wtv video'") == 0) {
            template.execute("insert into transcoding2(name, source_formats, target_format, step1) values('wtv video', 'wtv', 'flv', " +
                    "'ffmpeg -ss %o -i %s -async 30 -b %bk -r 23-.976 -s %wx%h -ar 44100 -ac 2 -v 0 -f flv -vcodec libx264 -preset fast -threads 0 -')");		
		}
		// FLAC transcoding
	    if (template.queryForInt("select count(*) from transcoding2 where name = 'FLAC audio'") == 0) {

	    if (template.queryForInt("SELECT count(*) from transcoding2 where source_formats like '%flac%' and name = 'mp3 audio'") == 1) {
			template.execute("update transcoding2 set source_formats = 'ogg oga aac m4a wav wma aif aiff ape mpc shn' " + 
					"where source_formats like '%flac%' and name = 'mp3 audio'");
		}
			template.execute("insert into transcoding2(name, source_formats, target_format, step1, step2) values('FLAC audio', 'flac', 'mp3', " +
					"'ffmpeg -i %s -v 0 -f wav -', 'lame -V 0 --tt %t --ta %a --tl %l -S --resample 44.1 - -')");
		}
		
		// SubWiji transcoding
//	    if (template.queryForInt("select count(*) from transcoding2 where name = 'SubWiji'") == 0) {
//			template.execute("insert into transcoding2(name, source_formats, target_format, step1, default_active) values('SubWiji', 'mp3', 'mp3', " +
//					"'ffmpeg -f mp3 -i %s -ab %bk -v 0 -f mp3 -', false)");
//		}
		
    }
        ////////////////////

		// new transcoding settings
        if (template.queryForInt("select count(*) from version where version = 107") == 0) {
            LOG.info("Updating database schema to version 107.");
            template.execute("insert into version values (107)");

		// FLAC transcoding
	    if (template.queryForInt("select count(*) from transcoding2 where name = 'FLAC audio'") == 1) {

		template.execute("delete from transcoding2 where name = 'FLAC audio'");		
		
	    if (template.queryForInt("SELECT count(*) from transcoding2 where source_formats like '%m4a%' and name = 'mp3 audio'") == 1) {
			template.execute("update transcoding2 set source_formats = 'ogg oga aac wav wma aif aiff ape mpc shn' " + 
					"where source_formats like '%m4a%' and name = 'mp3 audio'");
		}

		template.execute("insert into transcoding2(name, source_formats, target_format, step1, step2) values('m4a/FLAC audio', 'flac m4a', 'mp3', " +
					"'ffmpeg -i %s -v 0 -f wav -', 'lame -V 0 --tt %t --ta %a --tl %l -S --resample 44.1 - -')");
		}
		
		LOG.info("new transcoding in table 'transcoding2' was inserted successfully.");
      }
	  
        ////////////////////
		
		// new transcoding settings
        if (template.queryForInt("select count(*) from version where version = 108") == 0) {
            LOG.info("Updating database schema to version 108.");
            template.execute("insert into version values (108)");

		// FLAC transcoding
	    if (template.queryForInt("select count(*) from transcoding2 where name = 'm4a/FLAC audio'") == 1) {

		template.execute("delete from transcoding2 where name = 'm4a/FLAC audio'");		
		
		template.execute("insert into transcoding2(name, source_formats, target_format, step1 ) values('m4a/FLAC audio', 'm4a flac', 'mp3', " +
					"'Audioffmpeg -i %s -ab 256k -ar 44100 -ac 2 -v 0 -f mp3 -')");
					
		template.execute("update transcoding2 set step1 = 'Audioffmpeg -i %s -ab %bk -v 0 -f mp3 -' where name = 'mp3 audio'");					
//		template.execute("update transcoding2 set step1 = 'Audioffmpeg -f mp3 -i %s -ab %bk -v 0 -f mp3 -' where name = 'SubWiji'");					
					
		}
		
		LOG.info("new transcoding in table 'transcoding2' was inserted successfully.");
      }

		// Cleanup Transcoding
        if (template.queryForInt("select count(*) from version where version = 109") == 0) {
//            LOG.info("Updating database schema to version 109.");
//            template.execute("insert into version values (109)");
		}
	  

		////////////////////

		// new Access Control
        if (template.queryForInt("select count(*) from version where version = 110") == 0) {
            LOG.info("Updating database schema to version 110.");
            template.execute("insert into version values (110)");
		
			// Add Group Table
			if (!tableExists(template, "user_group")) {
				LOG.info("Database table 'user_group' not found. Creating it.");
				template.execute("create table user_group (" +
						"id identity, " +
						"name varchar not null)");  // + "primary key (id))");
				LOG.info("Database table 'user_group' was created successfully.");
			}	

			// Add Group Access Table
			if (!tableExists(template, "user_group_access")) {
				LOG.info("Database table 'user_group_access' not found. Creating it.");
				template.execute("create table user_group_access (" +
								 "user_group_id integer not null, " +
								 "music_folder_id integer not null, " +
								 "enabled boolean default true not null, " + 
								 "primary key (user_group_id, music_folder_id)," +
								 "foreign key (user_group_id) references user_group(id) on delete cascade," +
								 "foreign key (music_folder_id) references music_folder(id) on delete cascade)");
			LOG.info("Database table 'user_group_access' was created successfully.");            

            template.execute("create index idx_user_group_access_user_group_id_music_folder_id_enabled on user_group_access(user_group_id, music_folder_id, enabled)");
            LOG.info("Database index 'idx_user_group_access_user_group_id_music_folder_id_enabled' was added successfully.");
			}	
		}


		////////////////////
		
		// new transcoding settings
        if (template.queryForInt("select count(*) from version where version = 111") == 0) {
            
			LOG.info("Updating database schema to version 111.");
            template.execute("insert into version values (111)");
			
			//ALTER TABLE USER drop constraint FK_2
			//ALTER TABLE USER drop group_id

			template.execute("alter table user add column group_id integer default 0 not null;");

			template.execute("insert into user_group (id, name) values (0, 'ALL')");
			template.execute("insert into user_group (id, name) values (1, 'GUEST')");
			template.execute("insert into user_group (id, name) values (2, 'FAMILY')");
			template.execute("insert into user_group (id, name) values (3, 'FRIENDS')");
			template.execute("insert into user_group (id, name) values (4, 'LIMITED')");
			
			// Insert Default Access to all
			
//			template.execute("insert into user_group_access (user_group_id, music_folder_id, enabled)" + 
//							"(select distinct g.id as user_group_id, f.id as music_folder_id, 'true' as enabled from user_group g, music_folder f)");
			
			template.execute("insert into user_group_access (user_group_id, music_folder_id, enabled)" + 
							 "(select distinct user_group.id as user_group_id, music_folder.id as music_folder_id, 'true' as enabled from public.user_group user_group, public.music_folder music_folder)");

			
			template.execute("alter table user add constraint fk_group_id foreign key (group_id) references user_group (id)");
			
			LOG.info("Database table 'user' was updated successfully.");            
		}

		// Reset Access to default
        if (template.queryForInt("select count(*) from version where version = 112") == 0) {
            
			LOG.info("Updating database schema to version 112.");
            template.execute("insert into version values (112)");
            template.execute("delete from user_group_access");
			template.execute("insert into user_group_access (user_group_id, music_folder_id, enabled)" + 
							 "(select distinct user_group.id as user_group_id, music_folder.id as music_folder_id, 'true' as enabled from public.user_group user_group, public.music_folder music_folder)");
		}	
		
		// Update album table
        if (template.queryForInt("select count(*) from version where version = 113") == 0) {

			LOG.info("Updating database schema to version 113.");
            template.execute("insert into version values (113)");

            // Added new albumartist
			if (!columnExists(template, "albumartist", "album")) {
				
				LOG.info("Database column 'album.albumartist' not found. Creating it.");
				template.execute("alter table album add albumartist varchar");
				
				LOG.info("Database column 'album.albumartist' was added successfully.");
				
	            template.execute("create index idx_album_artist_albumartist_name on album(artist, albumartist, name)");
	            template.execute("create index idx_album_albumartist_name on album(albumartist, name)");

	            LOG.info("Index was created successfully.");
				
			}
		}	
        
        
		// Update album table
        if (template.queryForInt("select count(*) from version where version = 114") == 0) {

			LOG.info("Updating database schema to version 114.");
            template.execute("insert into version values (114)");

            	//Added new artist_folder
			if (!columnExists(template, "artist_folder", "artist")) {
				LOG.info("Database column 'artist.artist_folder' not found. Creating it.");
				template.execute("alter table artist add artist_folder varchar");
				LOG.info("Database column 'artist.artist_folder' was added successfully.");
			}

				// Added new albumartist
			if (!columnExists(template, "play_count", "artist")) {
				LOG.info("Database column 'artist.play_count' not found. Creating it.");
				template.execute("alter table artist add play_count int default 0 not null");
				LOG.info("Database column 'artist.play_count' was added successfully.");
			}
				
	            // Added new albumartist
			if (!columnExists(template, "song_count", "artist")) {
				LOG.info("Database column 'artist.song_count' not found. Creating it.");
				template.execute("alter table artist add song_count int default 0 not null");
				LOG.info("Database column 'artist.song_count' was added successfully.");
			}
		}	 
        
        
		// Update album table
        if (template.queryForInt("select count(*) from version where version = 115") == 0) {

			LOG.info("Updating database schema to version 115.");
            template.execute("insert into version values (115)");

            	//Added new artist_folder
			if (!columnExists(template, "mediaFileId", "album")) {
				LOG.info("Database column 'album.mediaFileId' not found. Creating it.");
				template.execute("alter table album add mediaFileId int");
				LOG.info("Database column 'album.mediaFileId' was added successfully.");
			}

				// Added new albumartist
			if (!columnExists(template, "genre", "album")) {
				LOG.info("Database column 'album.genre' not found. Creating it.");
				template.execute("alter table album add genre varchar");
				LOG.info("Database column 'album.genre' was added successfully.");
			}
				
	            // Added new albumartist
			if (!columnExists(template, "year", "album")) {
				LOG.info("Database column 'album.year' not found. Creating it.");
				template.execute("alter table album add year int");
				LOG.info("Database column 'album.year' was added successfully.");
			}
		}	        


		// Update album table
        if (template.queryForInt("select count(*) from version where version = 116") == 0) {

			LOG.info("Updating database schema to version 116.");
            template.execute("insert into version values (116)");

            	//Added new index
	            template.execute("create index idx_artist_artist_folder_name on artist(artist_folder, name)");
	            template.execute("create index idx_album_mediaFileId on album(mediaFileId)");
	            template.execute("create index idx_album_genre on album(genre)");
	            template.execute("create index idx_album_year on album(year)");
				LOG.info("Database index was added successfully.");
			}
        
        
        ////////////////////
        
        if (template.queryForInt("select count(*) from version where version = 117") == 0) {
            LOG.info("Updating database schema to version 117.");
            template.execute("insert into version values (117)");

			// Added new Usersettings
			if (!columnExists(template, "customaccordion", "user_settings")) {
				LOG.info("Database column 'user_settings.customaccordion' not found. Creating it.");
				template.execute("alter table user_settings add customaccordion boolean default false not null");
				LOG.info("Database column 'user_settings.customaccordion' was added successfully.");
			}
        }
        
		////////////////////

        if (template.queryForInt("select count(*) from version where version = 118") == 0) {
            LOG.info("Updating database schema to version 118.");
            template.execute("insert into version values (118)");
			
			// Added new Usersettings
			if (!columnExists(template, "autohide_chat", "user_settings")) {
				LOG.info("Database column 'user_settings.autohide_chat' not found. Creating it.");
				template.execute("alter table user_settings add autohide_chat boolean default false not null");
				LOG.info("Database column 'user_settings.autohide_chat' was added successfully.");
			}
        }        
        
		////////////////////
        if (template.queryForInt("select count(*) from version where version = 119") == 0) {
            LOG.info("Updating database schema to version 119.");
            template.execute("insert into version values (119)");

			// Added new audio_default_bitrate
			if (!columnExists(template, "audio_default_bitrate", "user_group")) {
				LOG.info("Database column 'user_group.audio_default_bitrate' not found. Creating it.");
				template.execute("alter table user_group add audio_default_bitrate int default 0 not null");
				LOG.info("Database column 'user_group.audio_default_bitrate' was added successfully.");
			}

			// Added new audio_max_bitrate
			if (!columnExists(template, "audio_max_bitrate", "user_group")) {
				LOG.info("Database column 'user_group.audio_max_bitrate' not found. Creating it.");
				template.execute("alter table user_group add audio_max_bitrate int default 0 not null");
				LOG.info("Database column 'user_group.audio_max_bitrate' was added successfully.");
			}		            
            
			// Added new video_default_bitrate
			if (!columnExists(template, "video_default_bitrate", "user_group")) {
				LOG.info("Database column 'user_group.video_default_bitrate' not found. Creating it.");
				template.execute("alter table user_group add video_default_bitrate int default 1000 not null");
				LOG.info("Database column 'user_group.video_default_bitrate' was added successfully.");
			}

			// Added new video_max_bitrate
			if (!columnExists(template, "video_max_bitrate", "user_group")) {
				LOG.info("Database column 'user_group.video_max_bitrate' not found. Creating it.");
				template.execute("alter table user_group add video_max_bitrate int default 10000 not null");
				LOG.info("Database column 'user_group.video_max_bitrate' was added successfully.");
			}			
			
        }
        /////////////////////
        
        
        ////////////////////
        if (template.queryForInt("select count(*) from version where version = 123") == 0) {
            LOG.info("Updating database schema to version 123.");
            template.execute("insert into version values (123)");

    		// Added new albumartist
        	if (!columnExists(template, "mood", "media_file")) {
        		LOG.info("Database column 'media_file.mood' not found. Creating it.");
        		template.execute("alter table media_file add mood varchar");
        		LOG.info("Database column 'media_file.mood' was added successfully.");
        	}
        }
		////////////////////

        ////////////////////
        if (template.queryForInt("select count(*) from version where version = 124") == 0) {
            LOG.info("Updating database schema to version 124.");
            template.execute("insert into version values (124)");

    		// Added new albumartist
        	if (!columnExists(template, "first_scanned", "media_file")) {
        		LOG.info("Database column 'media_file.first_scanned' not found. Creating it.");
        		template.execute("alter table media_file add first_scanned datetime");
        		LOG.info("Database column 'media_file.first_scanned' was added successfully.");
        	}
        }
		////////////////////
 
		// Update album table
        if (template.queryForInt("select count(*) from version where version = 125") == 0) {

			LOG.info("Updating database schema to version 125.");
            template.execute("insert into version values (125)");

            // Added new albumartist
			if (!columnExists(template, "nameid3", "album")) {
				
				LOG.info("Database column 'album.nameid3' not found. Creating it.");
				template.execute("alter table album add nameid3 varchar");
				LOG.info("Database column 'album.nameid3' was added successfully.");

				template.execute("create index idx_album_nameid3 on album(nameid3)");
	            LOG.info("Index was created successfully.");

				template.execute("delete from album");
				LOG.info("Database table ablum cleared successfully.");
	            
			}
		} 
		////////////////////

		// Update album table
        if (template.queryForInt("select count(*) from version where version = 126") == 0) {

			LOG.info("Updating database schema to version 126.");
            template.execute("insert into version values (126)");

            // Added new albumartist
			if (!columnExists(template, "genre", "artist")) {
				
				LOG.info("Database column 'artist.genre' not found. Creating it.");
				template.execute("alter table artist add genre varchar");
				LOG.info("Database column 'artist.genre' was added successfully.");

				template.execute("create index idx_artist_genre on artist(genre)");
	            LOG.info("Index was created successfully.");
          
			}
		} 
    		////////////////////
        	
    		// Update 
            if (template.queryForInt("select count(*) from version where version = 127") == 0) {

    			LOG.info("Updating database schema to version 127.");
                template.execute("insert into version values (127)");

                if (!tableExists(template, "statistic_server")) {
                    LOG.info("database table 'statistic_server' not found.  creating it.");
                    template.execute("create cached table statistic_server (" +
                            "id identity," +
                            "music_folder_id int not null," +
                    		"artist_all int," + 
                    		"artist_album int," +
                    		"albums int," +
                    		"genre int," +
                    		"music_files int," + 
                    		"music_files_size bigint," +
                    		"video_files int," + 
                    		"video_files_size bigint," +
                    		"podcast_files int," +
                    		"podcast_files_size bigint," +	 
                    		"audiobooks_files int," +
                    		"audiobooks_files_size bigint," +
                            "foreign key (music_folder_id) references music_folder(id) on delete cascade,"+
                            "unique (music_folder_id))");

                    template.execute("create index idx_statistic_server_id on statistic_server(id)");
                    template.execute("create index idx_statistic_server_music_folder_id on statistic_server(music_folder_id)");

                    LOG.info("Database table 'statistic_server' was created successfully.");                
    			}
    		} 
            
            ////////////////////
            
            if (template.queryForInt("select count(*) from version where version = 128") == 0) {
                LOG.info("Updating database schema to version 128.");
                template.execute("insert into version values (128)");

    			// Added new Usersettings
    			if (!columnExists(template, "selected_genre", "user_settings")) {
    				LOG.info("Database column 'user_settings.selected_genre' not found. Creating it.");
    				template.execute("alter table user_settings add selected_genre varchar");
    				LOG.info("Database column 'user_settings.selected_genre' was added successfully.");
    			}
            }
            
    		////////////////////
            
            ////////////////////
            if (template.queryForInt("select count(*) from version where version = 129") == 0) {
                LOG.info("Updating database schema to version 129.");
                template.execute("insert into version values (129)");

        		// Added new albumartist
            	if (!columnExists(template, "sharelevel", "playlist")) {
            		LOG.info("Database column 'playlist.sharelevel' not found. Creating it.");
            		template.execute("alter table playlist add sharelevel int");
            		LOG.info("Database column 'playlist.sharelevel' was added successfully.");
            	}
            }
    		//////////////////// 
 
        if (template.queryForInt("select count(*) from version where version = 131") == 0) {
            LOG.info("Updating database schema to version 131.");
            template.execute("insert into version values (131)");

			// Added new Usersettings
			if (!columnExists(template, "main_mood", "user_settings")) {
				LOG.info("Database column 'user_settings.main_mood' not found. Creating it.");
				template.execute("alter table user_settings add main_mood boolean default false not null");
				LOG.info("Database column 'user_settings.main_mood' was added successfully.");
			}
			
			// Added new Usersettings
			if (!columnExists(template, "playlist_mood", "user_settings")) {
				LOG.info("Database column 'user_settings.playlist_mood' not found. Creating it.");
				template.execute("alter table user_settings add playlist_mood boolean default false not null");
				LOG.info("Database column 'user_settings.playlist_mood' was added successfully.");
			}
			
			
        }
        
		//////////////////// 
      if (template.queryForInt("select count(*) from version where version = 135") == 0) {
			template.execute("insert into version values (135)");
			LOG.info("Updating database schema to version 135.");

			if (!columnExists(template, "locked", "user")) {
				template.execute("alter table user add locked boolean default false not null");
				LOG.info("Database table 'user' was updated successfully.");            
			}
      }
		//////////////////// 
      

        ////////////////////
        if (template.queryForInt("select count(*) from version where version = 139") == 0) {
            LOG.info("Updating database schema to version 139.");
            template.execute("insert into version values (139)");

    		// Added new albumartist
        	if (!columnExists(template, "rank", "media_file")) {
        		LOG.info("Database column 'media_file.rank' not found. Creating it.");
        		template.execute("alter table media_file add rank int default 0 not null");
        		LOG.info("Database column 'media_file.rank' was added successfully.");
        	}
        }
		////////////////////

		//////////////////// 
      if (template.queryForInt("select count(*) from version where version = 140") == 0) {
			template.execute("insert into version values (140)");
			LOG.info("Updating database schema to version 140.");

			if (!columnExists(template, "comment", "user")) {
				template.execute("alter table user add comment varchar");
				LOG.info("Database table 'user' was updated successfully.");            
			}
      }
		//////////////////// 
       
      
		// Update user_settings
      if (!columnExists(template, "button_rank", "user_settings")) {
          LOG.info("Database column 'user_settings.button_rank' not found. Creating it.");
          template.execute("alter table user_settings add button_rank boolean default true not null");
          LOG.info("Database column 'user_settings.button_rank' was added successfully.");
      }

      if (!columnExists(template, "button_starred", "user_settings")) {
          LOG.info("Database column 'user_settings.button_starred' not found. Creating it.");
          template.execute("alter table user_settings add button_starred boolean default true not null");
          LOG.info("Database column 'user_settings.button_starred' was added successfully.");
      }

      if (!columnExists(template, "button_play", "user_settings")) {
          LOG.info("Database column 'user_settings.button_play' not found. Creating it.");
          template.execute("alter table user_settings add button_play boolean default true not null");
          LOG.info("Database column 'user_settings.button_play' was added successfully.");
      }

      if (!columnExists(template, "button_playadd", "user_settings")) {
          LOG.info("Database column 'user_settings.button_playadd' not found. Creating it.");
          template.execute("alter table user_settings add button_playadd boolean default true not null");
          LOG.info("Database column 'user_settings.button_playadd' was added successfully.");
      }
      
      if (!columnExists(template, "button_playmore", "user_settings")) {
          LOG.info("Database column 'user_settings.button_playmore' not found. Creating it.");
          template.execute("alter table user_settings add button_playmore boolean default false not null");
          LOG.info("Database column 'user_settings.button_playmore' was added successfully.");
      }

      if (!columnExists(template, "button_addcontext", "user_settings")) {
          LOG.info("Database column 'user_settings.button_addcontext' not found. Creating it.");
          template.execute("alter table user_settings add button_addcontext boolean default true not null");
          LOG.info("Database column 'user_settings.button_addcontext' was added successfully.");
      }

      if (!columnExists(template, "button_addnext", "user_settings")) {
          LOG.info("Database column 'user_settings.button_addnext' not found. Creating it.");
          template.execute("alter table user_settings add button_addnext boolean default false not null");
          LOG.info("Database column 'user_settings.button_addnext' was added successfully.");
      }

      if (!columnExists(template, "button_addlast", "user_settings")) {
          LOG.info("Database column 'user_settings.button_addlast' not found. Creating it.");
          template.execute("alter table user_settings add button_addlast boolean default false not null");
          LOG.info("Database column 'user_settings.button_addlast' was added successfully.");
      }
      
      if (!columnExists(template, "button_download", "user_settings")) {
          LOG.info("Database column 'user_settings.button_download' not found. Creating it.");
          template.execute("alter table user_settings add button_download boolean default true not null");
          LOG.info("Database column 'user_settings.button_download' was added successfully.");
      }

      if (!columnExists(template, "button_youtube", "user_settings")) {
          LOG.info("Database column 'user_settings.button_youtube' not found. Creating it.");
          template.execute("alter table user_settings add button_youtube boolean default true not null");
          LOG.info("Database column 'user_settings.button_youtube' was added successfully.");
      }

      if (!columnExists(template, "main_disc_number", "user_settings")) {
          LOG.info("Database column 'user_settings.main_disc_number' not found. Creating it.");
          template.execute("alter table user_settings add main_disc_number boolean default false not null");
          LOG.info("Database column 'user_settings.main_disc_number' was added successfully.");
      }

      if (!columnExists(template, "playlist_disc_number", "user_settings")) {
          LOG.info("Database column 'user_settings.playlist_disc_number' not found. Creating it.");
          template.execute("alter table user_settings add playlist_disc_number boolean default false not null");
          LOG.info("Database column 'user_settings.playlist_disc_number' was added successfully.");
      }      
      
		////////////////////
		
		if (template.queryForInt("select count(*) from role where id = 13") == 0) {
			LOG.info("Role 'lastfm' not found in database. Creating it.");
			template.execute("insert into role values (13, 'lastfm')");
			// default for admin/stream role
			template.execute("insert into user_role " +
							 "select distinct u.username, 13 from user u, user_role ur " +
							 "where u.username = ur.username and ur.role_id = 8");
			LOG.info("Role 'lastfm' was created successfully.");
		}
      ////////////////////      

		//////////////////// 
	      if (template.queryForInt("select count(*) from version where version = 143") == 0) {
				template.execute("insert into version values (143)");
				LOG.info("Updating database schema to version 143.");

				if (!columnExists(template, "type", "music_folder")) {
					template.execute("alter table music_folder add type int default 1 not null");
					LOG.info("Database table 'music_folder' was updated successfully.");            
				}
	      }
			//////////////////// 		
		
			if (!columnExists(template, "groupby", "music_folder")) {
				template.execute("alter table music_folder add groupby int default 0 not null");
				LOG.info("Database table 'music_folder' was updated successfully.");            
			}	
      ////////////////////

			//////////////////// 
		      if (template.queryForInt("select count(*) from version where version = 144") == 0) {
					template.execute("insert into version values (144)");
					LOG.info("Updating database schema to version 144.");
					
					template.execute("delete from transcoding2");
					template.execute("delete from player_transcoding2");
			        template.execute("insert into transcoding2(name, source_formats, target_format, step1) values('video->flv', 'avi flv mpg mpeg mp4 m4v mkv mov wmv ogv divx m2ts', 'flv', 'ffmpeg -ss %o -i %s -async 1 -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f flv -c:v libx264 -preset superfast -threads 0 -')");		
			        template.execute("insert into transcoding2(name, source_formats, target_format, step1) values('video->mkv', 'avi flv mpg mpeg mp4 m4v mkv mov wmv ogv divx m2ts', 'mkv', 'ffmpeg -ss %o -i %s -c:v libx264 -preset superfast -b:v %bk -c:a libvorbis -f matroska -threads 0 -')");		
			        template.execute("insert into transcoding2(name, source_formats, target_format, step1) values('video->webm', 'avi flv mpg mpeg mp4 m4v mkv mov wmv ogv divx m2ts', 'webm', 'ffmpeg -ss %o -t %d -i %s -async 1 -vf lutyuv=y=val*1.3 -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f webm -c:v libvpx -preset superfast -c:a libvorbis -threads 0 -')");		
			        template.execute("insert into transcoding2(name, source_formats, target_format, step1) values('wtv->flv', 'wtv', 'flv', 'ffmpeg -ss %o -i %s -async 30 -b %bk -r 23-.976 -s 720x360 -ar 44100 -ac 2 -v 0 -f flv -c:v libx264 -preset fast -threads 0 -')");		
			        template.execute("insert into transcoding2(name, source_formats, target_format, step1) values('hdrun->flv', 'hdrun', 'flv', 'ffmpeg -ss %o -i %z -async 1 -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f flv -c:v libx264 -preset superfast -threads 0 -')");		
			        template.execute("insert into transcoding2(name, source_formats, target_format, step1) values('hdrun->mkv', 'hdrun', 'mkv', 'ffmpeg -ss %o -i %z -c:v libx264 -preset superfast -b:v %bk -c:a libvorbis -f matroska -threads 0 -')");		
					template.execute("insert into transcoding2(name, source_formats, target_format, step1, step2) values('tv->flv', 'tv', 'flv', 'tail -n1 %s', 'ffmpeg -ss %o -i - -async 1 -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f flv -c:v libx264 -preset superfast -threads 0 -')");
					template.execute("insert into transcoding2(name, source_formats, target_format, step1, step2) values('tv->mkv', 'tv', 'mkv', 'tail -n1 %s', 'ffmpeg -ss %o -i - -c:v libx264 -preset superfast -b:v %bk -c:a libvorbis -f matroska -threads 0 -')");
					template.execute("insert into transcoding2(name, source_formats, target_format, step1, default_active) values('audio->ogg', 'aac wav wma aif aiff ape mp3 m4a mpc shn flac', 'ogg', 'ffmpeg -i %s -map 0:0 -vn -c:a libvorbis -ar 44100 -b:a %bk -v 0 -f ogg -', false)");		
					template.execute("insert into transcoding2(name, source_formats, target_format, step1) values('audio->mp3', 'ogg oga aac wav wma aif aiff ape m4a mpc shn flac', 'mp3', 'ffmpeg -i %s -map 0:0 -b:a %bk -v 0 -f mp3 -')");		
					template.execute("insert into transcoding2(name, source_formats, target_format, step1, step2) values('mod->mp3', 'alm 669 mdl far xm mod fnk imf it liq mod wow mtm ptm rtm stm s3m ult dmf dbm med okt emod sfx m15 mtn amf gdm stx gmc psm j2b umx amd rad hsc flx gtk mgt mtp', 'mp3', 'xmp -Dlittle-endian -q -c %s', 'lame -r -b %b -S --resample 44.1 - -')");
					template.execute("insert into player_transcoding2(player_id, transcoding_id) select distinct p.id, t.id from player p, transcoding2 t where t.name <> 'audio->ogg'");
					
			        LOG.info("##Transcoding reseted to new Madsonic defaults");
			        
		      }
				//////////////////// 				
			
				//////////////////// 
		      if (template.queryForInt("select count(*) from version where version = 145") == 0) {
					template.execute("insert into version values (145)");
					LOG.info("Updating database schema to version 145.");
					
					//////////////////// 			
					// Update media_file
					if (!columnExists(template, "data", "media_file")) {
						LOG.info("Database column 'media_file.data' not found. Creating it.");
						template.execute("alter table media_file add data varchar");
						LOG.info("Database column 'media_file.data' was added successfully.");
						template.execute("create index idx_media_file_data on media_file(data)");
						LOG.info("Database index 'media_file.data' was created successfully.");
					}	
		
			        LOG.info("##Transcoding reseted to new FLV Madsonic defaults");
			        
		      }

				//////////////////// 
		      if (template.queryForInt("select count(*) from version where version = 146") == 0) {
					template.execute("insert into version values (146)");
					LOG.info("Updating database schema to version 146.");
					
					//////////////////// 			
					// Update media_file
					if (!columnExists(template, "topplay_found", "artist")) {
						LOG.info("Database column 'artist.topplay_found' not found. Creating it.");
						
						template.execute("alter table artist add topplay_found int default 0 not null");
						LOG.info("Database column 'artist.topplay_found' was added successfully.");
					}	

					if (!columnExists(template, "topplay_count", "artist")) {
						LOG.info("Database column 'artist.topplay_count' not found. Creating it.");
						
						template.execute("alter table artist add topplay_count int default 0 not null");
						LOG.info("Database column 'artist.topplay_count' was added successfully.");
					}						
			        
		      }		
	}
 

}