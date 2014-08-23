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

/**
 * Represent a user.
 *
 * @author Sindre Mehus
 */
public class User {

    public static final String USERNAME_ADMIN = "admin";
    public static final String USERNAME_GUEST = "guest";
    public static final String USERNAME_DEMO = "demo";
    public static final String USERNAME_DEFAULT = "default";
    
    private final String username;
    private String password;
    private String email;
    private boolean ldapAuthenticated;
    private long bytesStreamed;
    private long bytesDownloaded;
    private long bytesUploaded;
    private static int groupId;
    private boolean locked;
    private int usergroupId;
    private String comment;

    private boolean isAdminRole;
    private boolean isSettingsRole;
    private boolean isDownloadRole;
    private boolean isUploadRole;
    private boolean isPlaylistRole;
    private boolean isCoverArtRole;
    private boolean isCommentRole;
    private boolean isPodcastRole;
    private boolean isStreamRole;
    private boolean isJukeboxRole;
    private boolean isShareRole;
    private boolean isSearchRole;
    private boolean isLastFMRole;    
    
    public User(String username, String password, String email, boolean ldapAuthenticated,
                long bytesStreamed, long bytesDownloaded, long bytesUploaded, int groupId, boolean locked, String comment) {
    	
        this.username = username;
        this.password = password;
        this.email = email;
        this.ldapAuthenticated = ldapAuthenticated;
        this.bytesStreamed = bytesStreamed;
        this.bytesDownloaded = bytesDownloaded;
        this.bytesUploaded = bytesUploaded;
        this.usergroupId = groupId;
        this.locked = locked;
        this.comment = comment;
    }

    public User(String username, String password, String email) {
        this(username, password, email, false, 0, 0, 0, groupId, false, null);
    }    
    
    public User(String username, String password, String email, boolean locked) {
        this(username, password, email, false, 0, 0, 0, groupId, locked, null);
    }

	public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLdapAuthenticated() {
        return ldapAuthenticated;
    }

    public void setLdapAuthenticated(boolean ldapAuthenticated) {
        this.ldapAuthenticated = ldapAuthenticated;
    }

    public long getBytesStreamed() {
        return bytesStreamed;
    }

    public void setBytesStreamed(long bytesStreamed) {
        this.bytesStreamed = bytesStreamed;
    }

    public long getBytesDownloaded() {
        return bytesDownloaded;
    }

    public void setBytesDownloaded(long bytesDownloaded) {
        this.bytesDownloaded = bytesDownloaded;
    }

    public long getBytesUploaded() {
        return bytesUploaded;
    }

    public void setBytesUploaded(long bytesUploaded) {
        this.bytesUploaded = bytesUploaded;
    }

    /**
	 * @return the groupId
	 */
	public int getGroupId() {
		return usergroupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(int groupId) {
		this.usergroupId = groupId;
	}

	public boolean isAdminRole() {
        return isAdminRole;
    }

    public void setAdminRole(boolean isAdminRole) {
        this.isAdminRole = isAdminRole;
    }

    public boolean isSettingsRole() {
        return isSettingsRole;
    }

    public void setSettingsRole(boolean isSettingsRole) {
        this.isSettingsRole = isSettingsRole;
    }

    public boolean isCommentRole() {
        return isCommentRole;
    }

    public void setCommentRole(boolean isCommentRole) {
        this.isCommentRole = isCommentRole;
    }

    public boolean isDownloadRole() {
        return isDownloadRole;
    }

    public void setDownloadRole(boolean isDownloadRole) {
        this.isDownloadRole = isDownloadRole;
    }

    public boolean isUploadRole() {
        return isUploadRole;
    }

    public void setUploadRole(boolean isUploadRole) {
        this.isUploadRole = isUploadRole;
    }

    public boolean isPlaylistRole() {
        return isPlaylistRole;
    }

    public void setPlaylistRole(boolean isPlaylistRole) {
        this.isPlaylistRole = isPlaylistRole;
    }

    public boolean isCoverArtRole() {
        return isCoverArtRole;
    }

    public void setCoverArtRole(boolean isCoverArtRole) {
        this.isCoverArtRole = isCoverArtRole;
    }

    public boolean isPodcastRole() {
        return isPodcastRole;
    }

    public void setPodcastRole(boolean isPodcastRole) {
        this.isPodcastRole = isPodcastRole;
    }

    public boolean isStreamRole() {
        return isStreamRole;
    }

    public void setStreamRole(boolean streamRole) {
        isStreamRole = streamRole;
    }

    public boolean isJukeboxRole() {
        return isJukeboxRole;
    }

    public void setJukeboxRole(boolean jukeboxRole) {
        isJukeboxRole = jukeboxRole;
    }

    public boolean isShareRole() {
        return isShareRole;
    }

    public void setShareRole(boolean shareRole) {
        isShareRole = shareRole;
    }

    public boolean isSearchRole() {
        return isSearchRole;
    }

    public void setSearchRole(boolean searchRole) {
        isSearchRole = searchRole;
    }    

	public boolean isLastFMRole() {
		return isLastFMRole;
	}

	public void setLastFMRole(boolean isLastFMRole) {
		this.isLastFMRole = isLastFMRole;
	}    
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(username);

        if (isAdminRole) {
            result.append(" [admin]");
        }
        if (isSettingsRole) {
            result.append(" [settings]");
        }
        if (isDownloadRole) {
            result.append(" [download]");
        }
        if (isUploadRole) {
            result.append(" [upload]");
        }
        if (isPlaylistRole) {
            result.append(" [playlist]");
        }
        if (isCoverArtRole) {
            result.append(" [coverart]");
        }
        if (isCommentRole) {
            result.append(" [comment]");
        }
        if (isPodcastRole) {
            result.append(" [podcast]");
        }
        if (isStreamRole) {
            result.append(" [stream]");
        }
        if (isJukeboxRole) {
            result.append(" [jukebox]");
        }
        if (isShareRole) {
            result.append(" [share]");
        }
        if (isSearchRole) {
            result.append(" [search]");
        }
        if (isLastFMRole) {
            result.append(" [lastfm]");
        }
        return result.toString();
    }

	/**
	 * @return the usergroupId
	 */
	public int getUsergroupId() {
		return usergroupId;
	}

	/**
	 * @param usergroupId the usergroupId to set
	 */
	public void setUsergroupId(int usergroupId) {
		this.usergroupId = usergroupId;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
