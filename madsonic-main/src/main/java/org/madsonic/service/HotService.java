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
package org.madsonic.service;

import org.madsonic.dao.*;
import org.madsonic.domain.*;
import org.madsonic.util.FileUtil;

import java.util.*;
import java.io.File;

/**
 * Provides services for user ratings.
 *
 * @author Madevil
 */
public class HotService {

    private HotDao hotDao;
    
    private SecurityService securityService;
    private MediaFileService mediaFileService;

  
    public List<MediaFile> getHotRated(MusicFolder musicFolder, int offset, int count, int user_group_id) {
        List<String> hotRated = hotDao.getHotRated(offset, count);
        List<MediaFile> result = new ArrayList<MediaFile>();
        for (String path : hotRated) {
            File file = new File(path);
            if (FileUtil.exists(file) && securityService.isAccessAllowed(file, user_group_id)) {
            	
            	if (musicFolder != null  ) {
            		if (path.contains(musicFolder.getPath().toString())) {
                        result.add(mediaFileService.getMediaFile(path));
            		}
            	} else {
                    result.add(mediaFileService.getMediaFile(path));
            	}
            }
        }
        Collections.shuffle(result);
        return result;
    }    
    

    public List<MediaFile> getRandomHotRated(int offset, int count, int user_group_id) {
        List<String> hotRated = hotDao.getRandomHotRated(offset, count);
        List<MediaFile> result = new ArrayList<MediaFile>();
        for (String path : hotRated) {
            File file = new File(path);
            if (FileUtil.exists(file) && securityService.isAccessAllowed(file, user_group_id)) {
                result.add(mediaFileService.getMediaFile(path));
            }
        }
        Collections.shuffle(result);
        return result;
    }    
    
    
    
    /**
     * Sets the rating for a music file and a given user.
     *
     * @param username  The user name.
     * @param mediaFile The music file.
     */
    public void setAlbumHotFlag(String username, MediaFile mediaFile) {
    	hotDao.setHotFlag(username, mediaFile);
    }

    public Integer getAlbumHotFlag(MediaFile mediaFile) {
    	return hotDao.getHotFlag(mediaFile);
    }
	
	public int getCountHotFlag(){
		return hotDao.getCountHotFlag();
	}
	
    public void deleteAlbumHotFlag(MediaFile mediaFile) {
    	hotDao.deleteHotFlag(mediaFile);
    }
    
    public void setHotDao(HotDao hotDao) {
        this.hotDao = hotDao;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }



}
