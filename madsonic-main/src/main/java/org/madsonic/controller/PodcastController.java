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
package org.madsonic.controller;

import org.madsonic.domain.MediaFile;
import org.madsonic.domain.Playlist;
import org.madsonic.service.PlaylistService;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;
import org.madsonic.util.StringUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Controller for the page used to generate the Podcast XML file.
 *
 * @author Sindre Mehus
 */
public class PodcastController extends ParameterizableViewController {

    private static final DateFormat RSS_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
    private PlaylistService playlistService;
    private SettingsService settingsService;
    private SecurityService securityService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String url = request.getRequestURL().toString();
        String username = securityService.getCurrentUsername(request);
        List<Playlist> playlists = playlistService.getReadablePlaylistsForUser(username, "name asc");
        List<Podcast> podcasts = new ArrayList<Podcast>();

        for (Playlist playlist : playlists) {

            List<MediaFile> songs = playlistService.getFilesInPlaylist(playlist.getId());
            if (songs.isEmpty()) {
                continue;
            }
            long length = 0L;
            for (MediaFile song : songs) {
                length += song.getFileSize();
            }
            String publishDate = RSS_DATE_FORMAT.format(playlist.getCreated());

            // Resolve content type.
            String suffix = songs.get(0).getFormat();
            String type = StringUtil.getMimeType(suffix);

            String enclosureUrl = url.replaceFirst("/podcast.*", "/stream?playlist=" + playlist.getId() + "&amp;suffix=." + suffix);

            // Rewrite URLs in case we're behind a proxy.
            if (settingsService.isRewriteUrlEnabled()) {
                String referer = request.getHeader("referer");
                url = StringUtil.rewriteUrl(url, referer);
            }

            // Change protocol and port, if specified. (To make it work with players that don't support SSL.)
            int streamPort = settingsService.getStreamPort();
            if (streamPort != 0) {
                enclosureUrl = StringUtil.toHttpUrl(enclosureUrl, streamPort);
            }

            podcasts.add(new Podcast(playlist.getName(), publishDate, enclosureUrl, length, type));
        }

        Map<String, Object> map = new HashMap<String, Object>();

        ModelAndView result = super.handleRequestInternal(request, response);
        map.put("url", url);
        map.put("podcasts", podcasts);

        result.addObject("model", map);
        return result;
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    /**
     * Contains information about a single Podcast.
     */
    public static class Podcast {
        private String name;
        private String publishDate;
        private String enclosureUrl;
        private long length;
        private String type;

        public Podcast(String name, String publishDate, String enclosureUrl, long length, String type) {
            this.name = name;
            this.publishDate = publishDate;
            this.enclosureUrl = enclosureUrl;
            this.length = length;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getPublishDate() {
            return publishDate;
        }

        public String getEnclosureUrl() {
            return enclosureUrl;
        }

        public long getLength() {
            return length;
        }

        public String getType() {
            return type;
        }
    }
}