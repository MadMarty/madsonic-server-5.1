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
import org.madsonic.dao.schema.SchemaInfo;
import org.madsonic.dao.schema.Schema;
import org.madsonic.dao.schema.Schema25;
import org.madsonic.dao.schema.Schema26;
import org.madsonic.dao.schema.Schema27;
import org.madsonic.dao.schema.Schema28;
import org.madsonic.dao.schema.Schema29;
import org.madsonic.dao.schema.Schema30;
import org.madsonic.dao.schema.Schema31;
import org.madsonic.dao.schema.Schema32;
import org.madsonic.dao.schema.Schema33;
import org.madsonic.dao.schema.Schema34;
import org.madsonic.dao.schema.Schema35;
import org.madsonic.dao.schema.Schema36;
import org.madsonic.dao.schema.Schema37;
import org.madsonic.dao.schema.Schema38;
import org.madsonic.dao.schema.Schema40;
import org.madsonic.dao.schema.Schema43;
import org.madsonic.dao.schema.Schema45;
import org.madsonic.dao.schema.Schema46;
import org.madsonic.dao.schema.Schema47;
import org.madsonic.dao.schema.Schema49;
import org.madsonic.dao.schema.Schema47Upgrade;
import org.madsonic.dao.schema.Schema50;
import org.madsonic.dao.schema.SchemaMadsonic;
import org.madsonic.dao.schema.SchemaLastFM;
import org.madsonic.service.SettingsService;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import java.io.File;
import java.sql.SQLException;

/**
 * DAO helper class which creates the data source, and updates the database schema.
 *
 * @author Sindre Mehus
 */
public class DaoHelper {

    private static final Logger LOG = Logger.getLogger(DaoHelper.class);

    private Schema[] schemas = {new Schema25(), new Schema26(), new Schema27(), new Schema28(), new Schema29(),
                                new Schema30(), new Schema31(), new Schema32(), new Schema33(), new Schema34(),
                                new Schema35(), new Schema36(), new Schema37(), new Schema38(), new Schema40(),
                                new Schema43(), new Schema45(), new Schema46(), new Schema47(), 
                                new Schema47Upgrade(),
                                new SchemaMadsonic(),								
								new SchemaLastFM(),
    							new Schema49(), 
    							new Schema50(),
    							new SchemaInfo()};
    
    private DataSource dataSource;
    private static boolean shutdownHookAdded;

    public DaoHelper() {
        dataSource = createDataSource();
        checkDatabase();
		try {
			LOG.info("Checking HSQLDB version: " + dataSource.getConnection().getMetaData().getDatabaseProductVersion() );
		} catch (SQLException e) {
		}
        addShutdownHook();
    }

    public String getVersion() {
		try {
			return dataSource.getConnection().getMetaData().getDatabaseProductVersion();
		} catch (Exception e) {
			return null;
		}
    }
    
    private void addShutdownHook() {
        if (shutdownHookAdded) {
            return;
        }
        shutdownHookAdded = true;
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.err.println("Shutting down database...");
                getJdbcTemplate().execute("shutdown");
                
                System.err.println("Shutting down database - Done!");
            }
        });
    }

    /**
     * Returns a JDBC template for performing database operations.
     *
     * @return A JDBC template.
     */
    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    private DataSource createDataSource() {
    	
        File madsonicHome = SettingsService.getMadsonicHome();
        DriverManagerDataSource ds = new DriverManagerDataSource();
        
        ds.setDriverClassName("org.hsqldb.jdbcDriver");
        ds.setUrl("jdbc:hsqldb:file:" + madsonicHome.getPath() + "/db/madsonic;sql.enforce_size=false");
        ds.setUsername("sa");
        ds.setPassword("");
        
        return ds;
    }

    private void checkDatabase() {
        LOG.info("Checking database schema.");
        try {
            for (Schema schema : schemas) {
                schema.execute(getJdbcTemplate());
            }
            LOG.info("Done checking database schema.");
            
            
            
        } catch (Exception x) {
            LOG.error("Failed to initialize database.", x);
        }
    }
}
