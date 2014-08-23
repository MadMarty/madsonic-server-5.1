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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.io.Serializable;

/**
 * A music index is a mapping from an index string to a list of prefixes.  A complete index consists of a list of
 * <code>MusicIndex</code> instances.<p/>
 * <p/>
 * For a normal alphabetical index, such a mapping would typically be <em>"A" -&gt; ["A"]</em>.  The index can also be used
 * to group less frequently used letters, such as  <em>"X-&Aring;" -&gt; ["X", "Y", "Z", "&AElig;", "&Oslash;", "&Aring;"]</em>, or to make multiple
 * indexes for frequently used letters, such as <em>"SA" -&gt; ["SA"]</em> and <em>"SO" -&gt; ["SO"]</em><p/>
 * <p/>
 * Clicking on an index in the user interface will typically bring up a list of all music files that are categorized
 * under that index.
 *
 * @author Sindre Mehus
 */
public class MusicIndex implements Serializable {

    public static final MusicIndex OTHER = new MusicIndex("#");

    private final String index;
    private final List<String> prefixes = new ArrayList<String>();

    /**
     * Creates a new index with the given index string.
     *
     * @param index The index string, e.g., "A" or "The".
     */
    public MusicIndex(String index) {
        this.index = index;
    }

    /**
     * Adds a prefix to this index. Music files that starts with this prefix will be categorized under this index entry.
     *
     * @param prefix The prefix.
     */
    public void addPrefix(String prefix) {
        prefixes.add(prefix);
    }

    /**
     * Returns the index name.
     *
     * @return The index name.
     */
    public String getIndex() {
        return index;
    }

    /**
     * Returns the list of prefixes.
     *
     * @return The list of prefixes.
     */
    public List<String> getPrefixes() {
        return prefixes;
    }

    /**
     * Returns whether this object is equal to another one.
     *
     * @param o Object to compare to.
     * @return <code>true</code> if, and only if, the other object is a <code>MusicIndex</code> with the same
     *         index name as this one.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MusicIndex)) {
            return false;
        }

        final MusicIndex musicIndex = (MusicIndex) o;

        if (index != null ? !index.equals(musicIndex.index) : musicIndex.index != null) {
            return false;
        }

        return true;
    }

    /**
     * Returns a hash code for this object.
     *
     * @return A hash code for this object.
     */
    @Override
    public int hashCode() {
        return (index != null ? index.hashCode() : 0);
    }

    /**
     * An artist in an index.
     */
    public abstract static class SortableArtist implements Comparable<SortableArtist> {

        private final String name;
        private final String sortableName;


        public SortableArtist(String name, String sortableName) {
            this.name = name;
            this.sortableName = sortableName;
        }

        public String getName() {
            return name;
        }

        public String getSortableName() {
            return sortableName;
        }

        public int compareTo(SortableArtist artist) {
            return sortableName.compareToIgnoreCase(artist.sortableName);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SortableArtist artist = (SortableArtist) o;
            return sortableName.equalsIgnoreCase(artist.sortableName);
        }

        @Override
        public int hashCode() {
            return sortableName.hashCode();
        }

    }

    public static class SortableArtistWithMediaFiles extends SortableArtist {

        private final List<MediaFile> mediaFiles = new ArrayList<MediaFile>();

        public SortableArtistWithMediaFiles(String name, String sortableName) {
            super(name, sortableName);
        }

        public void addMediaFile(MediaFile mediaFile) {
            mediaFiles.add(mediaFile);
        }

        public List<MediaFile> getMediaFiles() {
            return mediaFiles;
        }
    }    

    public static class SortableArtistWithArtist extends SortableArtist {

        private final Artist artist;

        public SortableArtistWithArtist(String name, String sortableName, Artist artist) {
            super(name, sortableName);
            this.artist = artist;
        }

        public Artist getArtist() {
            return artist;
        }
    }

    public static class SortableArtistforGenre extends SortableArtist {

       private final Artist artist;
       private final String genre;
       
       private final List<MediaFile> mediaFiles = new ArrayList<MediaFile>();

       public SortableArtistforGenre(String name, String sortableName, Artist artist, String genre) {
           super(name, sortableName);
           this.artist = artist;
           this.genre = genre;
       }

       public Artist getArtist() {
           return artist;
       }

       public void addMediaFile(MediaFile mediaFile) {
           mediaFiles.add(mediaFile);
       }

       public List<MediaFile> getMediaFiles() {
           return mediaFiles;
       }

	public String getGenre() {
		return genre;
	}

   }   
   
   public static class SortableArtistWithData extends SortableArtist {

       private Artist artist = null;
   	
       private Integer albumCount = 0; 
       private Integer songCount = 0; 
       private Integer playCount = 0; 
   	
       private final List<MediaFile> mediaFiles = new ArrayList<MediaFile>();
       private final List<MediaFile> mediaFilesChildren = new ArrayList<MediaFile>();
       
       public SortableArtistWithData(String name, String sortableName) {
           super(name, sortableName);
       }

       public void addMediaFile(MediaFile mediaFile) {
           mediaFiles.add(mediaFile);
       }

       public List<MediaFile> getMediaFiles() {
           return mediaFiles;
       }
       
       public void addMediaFilesChildren(MediaFile mediaFile) {
       	mediaFilesChildren.add(mediaFile);
       }        
       
		public List<MediaFile> getMediaFilesChildren() {
			return mediaFilesChildren;
		}

		public Integer getAlbumCount() {
			return albumCount;
		}

		public void setAlbumCount(Integer albumCount) {
			this.albumCount = albumCount;
		}

		public Integer getPlayCount() {
			return playCount;
		}

		public void setPlayCount(Integer playCount) {
			this.playCount = playCount;
		}

		public Integer getSongCount() {
			return songCount;
		}

		public void setSongCount(Integer songCount) {
			this.songCount = songCount;
		}

		/**
		 * @return the artist
		 */
		public Artist getArtist() {
			return artist;
		}

		/**
		 * @param artist the artist to set
		 */
		public void setArtist(Artist artist) {
			this.artist = artist;
		}
   }        
   
   public static class SortableArtistWithAlbums extends SortableArtist {

       private Integer albumCount = 0; 
       private Integer songCount = 0; 
       private Integer playCount = 0; 
	   
	   private List<Album> albums =  new ArrayList<Album>();
       private final List<MediaFile> mediaFiles = new ArrayList<MediaFile>();
       
       public SortableArtistWithAlbums(String name, String sortableName) {
           super(name, sortableName);
       }

	public void addMediaFile(MediaFile mediaFile) {
           mediaFiles.add(mediaFile);
       }
       
       public List<MediaFile> getMediaFiles() {
           return mediaFiles;
       }

       public void addAlbum(Album album) {
    	   albums.add(album);
    	   albumCount++; 
    	   songCount=songCount+album.getSongCount();
    	   playCount=playCount+album.getPlayCount();
       }
       
       public List<Album> getAlbums() {
           return albums;
       }

	public Integer getAlbumCount() {
		return albumCount;
	}

	public void setAlbumCount(Integer albumCount) {
		this.albumCount = albumCount;
	}

	public Integer getSongCount() {
		return songCount;
	}

	public void setSongCount(Integer songCount) {
		this.songCount = songCount;
	}

	public Integer getPlayCount() {
		return playCount;
	}

	public void setPlayCount(Integer playCount) {
		this.playCount = playCount;
	}

   }        

}
