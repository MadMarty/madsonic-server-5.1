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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import org.madsonic.Logger;
import org.madsonic.domain.MediaFile;
import org.madsonic.service.MediaFileService;
import org.madsonic.service.metadata.MetaData;
import org.madsonic.service.metadata.MetaDataParser;
import org.madsonic.service.metadata.MetaDataParserFactory;

/**
 * Provides AJAX-enabled services for editing tags in music files.
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class TagService {

    private static final Logger LOG = Logger.getLogger(TagService.class);

    private MetaDataParserFactory metaDataParserFactory;
    private MediaFileService mediaFileService;

    /**
     * Updated tags for a given music file.
     *
     * @param id           The ID of the music file.
     * @param track        The track number.
     * @param artist       The artist name.
     * @param albumArtist  The albumartist name.
     * @param album        The album name.
     * @param title        The song title.
     * @param year         The release year.
     * @param mood         The musical mood.
     * @param genre        The musical genre.
     * @param rank         The track rank.
     * @return "UPDATED"   if the new tags were updated, "SKIPPED" if no update was necessary.
     *                     Otherwise the error message is returned.
     */
    public String setTags(int id, String track, String artist, String albumArtist, String album, String title, String year, String mood, String genre, int rank) {

        track = StringUtils.trimToNull(track);
        artist = StringUtils.trimToNull(artist);
        albumArtist = StringUtils.trimToNull(albumArtist);
        album = StringUtils.trimToNull(album);
        title = StringUtils.trimToNull(title);
        year = StringUtils.trimToNull(year);
        mood = StringUtils.trimToNull(mood);
        genre = StringUtils.trimToNull(genre);
        
        Integer trackNumber = null;
        if (track != null) {
            try {
                trackNumber = new Integer(track);
            } catch (NumberFormatException x) {
                LOG.warn("Illegal track number: " + track, x);
            }
        }

        Integer yearNumber = null;
        if (year != null) {
            try {
                yearNumber = new Integer(year);
            } catch (NumberFormatException x) {
                LOG.warn("Illegal year: " + year, x);
            }
        }

        try {

            MediaFile file = mediaFileService.getMediaFile(id);
            MetaDataParser parser = metaDataParserFactory.getParser(file.getFile());

            if (!parser.isEditingSupported()) {
                return "Tag editing of " + FilenameUtils.getExtension(file.getPath()) + " files is not supported.";
            }

            if (StringUtils.equals(artist, file.getArtist()) &&
                    StringUtils.equals(albumArtist, file.getAlbumArtist()) &&
                    StringUtils.equals(album, file.getAlbumName()) &&
                    StringUtils.equals(title, file.getTitle()) &&
                    ObjectUtils.equals(yearNumber, file.getYear()) &&
                    StringUtils.equals(mood, file.getMood()) &&                    
                    StringUtils.equals(genre, file.getGenre()) &&
                    ObjectUtils.equals(trackNumber, file.getTrackNumber()) && 
                    rank == file.getRank() ) {
                return "SKIPPED";
            }

            MetaData newMetaData = new MetaData();
            newMetaData.setArtist(artist);
            newMetaData.setAlbumArtist(albumArtist);
            newMetaData.setAlbumName(album);
            newMetaData.setTitle(title);
            newMetaData.setYear(yearNumber);
            newMetaData.setMood(mood);
            newMetaData.setGenre(genre);
            newMetaData.setTrackNumber(trackNumber);
            parser.setMetaData(file, newMetaData);

        	file.setRank(rank);
            
            mediaFileService.refreshMediaFile(file);
            mediaFileService.refreshMediaFile(mediaFileService.getParentOf(file));
            
//            if (rank != file.getRank()) {
//            	file.setRank(rank);
//                mediaFileService.refreshMediaFileRank(file);            
//            }
            
            return "UPDATED";

        } catch (Exception x) {
            LOG.warn("Failed to update tags for " + id, x);
            return x.getMessage();
        }
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setMetaDataParserFactory(MetaDataParserFactory metaDataParserFactory) {
        this.metaDataParserFactory = metaDataParserFactory;
    }
}
