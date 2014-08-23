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
 * Represents a Security Group.
 *
 */
public class Group {

    private Integer id;
    private String name;
    
    private Integer AudioDefaultBitrate;
    private Integer AudioMaxBitrate;

    private Integer VideoDefaultBitrate;
    private Integer VideoMaxBitrate;

    public Group(Integer id, String name, Integer AudioDefaultBitrate, Integer AudioMaxBitrate, Integer VideoDefaultBitrate, Integer VideoMaxBitrate) {
    	
        this.id = id;
        this.name = name;
        
        this.AudioDefaultBitrate = AudioDefaultBitrate;
        this.AudioMaxBitrate = AudioMaxBitrate;
        this.VideoDefaultBitrate = VideoDefaultBitrate;
        this.VideoMaxBitrate = VideoMaxBitrate;
    }    
    
    public Group(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Group(String name) {
        this(null, name);
    }

    public Group() {
	}

	public int getId() {
        return id;
    }

	public void setId(int id) {
		this.id = id;
	}
	
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	/**
	 * @return the audioDefaultBitrate
	 */
	public Integer getAudioDefaultBitrate() {
		return AudioDefaultBitrate;
	}

	/**
	 * @param audioDefaultBitrate the audioDefaultBitrate to set
	 */
	public void setAudioDefaultBitrate(Integer audioDefaultBitrate) {
		AudioDefaultBitrate = audioDefaultBitrate;
	}

	/**
	 * @return the audioMaxBitrate
	 */
	public Integer getAudioMaxBitrate() {
		return AudioMaxBitrate;
	}

	/**
	 * @param audioMaxBitrate the audioMaxBitrate to set
	 */
	public void setAudioMaxBitrate(Integer audioMaxBitrate) {
		AudioMaxBitrate = audioMaxBitrate;
	}

	/**
	 * @return the videoDefaultBitrate
	 */
	public Integer getVideoDefaultBitrate() {
		return VideoDefaultBitrate;
	}

	/**
	 * @param videoDefaultBitrate the videoDefaultBitrate to set
	 */
	public void setVideoDefaultBitrate(Integer videoDefaultBitrate) {
		VideoDefaultBitrate = videoDefaultBitrate;
	}

	/**
	 * @return the videoMaxBitrate
	 */
	public Integer getVideoMaxBitrate() {
		return VideoMaxBitrate;
	}

	/**
	 * @param videoMaxBitrate the videoMaxBitrate to set
	 */
	public void setVideoMaxBitrate(Integer videoMaxBitrate) {
		VideoMaxBitrate = videoMaxBitrate;
	}

}