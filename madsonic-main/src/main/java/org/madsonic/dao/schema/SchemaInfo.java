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
 *
 * @author Madevil
 */
public class SchemaInfo extends Schema {

    private static final Logger LOG = Logger.getLogger(SchemaInfo.class);

    @Override
    public void execute(JdbcTemplate template) {

    	int version = template.queryForInt("select MAX (version) version from version");
        LOG.info("Database schema is version: " + version);
        }
}