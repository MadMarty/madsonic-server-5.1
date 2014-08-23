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

import java.util.Comparator;

import org.madsonic.service.MediaFileService;
import static org.madsonic.domain.MediaFile.MediaType.DIRECTORY;
/**
 * Comparator for sorting media files.
 */
public class MusicFolderComparator implements Comparator<MusicFolder> {

    private static MediaFileService mediaFileService;
    
    public MusicFolderComparator(){
    }
	
    public int compare(MusicFolder a, MusicFolder b) {
    	
        if (a.getName() != null && b.getName() != null) {
            return a.getName().compareToIgnoreCase(b.getName());
        }
		return 0;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }
}

