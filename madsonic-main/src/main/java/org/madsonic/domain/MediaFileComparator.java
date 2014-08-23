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
public class MediaFileComparator implements Comparator<MediaFile> {

    private static MediaFileService mediaFileService;
    private boolean sortAlbumsByFolder = false;
    private boolean sortFilesByFilename = false;
    
    public MediaFileComparator(){
    }

    public MediaFileComparator(boolean sortAlbumsByFolder, boolean sortFilesByFilename) {
        this.sortAlbumsByFolder = sortAlbumsByFolder;
        this.sortFilesByFilename = sortFilesByFilename;
    }
    
    public int compare(MediaFile a, MediaFile b) {
        if (a.isFile() && b.isDirectory()) {
            return 1;
        }

        if (a.isDirectory() && b.isFile()) {
            return -1;
        }

        // Non-album directories before album directories.
        if (a.isAlbum() && b.getMediaType() == DIRECTORY) {
            return 1;
        }
        if (a.getMediaType() == DIRECTORY && b.isAlbum()) {
            return -1;
        }
		
		MediaFile parentA = mediaFileService.getMediaFile(a.getParentPath()); 	
		
		if (parentA.isMultiArtist()) {
			if (a.getAlbumSetName() != null && b.getAlbumSetName() != null)  {
				return a.getAlbumSetName().compareToIgnoreCase(b.getAlbumSetName());
			} else {
				return a.getName().compareToIgnoreCase(b.getName());
			}         	
		}

		if (sortAlbumsByFolder && a.isSingleArtist() && b.isSingleArtist()) {
            return a.getName().compareToIgnoreCase(b.getName());
		}            

		if (a.isSingleArtist() && b.isSingleArtist()) {
            int i = nullSafeCompare(b.getYear(), a.getYear(), false);
            if (i != 0) {
                return i;
            }
		}   		
		
		
        if (sortAlbumsByFolder && a.isAlbum() && b.isAlbum()) {
			if (a.getAlbumSetName() != null && b.getAlbumSetName() != null)  {
				return a.getAlbumSetName().compareToIgnoreCase(b.getAlbumSetName());
			} else {
				return a.getName().compareToIgnoreCase(b.getName());
			} 
		}		
        
        if (a.isAlbum() && b.isAlbum()) {
            int i = nullSafeCompare(b.getYear(), a.getYear(), false);
            if (i != 0) {
                return i;
            }
		}        
        
        if (a.isDirectory() && b.isDirectory()) {
            return a.getName().compareToIgnoreCase(b.getName());
        }

        // Compare by disc and track numbers, if present.
        Integer trackA = getSortableDiscAndTrackNumber(a);
        Integer trackB = getSortableDiscAndTrackNumber(b);
        int i = nullSafeCompare(trackA, trackB, false);
        if (i != 0) {
            return i;
        }

        if (sortFilesByFilename){
            return a.getPath().compareToIgnoreCase(b.getPath());
        }
        else
        {
            return a.getName().compareToIgnoreCase(b.getName());
        }
        
    }

    private <T extends Comparable<T>>  int nullSafeCompare(T a, T b, boolean nullIsSmaller) {
        if (a == null && b == null) {
            return 0;
        }
        if (a == null) {
            return nullIsSmaller ? -1 : 1;
        }
        if (b == null) {
            return nullIsSmaller ? 1 : -1;
        }
        return a.compareTo(b);
    }

    private Integer getSortableDiscAndTrackNumber(MediaFile file) {
        if (file.getTrackNumber() == null) {
            return null;
        }

        int discNumber = file.getDiscNumber() == null ? 1 : file.getDiscNumber();
        return discNumber * 1000 + file.getTrackNumber();
    }
	
	    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

		public boolean isSortAlbumsByFolder() {
			return sortAlbumsByFolder;
		}

		public boolean isSortFilesByFilename() {
			return sortFilesByFilename;
		}

		public void setSortFilesByFilename(boolean sortFilesByFilename) {
			this.sortFilesByFilename = sortFilesByFilename;
		}


}

