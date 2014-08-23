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

import org.madsonic.Logger;
import org.madsonic.domain.AvatarScheme;
import org.madsonic.domain.MediaFile;
import org.madsonic.domain.Player;
import org.madsonic.domain.TransferStatus;
import org.madsonic.domain.UserSettings;
import org.madsonic.service.MediaFileService;
import org.madsonic.service.MediaScannerService;
import org.madsonic.service.PlayerService;
import org.madsonic.service.SettingsService;
import org.madsonic.service.StatusService;
import org.madsonic.service.metadata.MetaData;
import org.madsonic.service.metadata.MetaDataParser;
import org.madsonic.service.metadata.MetaDataParserFactory;
import org.madsonic.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides AJAX-enabled services for retrieving the currently playing file and directory.
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class NowPlayingService {

    private static final Logger LOG = Logger.getLogger(NowPlayingService.class);

    private PlayerService playerService;
    private StatusService statusService;
    private SettingsService settingsService;
    private MediaScannerService mediaScannerService;
    private MediaFileService mediaFileService;
    private MetaDataParserFactory metaDataParserFactory;

    /**
     * Returns details about what the current player is playing.
     *
     * @return Details about what the current player is playing, or <code>null</code> if not playing anything.
     */
    public NowPlayingInfo getNowPlayingForCurrentPlayer() throws Exception {
        WebContext webContext = WebContextFactory.get();
        Player player = playerService.getPlayer(webContext.getHttpServletRequest(), webContext.getHttpServletResponse());
        List<TransferStatus> statuses = statusService.getStreamStatusesForPlayer(player);
        List<NowPlayingInfo> result = convert(statuses);

        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Returns details about what all users are currently playing.
     *
     * @return Details about what all users are currently playing.
     */
    public List<NowPlayingInfo> getNowPlaying() throws Exception {
        try {
            return convert(statusService.getAllStreamStatuses());
        } catch (Throwable x) {
            LOG.error("Unexpected error in getNowPlaying: " + x, x);
            return Collections.emptyList();
        }
    }

    /**
     * Returns media folder scanning status.
     */
    public ScanInfo getScanningStatus() {
        return new ScanInfo(mediaScannerService.isScanning(), mediaScannerService.getScanCount());
    }

    private List<NowPlayingInfo> convert(List<TransferStatus> statuses) throws Exception {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        String url = request.getRequestURL().toString();
        List<NowPlayingInfo> result = new ArrayList<NowPlayingInfo>();
        for (TransferStatus status : statuses) {

            Player player = status.getPlayer();
            File file = status.getFile();

            if (player != null && player.getUsername() != null && file != null) {

                String username = player.getUsername();
                UserSettings userSettings = settingsService.getUserSettings(username);
                if (!userSettings.isNowPlayingAllowed()) {
                    continue;
                }

                MediaFile mediaFile = mediaFileService.getMediaFile(file);
                File coverArt = mediaFileService.getCoverArt(mediaFile);

                String artist = mediaFile.getArtist();
                String title = mediaFile.getTitle();
                
                String streamUrl = url.replaceFirst("/dwr/.*", "/stream?player=" + player.getId() + "&id=" + mediaFile.getId());
                String albumUrl = url.replaceFirst("/dwr/.*", "/main.view?id=" + mediaFile.getId());
                
                MetaDataParser parser = metaDataParserFactory.getParser(mediaFile.getFile());
                MetaData metaData = parser.getMetaData(mediaFile.getFile());

            	String lyricsUrl;
            	if (metaData.hasLyrics()) {
            		lyricsUrl = "lyrics.view?id=" + mediaFile.getId();
            	} else {
                    lyricsUrl = url.replaceFirst("/dwr/.*", "/lyrics.view?artistUtf8Hex=" + StringUtil.utf8HexEncode(artist) + "&songUtf8Hex=" + StringUtil.utf8HexEncode(title));
            	}
            	
//              String coverArtUrl = coverArt == null ? null : url.replaceFirst("/dwr/.*", "/coverArt.view?size=48&id=" + mediaFile.getId());
//              String coverArtZoomUrl = coverArt == null ? null : url.replaceFirst("/dwr/.*", "/coverArt.view?id=" + mediaFile.getId());

                String coverArtUrl = url.replaceFirst("/dwr/.*", "/coverArt.view?size=48&id=" + mediaFile.getId());
                String coverArtZoomUrl = url.replaceFirst("/dwr/.*", "/coverArt.view?id=" + mediaFile.getId());
 
				
                String avatarUrl = null;
                if (userSettings.getAvatarScheme() == AvatarScheme.SYSTEM) {
                    avatarUrl = url.replaceFirst("/dwr/.*", "/avatar.view?id=" + userSettings.getSystemAvatarId());
                } else if (userSettings.getAvatarScheme() == AvatarScheme.CUSTOM && settingsService.getCustomAvatar(username) != null) {
                    avatarUrl = url.replaceFirst("/dwr/.*", "/avatar.view?username=" + username);
                }

                // Rewrite URLs in case we're behind a proxy.
                if (settingsService.isRewriteUrlEnabled()) {
                    String referer = request.getHeader("referer");
                    streamUrl = StringUtil.rewriteUrl(streamUrl, referer);
                    albumUrl = StringUtil.rewriteUrl(albumUrl, referer);
                    lyricsUrl = StringUtil.rewriteUrl(lyricsUrl, referer);
                    coverArtUrl = StringUtil.rewriteUrl(coverArtUrl, referer);
                    coverArtZoomUrl = StringUtil.rewriteUrl(coverArtZoomUrl, referer);
                    avatarUrl = StringUtil.rewriteUrl(avatarUrl, referer);
                }

                String tooltip = StringUtil.toHtml(artist) + " &ndash; " + StringUtil.toHtml(title);

                if (StringUtils.isNotBlank(player.getName())) {
                    username += "@" + player.getName();
                }
                artist = StringUtil.toHtml(StringUtils.abbreviate(artist, 25));
                title = StringUtil.toHtml(StringUtils.abbreviate(title, 25));
                username = StringUtil.toHtml(StringUtils.abbreviate(username, 25));

                long minutesAgo = status.getMillisSinceLastUpdate() / 1000L / 60L;
                if (minutesAgo < 60) {
                    result.add(new NowPlayingInfo(username, artist, title, tooltip, streamUrl, albumUrl, lyricsUrl,
                            coverArtUrl, coverArtZoomUrl, avatarUrl, (int) minutesAgo));
                }
            }
        }

        return result;

    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setStatusService(StatusService statusService) {
        this.statusService = statusService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setMediaScannerService(MediaScannerService mediaScannerService) {
        this.mediaScannerService = mediaScannerService;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }
    
    public void setMetaDataParserFactory(MetaDataParserFactory metaDataParserFactory) {
        this.metaDataParserFactory = metaDataParserFactory;
    }
    
}
