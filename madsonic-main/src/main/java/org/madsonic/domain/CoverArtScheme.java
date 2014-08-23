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
 * Enumeration of cover art schemes. Each value contains a size, which indicates how big the
 * scaled covert art images should be.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.3 $ $Date: 2005/06/15 18:10:40 $
 */
public enum CoverArtScheme {

    OFF    (  0,  0),
    MINI   ( 80,  0),
    SMALL  (100, 14),
    MEDIUM (118, 19),
    LARGE  (160, 25),
    XLARGE (200, 35);
    
    private int size;
    private final int captionLength;

    CoverArtScheme(int size, int captionLength) {
    	
        this.size = size;
        this.captionLength = captionLength;
    }

    /**
     * Returns the covert art size for this scheme.
     *
     * @return the covert art size for this scheme.
     */
    public int getSize() {
        return size;
    }

    public int getCaptionLength() {
        return captionLength;
    }
}
