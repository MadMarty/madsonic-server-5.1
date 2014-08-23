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
package org.madsonic.service.metadata;

import org.madsonic.Logger;
import org.madsonic.domain.MediaFile;
import org.madsonic.service.SettingsService;
import org.madsonic.util.StringUtil;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v22Tag;
import org.jaudiotagger.tag.id3.ID3v23Frame;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.id3.ID3v24Frame;
import org.jaudiotagger.tag.id3.ID3v24Frames;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTXXX;
import org.jaudiotagger.tag.reference.GenreTypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.LogManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses meta data from audio files using the Jaudiotagger library
 * (http://www.jthink.net/jaudiotagger/)
 *
 * @author Sindre Mehus
 */
public class JaudiotaggerParser extends MetaDataParser {

    private static final Logger LOG = Logger.getLogger(JaudiotaggerParser.class);
    
    private static final Pattern GENRE_PATTERN = Pattern.compile("\\((\\d+)\\).*");
    private static final Pattern TRACK_NUMBER_PATTERN = Pattern.compile("(\\d+)/\\d+");
    private static final Pattern YEAR_NUMBER_PATTERN = Pattern.compile("(\\d{4}).*");

    private static final HashMap<String, String> hmap = new HashMap<String, String>();
    
	private static final File MADSONIC_HOME_WINDOWS = new File(SettingsService.getMadsonicHome().toString());
    
    public void init() {
    	readConfig();
    }
	
    static {
        try {
            LogManager.getLogManager().reset();
        } catch (Throwable x) {
            LOG.warn("Failed to turn off logging from Jaudiotagger.", x);
        }
    }

    /**
     * Parses meta data for the given music file. No guessing or reformatting is done.
     *
     *
     * @param file The music file to parse.
     * @return Meta data for the file.
     */
    @Override
    public MetaData getRawMetaData(File file) {

        MetaData metaData = new MetaData();
        

        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();
            if (tag != null) {
                metaData.setAlbumName(getTagField(tag, FieldKey.ALBUM));
                metaData.setTitle(getTagField(tag, FieldKey.TITLE));
				metaData.setLyrics(getTagField(tag, FieldKey.LYRICS));
                
				if (metaData.getLyrics() != null) {
					metaData.setHasLyrics(true);
				}
				
				int getYear = 0;
				if (parseInteger(dateToYear(getTagField(tag, FieldKey.YEAR))) != null ){
					getYear = parseInteger(dateToYear(getTagField(tag, FieldKey.YEAR)));
				}
				if (getYear > 1000) {
				   metaData.setYear(getYear);
				}
				
                String moodsAll = getTagField(tag, FieldKey.MOOD);
                
                if (moodsAll != null && moodsAll.contains(";")) {
	                List<String> resultMoods = new LinkedList<String>(Arrays.asList(StringUtil.splitMoods(moodsAll,";")));
	                metaData.setMood(resultMoods.get(0));
                } else if (moodsAll != null && moodsAll.contains(",")){
	                List<String> resultMoods = new LinkedList<String>(Arrays.asList(StringUtil.splitMoods(moodsAll,",")));
	                metaData.setMood(resultMoods.get(0));
                } else if (moodsAll != null && moodsAll.contains("//")){
	                List<String> resultMoods = new LinkedList<String>(Arrays.asList(StringUtil.splitMoods(moodsAll,"//")));
	                metaData.setMood(resultMoods.get(0));
                } else if (moodsAll != null && moodsAll.contains("|")){
	                List<String> resultMoods = new LinkedList<String>(Arrays.asList(StringUtil.splitMoods(moodsAll,"|")));
	                metaData.setMood(resultMoods.get(0));
                } else {
	                metaData.setMood(moodsAll);
                }
                
                String genreAll = getTagField(tag, FieldKey.GENRE);

                if (genreAll != null && genreAll.contains(";")) {
	                List<String> resultGenre = new LinkedList<String>(Arrays.asList(StringUtil.splitMoods(genreAll,";")));
	                metaData.setGenre(translateGenre(mapGenre(resultGenre.get(0))));
                } else if (genreAll != null && genreAll.contains(",")){
	                List<String> resultGenre = new LinkedList<String>(Arrays.asList(StringUtil.splitMoods(genreAll,",")));
	                metaData.setGenre(translateGenre(mapGenre(resultGenre.get(0))));
                } else if (genreAll != null && genreAll.contains("//")){
	                List<String> resultGenre = new LinkedList<String>(Arrays.asList(StringUtil.splitMoods(genreAll,"//")));
	                metaData.setGenre(translateGenre(mapGenre(resultGenre.get(0))));
                } else if (genreAll != null && genreAll.contains("|")){
	                List<String> resultGenre = new LinkedList<String>(Arrays.asList(StringUtil.splitMoods(genreAll,"|")));
	                metaData.setGenre(translateGenre(mapGenre(resultGenre.get(0))));
                } else {
                	metaData.setGenre(translateGenre(mapGenre(genreAll)));
                }
                
                metaData.setDiscNumber(parseInteger(getTagField(tag, FieldKey.DISC_NO)));
                metaData.setTrackNumber(parseTrackNumber(getTagField(tag, FieldKey.TRACK)));

                String songArtist = getTagField(tag, FieldKey.ARTIST);
                String albumArtist = getTagField(tag, FieldKey.ALBUM_ARTIST);
                metaData.setArtist(StringUtils.isBlank(songArtist) ? albumArtist : songArtist);
                metaData.setAlbumArtist(StringUtils.isBlank(albumArtist) ? songArtist : albumArtist);
            }

            AudioHeader audioHeader = audioFile.getAudioHeader();
            if (audioHeader != null) {
                metaData.setVariableBitRate(audioHeader.isVariableBitRate());
                metaData.setBitRate((int) audioHeader.getBitRateAsNumber());
                metaData.setDurationSeconds(audioHeader.getTrackLength());
            }


        } catch (Throwable x) {
            LOG.warn("Error when parsing tags in " + file + " " + x.getMessage(), x);
        }

        return metaData;
    }

    
    public String getCustomTag(AudioFile audioFile, String text){
    //	FrameBodyTXXX txxxBody = 
		return text;
    }
    
    public boolean setCustomTag(AudioFile audioFile, String description, String text){
    	
        FrameBodyTXXX txxxBody = new FrameBodyTXXX();
        txxxBody.setDescription(description);
        txxxBody.setText(text);

        // Get the tag from the audio file
        // If there is no ID3Tag create an ID3v2.3 tag
        Tag tag = audioFile.getTagOrCreateAndSetDefault();
        // If there is only a ID3v1 tag, copy data into new ID3v2.3 tag
        if(!(tag instanceof ID3v23Tag || tag instanceof ID3v24Tag)){
            Tag newTagV23 = null;
            if(tag instanceof ID3v1Tag){
                newTagV23 = new ID3v23Tag((ID3v1Tag)audioFile.getTag()); // Copy old tag data               
            }
            if(tag instanceof ID3v22Tag){
                newTagV23 = new ID3v23Tag((ID3v1Tag)audioFile.getTag()); // Copy old tag data              
            }           
            audioFile.setTag(newTagV23);
        }

        AbstractID3v2Frame frame = null;
        if(tag instanceof ID3v23Tag){
            frame = new ID3v23Frame("TXXX");
        }
        else if(tag instanceof ID3v24Tag){
            frame = new ID3v24Frame("TXXX");
        }

        frame.setBody(txxxBody);

        try {
            tag.addField(frame);
        } catch (FieldDataInvalidException e) {
            e.printStackTrace();
            return false;
        }

        try {
            audioFile.commit();
        } catch (CannotWriteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }    
    
    private String getTagField(Tag tag, FieldKey fieldKey) {
        try {
            return StringUtils.trimToNull(tag.getFirst(fieldKey));
        } catch (Exception x) {
            // Ignored.
            return null;
        }
    }

    /**
     * Returns all tags supported by id3v1.
     */
    public static SortedSet<String> getID3V1Genres() {
        return new TreeSet<String>(GenreTypes.getInstanceOf().getAlphabeticalValueList());
    }

    /**
     * Sometimes the genre is returned as "(17)" or "(17)Rock", instead of "Rock".  This method
     * maps the genre ID to the corresponding text.
     */
    private String mapGenre(String genre) {
        if (genre == null) {
            return null;
        }
        Matcher matcher = GENRE_PATTERN.matcher(genre);
        if (matcher.matches()) {
            int genreId = Integer.parseInt(matcher.group(1));
            if (genreId >= 0 && genreId < GenreTypes.getInstanceOf().getSize()) {
                return GenreTypes.getInstanceOf().getValueForId(genreId);
            }
        }
        return genre;
    }

    
    private void readConfig(){
    	
    	File f = new File(MADSONIC_HOME_WINDOWS + "/config/genremap.cfg");
    	
    	if(f.exists()){
    		LOG.debug("## try loading GenreMerge template");
    		try (BufferedReader br = new BufferedReader(new FileReader(f)))
    		{
    			String sCurrentLine;
    			while ( (sCurrentLine = br.readLine()) != null) {
    				if (!sCurrentLine.startsWith("#")) {
    					String[] line = sCurrentLine.split(",");
    					hmap.put(line[0].trim(), line[1].trim());
//    					System.out.println(line[0].trim() + line[1].trim());
    				}
    			}
    			LOG.debug("## GenreMerge Entries found: " + hmap.size());
    		} 
    		catch (IOException e) { 
    			LOG.error("## Cant read GenreMap Template file");
    			e.printStackTrace();
    		} 
    	}
    }

    
    private String translateGenre(String genre){
    	if (hmap.size() < 1){
    		readConfig();
    	}
    	
        /* Get values based on key*/
        String var = hmap.get(genre);
        if (var != null) {
            LOG.debug("## replaced Genre: " + genre + " -> " + var);
            return WordUtils.capitalize(var);
        }
    	return WordUtils.capitalize(genre);
    }
    
    
    /**
     * Parses the track number from the given string.  Also supports
     * track numbers on the form "4/12".
     */
    private Integer parseTrackNumber(String trackNumber) {
        if (trackNumber == null) {
            return null;
        }

        Integer result = null;

        try {
            result = new Integer(trackNumber);
        } catch (NumberFormatException x) {
            Matcher matcher = TRACK_NUMBER_PATTERN.matcher(trackNumber);
            if (matcher.matches()) {
                try {
                    result = Integer.valueOf(matcher.group(1));
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }

        if (Integer.valueOf(0).equals(result)) {
            return null;
        }
        return result;
    }

    @Deprecated
    private Integer parseYear(String year) {
        if (year == null) {
            return null;
        }

        Integer result = null;

        try {
            result = new Integer(year);
        } catch (NumberFormatException x) {
            Matcher matcher = YEAR_NUMBER_PATTERN.matcher(year);
            if (matcher.matches()) {
                try {
                    result = Integer.valueOf(matcher.group(1));
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }

        if (Integer.valueOf(0).equals(result)) {
            return null;
        }
        return result;
    }
    
    /**
     * Try and retrieve a valid year from a date tag, since most taggers put full release date
     * Will only try to grab a different date if the length of the string is > 4
     */
    private String dateToYear(String date) {
        if (date == null) {
        	return "-1";}

        	String yr = date;
            if (date.length() > 4) {
                    try {
                            if (date.matches("^\\d{4}.*")) {
                                    int i = Integer.parseInt(date.substring(0,4));
                                    if (isValidYear(i)) {
                                            yr = date.substring(0,4);
                                    }
                            }
                            if (date.matches(".*\\d{4}$") && yr.equals(date)){
                                    int i = Integer.parseInt(date.substring(date.length()-4));
                                    if (isValidYear(i)) {
                                            yr = date.substring(date.length()-4);
                                    }
                            }
                    }
                    catch (NumberFormatException nfe) {
//                          Ignore NFEs
//                          nfe.printStackTrace();
                    }
                    catch (NullPointerException npe) {
//                      Ignore NPEs
//                      npe.printStackTrace();
                }
            }
            return yr;
    }

    /**
     * Return true if integer appears to be a valid year
     */
    private boolean isValidYear(Integer i) {
            boolean val = false;
            if (i>1000 && i<2100) {
                    val = true;
            }
            return val;
    }
    
    

    private Integer parseInteger(String s) {
        s = StringUtils.trimToNull(s);
        if (s == null) {
            return null;
        }
        try {
            Integer result = Integer.valueOf(s);
            if (Integer.valueOf(0).equals(result)) {
                return null;
            }
            return result;
        } catch (NumberFormatException x) {
            return null;
        }
    }

    /**
     * Updates the given file with the given meta data.
     *
     * @param file     The music file to update.
     * @param metaData The new meta data.
     */
    @Override
    public void setMetaData(MediaFile file, MetaData metaData) {

        try {
            AudioFile audioFile = AudioFileIO.read(file.getFile());
            Tag tag = audioFile.getTagOrCreateAndSetDefault();

            tag.setField(FieldKey.ARTIST, StringUtils.trimToEmpty(metaData.getArtist()));
            tag.setField(FieldKey.MOOD, StringUtils.trimToEmpty(metaData.getMood()));
            tag.setField(FieldKey.ALBUM, StringUtils.trimToEmpty(metaData.getAlbumName()));
            tag.setField(FieldKey.TITLE, StringUtils.trimToEmpty(metaData.getTitle()));
            tag.setField(FieldKey.GENRE, StringUtils.trimToEmpty(metaData.getGenre()));
            try {
                tag.setField(FieldKey.ALBUM_ARTIST, StringUtils.trimToEmpty(metaData.getAlbumArtist()));
            } catch (Exception x) {
                // Silently ignored. ID3v1 doesn't support album artist.
            }

            Integer track = metaData.getTrackNumber();
            if (track == null) {
                tag.deleteField(FieldKey.TRACK);
            } else {
                tag.setField(FieldKey.TRACK, String.valueOf(track));
            }

            if (metaData.getYear() == null) {
                tag.deleteField(FieldKey.YEAR);
            } else {
                tag.setField(FieldKey.YEAR, String.valueOf(metaData.getYear()));
            }			
			
            audioFile.commit();

        } catch (Throwable x) {
            LOG.warn("Failed to update tags for file " + file, x);
            throw new RuntimeException("Failed to update tags for file " + file + ". " + x.getMessage(), x);
        }
    }

    /**
     * Returns whether this parser supports tag editing (using the {@link #setMetaData} method).
     *
     * @return Always true.
     */
    @Override
    public boolean isEditingSupported() {
        return true;
    }

    /**
     * Returns whether this parser is applicable to the given file.
     *
     * @param file The music file in question.
     * @return Whether this parser is applicable to the given file.
     */
    @Override
    public boolean isApplicable(File file) {
        if (!file.isFile()) {
            return false;
        }

        String format = FilenameUtils.getExtension(file.getName()).toLowerCase();

        return format.equals("mp3") ||
                format.equals("m4a") ||
                format.equals("aac") ||
                format.equals("ogg") ||
                format.equals("oga") ||                
                format.equals("flac") ||
                format.equals("wav") ||
                format.equals("mpc") ||
                format.equals("mp+") ||
                format.equals("ape") ||
                format.equals("wma");
    }

    /**
     * Returns whether cover art image data is available in the given file.
     *
     * @param file The music file.
     * @return Whether cover art image data is available.
     */
    public boolean isImageAvailable(MediaFile file) {
        try {
            return getArtwork(file) != null;
        } catch (Throwable x) {
            LOG.warn("Failed to find cover art tag in " + file, x);
            return false;
        }
    }

    /**
     * Returns the cover art image data embedded in the given file.
     *
     * @param file The music file.
     * @return The embedded cover art image data, or <code>null</code> if not available.
     */
    public byte[] getImageData(MediaFile file) {
        try {
            return getArtwork(file).getBinaryData();
        } catch (Throwable x) {
            LOG.warn("Failed to find cover art tag in " + file, x);
            return null;
        }
    }

    private Artwork getArtwork(MediaFile file) throws Exception {
        AudioFile audioFile = AudioFileIO.read(file.getFile());
        Tag tag = audioFile.getTag();
        return tag == null ? null : tag.getFirstArtwork();
    }
}