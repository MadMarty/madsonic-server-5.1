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
package org.madsonic.ajax;

import java.util.List;

import org.madsonic.domain.MediaFile;
import org.madsonic.domain.Playlist;

/**
 * The playlist of a player.
 *
 * @author Sindre Mehus
 */
public class PlaylistInfo {

    private final Playlist playlist;
    private final List<Entry> entries;

    public PlaylistInfo(Playlist playlist, List<Entry> entries) {
        this.playlist = playlist;
        this.entries = entries;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public static class Entry {
        private final int id;
        private final int rank;
        private final String title;
        private final String artist;
        private final String album;
        private final String durationAsString;
        private final boolean starred;

        public Entry(int id, int rank, String title, String artist, String album, String durationAsString, boolean starred) {
            this.id = id;
            this.rank = rank;
            this.title = title;
            this.artist = artist;
            this.album = album;
            this.durationAsString = durationAsString;
            this.starred = starred;
        }

        public int getId() {
            return id;
        }

        public int getRank() {
            return rank;
        }

        public String getTitle() {
            return title;
        }

        public String getArtist() {
            return artist;
        }

        public String getAlbum() {
            return album;
        }
        
        public String getDurationAsString() {
            return durationAsString;
        }

        public boolean isStarred() {
            return starred;
        }
    }
}
