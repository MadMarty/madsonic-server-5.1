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

import java.awt.Dimension;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.madsonic.domain.MediaFile;
import org.madsonic.service.MediaFileService;
import org.madsonic.service.SearchService;

import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import org.madsonic.Logger;
import org.madsonic.domain.Player;
import org.madsonic.domain.PlayQueue;
import org.madsonic.domain.TransferStatus;
import org.madsonic.domain.User;
import org.madsonic.domain.VideoTranscodingSettings;
import org.madsonic.io.PlayQueueInputStream;
import org.madsonic.io.RangeOutputStream;
import org.madsonic.io.ShoutCastOutputStream;
import org.madsonic.service.AudioScrobblerService;
import org.madsonic.service.PlayerService;
import org.madsonic.service.PlaylistService;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;
import org.madsonic.service.StatusService;
import org.madsonic.service.TranscodingService;
import org.madsonic.util.HttpRange;
import org.madsonic.util.StringUtil;
import org.madsonic.util.Util;

/**
 * A controller which streams the content of a {@link org.madsonic.domain.PlayQueue} to a remote
 * {@link Player}.
 *
 * @author Sindre Mehus
 */
public class StreamController implements Controller {

    private static final Logger LOG = Logger.getLogger(StreamController.class);

    private StatusService statusService;
    private PlayerService playerService;
    private PlaylistService playlistService;
    private SecurityService securityService;
    private SettingsService settingsService;
    private TranscodingService transcodingService;
    private AudioScrobblerService audioScrobblerService;
    private MediaFileService mediaFileService;
    private SearchService searchService;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        TransferStatus status = null;
        PlayQueueInputStream in = null;
        Player player = playerService.getPlayer(request, response, false, true);
        User user = securityService.getUserByName(player.getUsername());

        try {

            if (!user.isStreamRole()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Streaming is forbidden for user " + user.getUsername());
                return null;
            }

            // If "playlist" request parameter is set, this is a Podcast request. In that case, create a separate
            // play queue (in order to support multiple parallel Podcast streams).
            Integer playlistId = ServletRequestUtils.getIntParameter(request, "playlist");
            boolean isPodcast = playlistId != null;
            if (isPodcast) {
                PlayQueue playQueue = new PlayQueue();
                playQueue.addFiles(false, playlistService.getFilesInPlaylist(playlistId));
                player.setPlayQueue(playQueue);
                Util.setContentLength(response, playQueue.length());
                LOG.info("Incoming Podcast request for playlist " + playlistId);
            }

            response.addHeader("Access-Control-Allow-Origin", "*");

            String contentType = StringUtil.getMimeType(request.getParameter("suffix"));
            
//            if (player.isWeb()) {
//            	contentType = StringUtil.getMimeType("mp3");
//            }

            response.setContentType(contentType);

            String preferredTargetFormat = request.getParameter("format");
            Integer maxBitRate = ServletRequestUtils.getIntParameter(request, "maxBitRate");
            if (Integer.valueOf(0).equals(maxBitRate)) {
                maxBitRate = null;
            }

            VideoTranscodingSettings videoTranscodingSettings = null;

            // Is this a request for a single file (typically from the embedded Flash player)?
            // In that case, create a separate playlist (in order to support multiple parallel streams).
            // Also, enable partial download (HTTP byte range).
            MediaFile file = getSingleFile(request);
            boolean isSingleFile = file != null;
            HttpRange range = null;

            if (isSingleFile) {
                PlayQueue playQueue = new PlayQueue();
                playQueue.addFiles(true, file);
                player.setPlayQueue(playQueue);

                if (!file.isVideo()) {
                    response.setIntHeader("ETag", file.getId());
                    response.setHeader("Accept-Ranges", "bytes");
                }

                TranscodingService.Parameters parameters = transcodingService.getParameters(file, player, maxBitRate, preferredTargetFormat, null, false);
                long fileLength = getFileLength(parameters);
                boolean isConversion = parameters.isDownsample() || parameters.isTranscode();
                boolean estimateContentLength = ServletRequestUtils.getBooleanParameter(request, "estimateContentLength", false);
                boolean isHls = ServletRequestUtils.getBooleanParameter(request, "hls", false);

                range = getRange(request, file);
                if (range != null) {
                    LOG.info("Got HTTP range: " + range);
                    response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                    Util.setContentLength(response, range.isClosed() ? range.size() : fileLength - range.getFirstBytePos());
                    long lastBytePos = range.getLastBytePos() != null ? range.getLastBytePos() : fileLength - 1;
                    response.setHeader("Content-Range", "bytes " + range.getFirstBytePos() + "-" + lastBytePos + "/" + fileLength);
                } else if (!isHls && (!isConversion || estimateContentLength)) {
                    Util.setContentLength(response, fileLength);
                }

                if (isHls) {
                    response.setContentType(StringUtil.getMimeType("ts")); // HLS is always MPEG TS.
                } else {
                	
                    String transcodedSuffix = transcodingService.getSuffix(player, file, preferredTargetFormat);
                    
//                    if (player.isWeb()) {
//                    	transcodedSuffix = "mp3";
//                    }
                    response.setContentType(StringUtil.getMimeType(transcodedSuffix));
                    setContentDuration(response, file);
                }

                if (file.isVideo() || isHls) {
                    videoTranscodingSettings = createVideoTranscodingSettings(file, request);
                }
            }

            if (request.getMethod().equals("HEAD")) {
                return null;
            }

            // Terminate any other streams to this player.
            if (!isPodcast && !isSingleFile) {
                for (TransferStatus streamStatus : statusService.getStreamStatusesForPlayer(player)) {
                    if (streamStatus.isActive()) {
                        streamStatus.terminate();
                    }
                }
            }

            status = statusService.createStreamStatus(player);

            in = new PlayQueueInputStream(player, status, maxBitRate, preferredTargetFormat, videoTranscodingSettings, transcodingService, audioScrobblerService, mediaFileService, searchService);
            OutputStream out = RangeOutputStream.wrap(response.getOutputStream(), range);

            // Enabled SHOUTcast, if requested.
            boolean isShoutCastRequested = "1".equals(request.getHeader("icy-metadata"));
            if (isShoutCastRequested && !isSingleFile) {
                response.setHeader("icy-metaint", "" + ShoutCastOutputStream.META_DATA_INTERVAL);
                response.setHeader("icy-notice1", "This stream is served using Madsonic");
                response.setHeader("icy-notice2", "Madsonic - Free media streamer - madsonic.org");
                response.setHeader("icy-name", "Madsonic");
                response.setHeader("icy-genre", "Mixed");
                response.setHeader("icy-url", "http://madsonic.org/");
                out = new ShoutCastOutputStream(out, player.getPlayQueue(), settingsService);
            }

            final int BUFFER_SIZE = 2048;
            byte[] buf = new byte[BUFFER_SIZE];

            while (true) {

                // Check if stream has been terminated.
                if (status.terminated()) {
                    return null;
                }

                if (player.getPlayQueue().getStatus() == PlayQueue.Status.STOPPED) {
                    if (isPodcast || isSingleFile) {
                        break;
                    } else {
                        sendDummy(buf, out);
                    }
                } else {

                    int n = in.read(buf);
                    if (n == -1) {
                        if (isPodcast || isSingleFile) {
                            break;
                        } else {
                            sendDummy(buf, out);
                        }
                    } else {
                        out.write(buf, 0, n);
                    }
                }
            }

        } finally {
            if (status != null) {
                securityService.updateUserByteCounts(user, status.getBytesTransfered(), 0L, 0L);
                statusService.removeStreamStatus(status);
            }
            IOUtils.closeQuietly(in);
        }
        return null;
    }

    private void setContentDuration(HttpServletResponse response, MediaFile file) {
        if (file.getDurationSeconds() != null) {
            response.setHeader("X-Content-Duration", String.format("%.1f", file.getDurationSeconds().doubleValue()));
        }
    }
    
    private MediaFile getSingleFile(HttpServletRequest request) throws ServletRequestBindingException {
        String path = request.getParameter("path");
        if (path != null) {
            return mediaFileService.getMediaFile(path);
        }
        Integer id = ServletRequestUtils.getIntParameter(request, "id");
        if (id != null) {
            return mediaFileService.getMediaFile(id);
        }
        return null;
    }

    private long getFileLength(TranscodingService.Parameters parameters) {
        MediaFile file = parameters.getMediaFile();

        if (!parameters.isDownsample() && !parameters.isTranscode()) {
            return file.getFileSize();
        }
        Integer duration = file.getDurationSeconds();
        Integer maxBitRate = parameters.getMaxBitRate();

        if (duration == null) {
            LOG.warn("Unknown duration for " + file + ". Unable to estimate transcoded size.");
            return file.getFileSize();
        }

        if (maxBitRate == null) {
            LOG.error("Unknown bit rate for " + file + ". Unable to estimate transcoded size.");
            return file.getFileSize();
        }

        return duration * maxBitRate * 1000L / 8L;
    }

    private HttpRange getRange(HttpServletRequest request, MediaFile file) {

        // First, look for "Range" HTTP header.
        HttpRange range = HttpRange.valueOf(request.getHeader("Range"));
        if (range != null) {
            return range;
        }

        // Second, look for "offsetSeconds" request parameter.
        String offsetSeconds = request.getParameter("offsetSeconds");
        range = parseAndConvertOffsetSeconds(offsetSeconds, file);
        if (range != null) {
            return range;
        }

        return null;
    }

    private HttpRange parseAndConvertOffsetSeconds(String offsetSeconds, MediaFile file) {
        if (offsetSeconds == null) {
            return null;
        }

        try {
            Integer duration = file.getDurationSeconds();
            Long fileSize = file.getFileSize();
            if (duration == null || fileSize == null) {
                return null;
            }
            float offset = Float.parseFloat(offsetSeconds);

            // Convert from time offset to byte offset.
            long byteOffset = (long) (fileSize * (offset / duration));
            return new HttpRange(byteOffset, null);

        } catch (Exception x) {
            LOG.error("Failed to parse and convert time offset: " + offsetSeconds, x);
            return null;
        }
    }

    private VideoTranscodingSettings createVideoTranscodingSettings(MediaFile file, HttpServletRequest request) throws ServletRequestBindingException {
        Integer existingWidth = file.getWidth();
        Integer existingHeight = file.getHeight();
        Integer maxBitRate = ServletRequestUtils.getIntParameter(request, "maxBitRate");
        int timeOffset = ServletRequestUtils.getIntParameter(request, "timeOffset", 0);
        int defaultDuration = file.getDurationSeconds() == null ? Integer.MAX_VALUE : file.getDurationSeconds() - timeOffset;
        int duration = ServletRequestUtils.getIntParameter(request, "duration", defaultDuration);
        boolean hls = ServletRequestUtils.getBooleanParameter(request, "hls", false);

        Dimension dim = getRequestedVideoSize(request.getParameter("size"));
        if (dim == null) {
            dim = getSuitableVideoSize(existingWidth, existingHeight, maxBitRate);
        }

        return new VideoTranscodingSettings(dim.width, dim.height, timeOffset, duration, hls);
    }

    protected Dimension getRequestedVideoSize(String sizeSpec) {
        if (sizeSpec == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("^(\\d+)x(\\d+)$");
        Matcher matcher = pattern.matcher(sizeSpec);
        if (matcher.find()) {
            int w = Integer.parseInt(matcher.group(1));
            int h = Integer.parseInt(matcher.group(2));
            if (w >= 0 && h >= 0 && w <= 2000 && h <= 2000) {
                return new Dimension(w, h);
            }
        }
        return null;
    }

    protected Dimension getSuitableVideoSize(Integer existingWidth, Integer existingHeight, Integer maxBitRate) {
        if (maxBitRate == null) {
            return new Dimension(400, 300);
        }

        int w, h;
        if (maxBitRate < 400) {
            w = 400; h = 300;
        } else if (maxBitRate < 600) {
            w = 480; h = 360;
        } else if (maxBitRate < 1800) {
            w = 640; h = 480;
        } else {
            w = 960; h = 720;
        }

        if (existingWidth == null || existingHeight == null) {
            return new Dimension(w, h);
        }

        if (existingWidth < w || existingHeight < h) {
            return new Dimension(even(existingWidth), even(existingHeight));
        }

        double aspectRate = existingWidth.doubleValue() / existingHeight.doubleValue();
        h = (int) Math.round(w / aspectRate);

        return new Dimension(even(w), even(h));
    }

    // Make sure width and height are multiples of two, as some versions of ffmpeg require it.
    private int even(int size) {
        return size + (size % 2);
    }

    /**
     * Feed the other end with some dummy data to keep it from reconnecting.
     */
    private void sendDummy(byte[] buf, OutputStream out) throws IOException {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException x) {
            LOG.warn("Interrupted in sleep.", x);
        }
        Arrays.fill(buf, (byte) 0xFF);
        out.write(buf);
        out.flush();
    }

    public void setStatusService(StatusService statusService) {
        this.statusService = statusService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }

    public void setAudioScrobblerService(AudioScrobblerService audioScrobblerService) {
        this.audioScrobblerService = audioScrobblerService;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }
}
