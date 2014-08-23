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

import java.io.IOException;
import java.io.StringReader;
import java.net.SocketException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import org.madsonic.Logger;
import org.madsonic.domain.MediaFile;
import org.madsonic.service.MediaFileService;
import org.madsonic.service.metadata.MetaData;
import org.madsonic.service.metadata.MetaDataParser;
import org.madsonic.service.metadata.MetaDataParserFactory;
import org.madsonic.util.StringUtil;

/**
 * Provides AJAX-enabled services for retrieving song lyrics from chartlyrics.com.
 * <p/>
 * See http://www.chartlyrics.com/api.aspx for details.
 * <p/>
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class LyricsService {

    private static final Logger LOG = Logger.getLogger(LyricsService.class);

    private MediaFileService mediaFileService;
    private MetaDataParserFactory metaDataParserFactory;	
    
    /**
     * Returns lyrics for the given song and artist.
     *
     * @param artist The artist.
     * @param song   The song.
     * @return The lyrics, never <code>null</code> .
     */
    public LyricsInfo getLyrics(String artist, String song) {
    	LyricsInfo lyrics = new LyricsInfo();
        try {

            artist = StringUtil.urlEncode(artist);
            song = StringUtil.urlEncode(song);

            String url = "http://api.chartlyrics.com/apiv1.asmx/SearchLyricDirect?artist=" + artist + "&song=" + song;
            String xml = executeGetRequest(url);
            lyrics = parseSearchResult(xml);

        } catch (SocketException x) {
            lyrics.setTryLater(true);
        } catch (Exception x) {
            LOG.warn("Failed to get lyrics for song '" + song + "'.", x);
        }
        return lyrics;
    }

    public LyricsInfo getLyrics(String id, String artistname, String songname) throws SocketException {
    	LyricsInfo lyrics = new LyricsInfo();
    	String lyric = null;
        try {
            int mediaFileId = NumberUtils.toInt(id);
            if (mediaFileId > 0) {
    	        MediaFile mediaFile = mediaFileService.getMediaFile(mediaFileId);
    	        MetaData metaData = null;
    	        MetaDataParser parser = metaDataParserFactory.getParser(mediaFile.getFile());
    	        if (parser != null) {
    	            metaData = parser.getMetaData(mediaFile.getFile());
    	            lyric = metaData.getLyrics();
    	        }
    	        if (lyric == null){
    	            String artist = StringUtil.urlEncode(artistname);
    	            String song = StringUtil.urlEncode(songname);

    	            String url = "http://api.chartlyrics.com/apiv1.asmx/SearchLyricDirect?artist=" + artist + "&song=" + song;
    	            String xml = executeGetRequest(url);
    	            lyrics = parseSearchResult(xml);
    	            return lyrics;
    	        }
            }
        } catch (Exception x) {
            LOG.warn("Failed to get lyrics for id '" + id + "'.", x);
        }
		return new LyricsInfo(lyric, artistname, songname);
    }    
    
    private LyricsInfo parseSearchResult(String xml) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(new StringReader(xml));

        Element root = document.getRootElement();
        Namespace ns = root.getNamespace();

        String lyric = StringUtils.trimToNull(root.getChildText("Lyric", ns));
        String song =  root.getChildText("LyricSong", ns);
        String artist =  root.getChildText("LyricArtist", ns);

        return new LyricsInfo(lyric, artist, song);
    }

    private String executeGetRequest(String url) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
        HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
        HttpGet method = new HttpGet(url);
        try {

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            return client.execute(method, responseHandler);

        } finally {
            client.getConnectionManager().shutdown();
        }
    }
    
    public void setMetaDataParserFactory(MetaDataParserFactory metaDataParserFactory) {
        this.metaDataParserFactory = metaDataParserFactory;
    }
    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }  
    
}
