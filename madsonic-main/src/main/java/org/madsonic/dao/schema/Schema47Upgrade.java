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

 Copyright 2012 (C) Madevil
 */
package org.madsonic.dao.schema;

import org.madsonic.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Used for creating and evolving the database schema.
 * This class implements the database schema for Subsonic version 4.7 Upgrade.
 *
 * @author Madevil
 */
public class Schema47Upgrade extends Schema {

    private static final Logger LOG = Logger.getLogger(Schema47Upgrade.class);

    @Override
    public void execute(JdbcTemplate template) {

        if (template.queryForInt("select count(*) from version where version = 100") == 0) {
            LOG.info("Updating database schema to version 100.");
            template.execute("insert into version values (100)");
        }

        // Added in 4.7.beta3
        if (!rowExists(template, "table_name='ALBUM' and column_name='NAME' and ordinal_position=1", "information_schema.system_indexinfo")) {
            template.execute("create index idx_album_name on album(name)");
        }		
		
		// Added new Usersettings
        if (!columnExists(template, "list_type", "user_settings")) {
            LOG.info("Database column 'user_settings.list_type' not found.  Creating it.");
            template.execute("alter table user_settings add list_type varchar default 'random' not null");
            LOG.info("Database column 'user_settings.list_type' was added successfully.");
        }

		// Update user_settings
        if (!columnExists(template, "list_rows", "user_settings")) {
            LOG.info("Database column 'user_settings.list_rows' not found.  Creating it.");
            template.execute("alter table user_settings add list_rows int default 2");
            LOG.info("Database column 'user_settings.list_rows' was added successfully.");
        }

        if (!columnExists(template, "list_columns", "user_settings")) {
            LOG.info("Database column 'user_settings.list_columns' not found.  Creating it.");
            template.execute("alter table user_settings add list_columns int default 5");
            LOG.info("Database column 'user_settings.list_columns' was added successfully.");
        }

        if (!columnExists(template, "playqueue_resize", "user_settings")) {
            LOG.info("Database column 'user_settings.playqueue_resize' not found.  Creating it.");
            template.execute("alter table user_settings add playqueue_resize boolean default false not null");
            LOG.info("Database column 'user_settings.playqueue_resize' was added successfully.");
        }

        if (!columnExists(template, "leftframe_resize", "user_settings")) {
            LOG.info("Database column 'user_settings.leftframe_resize' not found.  Creating it.");
            template.execute("alter table user_settings add leftframe_resize boolean default false not null");
            LOG.info("Database column 'user_settings.leftframe_resize' was added successfully.");
        }

	    if (!columnExists(template, "leftframe_resize", "user_settings")) {
            LOG.info("Database column 'user_settings.leftframe_resize' not found.  Creating it.");
            template.execute("alter table user_settings add leftframe_resize boolean default false not null");
            LOG.info("Database column 'user_settings.leftframe_resize' was added successfully.");
        }

		// Update album Table
        if (!columnExists(template, "SetName", "album")) {
            LOG.info("Database column 'album.SetName' not found.  Creating it.");
            template.execute("alter table album add SetName varchar");
			template.execute("create index idx_album_SetName on album(SetName)");
            LOG.info("Database column 'album.SetName' was added successfully.");
			}		
	
		// Update media_file
        if (!columnExists(template, "override", "media_file")) {
            LOG.info("Database column 'media_file.override' not found.  Creating it.");
            template.execute("alter table media_file add override boolean default false not null");
            LOG.info("Database column 'media_file.override' was added successfully.");
        }		
	
        if (!columnExists(template, "album_name", "media_file")) {
            LOG.info("Database column 'media_file.album_name' not found.  Creating it.");
            template.execute("alter table media_file add album_name varchar");
            template.execute("create index idx_media_file_album_name on media_file(album_name)");
            LOG.info("Database column 'media_file.album_name' was added successfully.");
			}		
			

//		 Update to new Version System
//         if (template.queryForInt("select count(*) from version where version = 21") == 1) {
//             template.execute("delete from version where version= 21");

//        if (template.queryForInt("select count(*) from version where version = 31") == 1) {
//            template.execute("update version set version = 101 where version= 31");}
//			
//        if (template.queryForInt("select count(*) from version where version = 32") == 1) {
//            template.execute("update version set version = 102 where version= 32");}
//
//        if (template.queryForInt("select count(*) from version where version = 33") == 1) {
//            template.execute("update version set version = 103 where version= 33");}
//
//        if (template.queryForInt("select count(*) from version where version = 34") == 1) {
//            template.execute("update version set version = 104 where version= 34");}
//			
//        if (template.queryForInt("select count(*) from version where version = 35") == 1) {
//            template.execute("update version set version = 105 where version= 35");}
//
//        if (template.queryForInt("select count(*) from version where version = 36") == 1) {
//            template.execute("update version set version = 106 where version= 36");}
//
//			LOG.info("Updating database schema to new Version System.");
//        }
			
    }
}