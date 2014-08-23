/*
 This file is part of Madsonic.

 Madsonic is free software: you can redistribute it and/or modify
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
package org.madsonic.domain;


/**
 * Represents a Security Group.
 *
 */
public class AccessRight {

    private int musicfolder_id;
    private boolean musicfolder_enabled;
    private boolean isEnabled;
    
	public AccessRight(Integer musicfolder_id, boolean musicfolder_Enabled, boolean isEnabled) {
		this.setMusicfolder_id(musicfolder_id);
		this.musicfolder_enabled = musicfolder_Enabled;
        this.isEnabled = isEnabled;
    }
    
    public AccessRight() {}

	/**
	 * @return the isEnabled
	 */
	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * @param isEnabled the isEnabled to set
	 */
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * @return the musicfolder_id
	 */
	public int getMusicfolder_id() {
		return musicfolder_id;
	}

	/**
	 * @param musicfolder_id the musicfolder_id to set
	 */
	public void setMusicfolder_id(int musicfolder_id) {
		this.musicfolder_id = musicfolder_id;
	}

	public boolean getMusicfolder_enabled() {
		return musicfolder_enabled;
	}

	public void setMusicfolder_enabled(boolean musicfolder_enabled) {
		this.musicfolder_enabled = musicfolder_enabled;
	}

}