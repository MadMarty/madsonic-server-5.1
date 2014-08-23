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
package org.madsonic.domain;

import org.madsonic.util.StringUtil;

import java.util.Date;

/**
 * @author Sindre Mehus
 */
public class Playlist {

    private int id;
    private String username;
    private int shareLevel;
    private boolean isPublic;
    private String name;
    private String comment;
    private int fileCount;
    private int durationSeconds;
    private Date created;
    private Date changed;
    private String importedFrom;

    public Playlist() {
    }

    public Playlist(int id, String username, int shareLevel, boolean isPublic, String name, String comment, int fileCount,
                    int durationSeconds, Date created, Date changed, String importedFrom) {
        this.id = id;
        this.username = username;
        this.shareLevel = shareLevel;
        this.isPublic = isPublic;
        this.name = name;
        this.comment = comment;
        this.fileCount = fileCount;
        this.durationSeconds = durationSeconds;
        this.created = created;
        this.changed = changed;
        this.importedFrom = importedFrom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getShareLevel() {
        return shareLevel;
    }
    
    public void setShareLevel(int sharelevel) {
        this.shareLevel = sharelevel;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getDurationAsString() {
        return StringUtil.formatDuration(durationSeconds);
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getChanged() {
        return changed;
    }

    public void setChanged(Date changed) {
        this.changed = changed;
    }

    public String getImportedFrom() {
        return importedFrom;
    }

    public void setImportedFrom(String importedFrom) {
        this.importedFrom = importedFrom;
    }
}
