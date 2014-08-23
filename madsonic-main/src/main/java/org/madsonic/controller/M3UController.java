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

import org.madsonic.Logger;
import org.madsonic.domain.MediaFile;
import org.madsonic.domain.Player;
import org.madsonic.domain.PlayQueue;
import org.madsonic.service.PlayerService;
import org.madsonic.service.SettingsService;
import org.madsonic.service.TranscodingService;
import org.madsonic.util.StringUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Controller which produces the M3U playlist.
 *
 * @author Sindre Mehus
 */
public class M3UController implements Controller {

    private PlayerService playerService;
    private SettingsService settingsService;
    private TranscodingService transcodingService;

    private static final Logger LOG = Logger.getLogger(M3UController.class);

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("audio/x-mpegurl");
        response.setCharacterEncoding(StringUtil.ENCODING_UTF8);

        Player player = playerService.getPlayer(request, response);

        String url = request.getRequestURL().toString();
        url = url.replaceFirst("play.m3u.*", "stream?");

        // Rewrite URLs in case we're behind a proxy.
        if (settingsService.isRewriteUrlEnabled()) {
            String referer = request.getHeader("referer");
            url = StringUtil.rewriteUrl(url, referer);
        }

        // Change protocol and port, if specified. (To make it work with players that don't support SSL.)
        int streamPort = settingsService.getStreamPort();
        if (streamPort != 0) {
            url = StringUtil.toHttpUrl(url, streamPort);
            LOG.info("Using non-SSL port " + streamPort + " in m3u playlist.");
        }

        if (player.isExternalWithPlaylist()) {
            createClientSidePlaylist(response.getWriter(), player, url);
        } else {
            createServerSidePlaylist(response.getWriter(), player, url);
        }
        return null;
    }

    private void createClientSidePlaylist(PrintWriter out, Player player, String url) throws Exception {
        out.println("#EXTM3U");
        List<MediaFile> result;
        synchronized (player.getPlayQueue()) {
            result = player.getPlayQueue().getFiles();
        }
        for (MediaFile mediaFile : result) {
            Integer duration = mediaFile.getDurationSeconds();
            if (duration == null) {
                duration = -1;
            }
            out.println("#EXTINF:" + duration + "," + mediaFile.getArtist() + " - " + mediaFile.getTitle());
            out.println(url + "player=" + player.getId() + "&id=" +mediaFile.getId() + "&suffix=." + transcodingService.getSuffix(player, mediaFile, null));
        }
    }

    private void createServerSidePlaylist(PrintWriter out, Player player, String url) throws IOException {

        url += "player=" + player.getId();

        // Get suffix of current file, e.g., ".mp3".
        String suffix = getSuffix(player);
        if (suffix != null) {
            url += "&suffix=." + suffix;
        }

        out.println("#EXTM3U");
        out.println("#EXTINF:-1,Madsonic");
        out.println(url);
    }

    private String getSuffix(Player player) {
        PlayQueue playQueue = player.getPlayQueue();
        return playQueue.isEmpty() ? null : transcodingService.getSuffix(player, playQueue.getFile(0), null);
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }
}
