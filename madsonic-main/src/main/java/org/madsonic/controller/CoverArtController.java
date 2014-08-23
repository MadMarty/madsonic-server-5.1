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
import org.madsonic.dao.AlbumDao;
import org.madsonic.dao.ArtistDao;
import org.madsonic.domain.Album;
import org.madsonic.domain.Artist;
import org.madsonic.domain.CoverArtScheme;
import org.madsonic.domain.MediaFile;
import org.madsonic.service.MediaFileService;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;
import org.madsonic.service.metadata.JaudiotaggerParser;
import org.madsonic.util.StringUtil;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.LastModified;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Controller which produces cover art images.
 *
 * @author Sindre Mehus
 */
public class CoverArtController implements Controller, LastModified {

    public static final String ALBUM_COVERART_PREFIX = "al-";
    public static final String ARTIST_COVERART_PREFIX = "ar-";

    private static final Logger LOG = Logger.getLogger(CoverArtController.class);

    private SecurityService securityService;
    private MediaFileService mediaFileService;
    private ArtistDao artistDao;
    private AlbumDao albumDao;

    public long getLastModified(HttpServletRequest request) {
        CoverArtRequest coverArtRequest = createCoverArtRequest(request);
        long result = coverArtRequest.lastModified();
//        LOG.info("getLastModified - " + coverArtRequest + ": " + new Date(result));
        return result;
    }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        CoverArtRequest coverArtRequest = createCoverArtRequest(request);
//        LOG.info("handleRequest - " + coverArtRequest);
        Integer size = ServletRequestUtils.getIntParameter(request, "size");
        
		boolean typArtist = ServletRequestUtils.getBooleanParameter(request, "typArtist", false);
		boolean typVideo = ServletRequestUtils.getBooleanParameter(request, "typVideo", false);
		
		if (typArtist == true) {
			if (coverArtRequest == null) {
				sendDefaultArtist(size, response);
				return null;
			}
		}
		else
		{
			if (coverArtRequest == null) {
				sendDefault(size, response);
				return null;
			}
		}

        // Optimize if no scaling is required.
        if (typVideo) {
            sendUnscaledVideoset(size, coverArtRequest, response);
            return null;
        }
		
        // Optimize if no scaling is required.
        if (size == null) {
            sendUnscaled(coverArtRequest, response);
            return null;
        }

        // Send cached image, creating it if necessary.
        try {
            File cachedImage = getCachedImage(coverArtRequest, size);
            sendImage(cachedImage, response);
        } catch (IOException e) {
            sendDefault(size, response);
        }

        return null;
    }

    private CoverArtRequest createCoverArtRequest(HttpServletRequest request) {
        String id = request.getParameter("id");
        if (id == null) {
            return null;
        }

        if (id.startsWith(ALBUM_COVERART_PREFIX)) {
            return createAlbumCoverArtRequest(Integer.valueOf(id.replace(ALBUM_COVERART_PREFIX, "")));
        }
        if (id.startsWith(ARTIST_COVERART_PREFIX)) {
            return createArtistCoverArtRequest(Integer.valueOf(id.replace(ARTIST_COVERART_PREFIX, "")));
        }
        
        int req = 0;
        req = Integer.valueOf(id);
        return createMediaFileCoverArtRequest(req);
    }

    private CoverArtRequest createAlbumCoverArtRequest(int id) {
        Album album = albumDao.getAlbum(id);
        return album == null ? null : new AlbumCoverArtRequest(album);
    }

    private CoverArtRequest createArtistCoverArtRequest(int id) {
        Artist artist = artistDao.getArtist(id);
        return artist == null ? null : new ArtistCoverArtRequest(artist);
    }

    private CoverArtRequest createMediaFileCoverArtRequest(int id) {
        MediaFile mediaFile = mediaFileService.getMediaFile(id);
        return mediaFile == null ? null : new MediaFileCoverArtRequest(mediaFile);
    }

    private void sendImage(File file, HttpServletResponse response) throws IOException {
        response.setContentType(StringUtil.getMimeType(FilenameUtils.getExtension(file.getName())));
        InputStream in = new FileInputStream(file);
        try {
            IOUtils.copy(in, response.getOutputStream());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private void sendDefault(Integer size, HttpServletResponse response) throws IOException {
        if (response.getContentType() == null) {
        response.setContentType(StringUtil.getMimeType("png"));
        }
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream("default_cover.png");
            BufferedImage image = ImageIO.read(in);
            if (size != null) {
                image = scale(image, size, size);
            }
            ImageIO.write(image, "png", response.getOutputStream());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private void sendDefaultArtist(Integer size, HttpServletResponse response) throws IOException {
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream("default_artist.png");
            BufferedImage imageArtist = ImageIO.read(in);
            if (size != null) {
                imageArtist = scale(imageArtist, size, size);
            }
            ImageIO.write(imageArtist, "png", response.getOutputStream());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }	

    private void sendUnscaledVideoset(Integer size, CoverArtRequest coverArtRequest, HttpServletResponse response) throws IOException {
        File file = coverArtRequest.getCoverArt();
        JaudiotaggerParser parser = new JaudiotaggerParser();
        if (!parser.isApplicable(file)) {
            response.setContentType(StringUtil.getMimeType(FilenameUtils.getExtension(file.getName())));
        }
        InputStream in = null;
        try {
            in = getImageInputStream(file);
            BufferedImage imageArtist = videoScale(ImageIO.read(in), size, (int) (size * 1.3));
            ImageIO.write(imageArtist, "png", response.getOutputStream());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
   
    
    private void sendUnscaled(CoverArtRequest coverArtRequest, HttpServletResponse response) throws IOException {
        File file = coverArtRequest.getCoverArt();
        JaudiotaggerParser parser = new JaudiotaggerParser();
        if (!parser.isApplicable(file)) {
            response.setContentType(StringUtil.getMimeType(FilenameUtils.getExtension(file.getName())));
        }
        InputStream in = null;
        try {
            in = getImageInputStream(file);
            IOUtils.copy(in, response.getOutputStream());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private File getCachedImage(CoverArtRequest request, int size) throws IOException {
        String hash = DigestUtils.md5Hex(request.getKey());
        String encoding = request.getCoverArt() != null ? "jpeg" : "png";
        File cachedImage = new File(getImageCacheDirectory(size), hash + "." + encoding);

        // Synchronize to avoid concurrent writing to the same file.
        synchronized (hash.intern()) {

            // Is cache missing or obsolete?
            if (!cachedImage.exists() || request.lastModified() > cachedImage.lastModified()) {
//                LOG.info("Cache MISS - " + request + " (" + size + ")");
                OutputStream out = null;
                try {
                    BufferedImage image = request.createImage(size);
                    if (image == null) {
                        throw new Exception("Unable to decode image.");
                    }
                    out = new FileOutputStream(cachedImage);
                    ImageIO.write(image, encoding, out);

                } catch (Throwable x) {
                    // Delete corrupt (probably empty) thumbnail cache.
                    LOG.warn("Failed to create thumbnail for " + request, x);
                    IOUtils.closeQuietly(out);
                    cachedImage.delete();
                    throw new IOException("Failed to create thumbnail for " + request + ". " + x.getMessage());

                } finally {
                    IOUtils.closeQuietly(out);
                }
            } else {
//                LOG.info("Cache HIT - " + request + " (" + size + ")");
            }
            return cachedImage;
        }
    }

    /**
     * Returns an input stream to the image in the given file.  If the file is an audio file,
     * the embedded album art is returned.
     */
    private InputStream getImageInputStream(File file) throws IOException {
        JaudiotaggerParser parser = new JaudiotaggerParser();
        if (parser.isApplicable(file)) {
            MediaFile mediaFile = mediaFileService.getMediaFile(file);
            return new ByteArrayInputStream(parser.getImageData(mediaFile));
        } else {
            return new FileInputStream(file);
        }
    }

    private synchronized File getImageCacheDirectory(int size) {
        File dir = new File(SettingsService.getMadsonicHome(), "thumbs");
        dir = new File(dir, String.valueOf(size));
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                LOG.info("Created thumbnail cache " + dir);
            } else {
                LOG.error("Failed to create thumbnail cache " + dir);
            }
        }

        return dir;
    }

    public static BufferedImage videoScale(BufferedImage image, int width, int height) {
    	
        BufferedImage thumb = image;

        BufferedImage temp = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = temp.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(thumb, 0, 0, width, height, null);
        g2.dispose();
        thumb = temp;
            
        return thumb;
    }
    
    public static BufferedImage scale(BufferedImage image, int width, int height) {
    	
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage thumb = image;

        // For optimal results, use step by step bilinear resampling - halfing the size at each step.
        do {
            w /= 2;
            h /= 2;
            if (w < width) {
                w = width;
            }
            if (h < height) {
                h = height;
            }

            double thumbRatio = (double) width / (double) height;
            double aspectRatio = (double) w / (double) h;

//            LOG.debug("## thumbsRatio: " + thumbRatio);
//            LOG.debug("## aspectRatio: " + aspectRatio);
            
            if (thumbRatio < aspectRatio) {
            	h = (int) (w / aspectRatio);
            } else {
            	w = (int) (h * aspectRatio);
            }            
            
            BufferedImage temp = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = temp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(thumb, 0, 0, w, h, null);
            g2.dispose();

            thumb = temp;
        } while (w != width);

        //FIXME: check
        if (thumb.getHeight() > thumb.getWidth()) {
        	thumb = thumb.getSubimage(0, 0, thumb.getWidth(), thumb.getWidth());
        }
        return thumb;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setArtistDao(ArtistDao artistDao) {
        this.artistDao = artistDao;
    }

    public void setAlbumDao(AlbumDao albumDao) {
        this.albumDao = albumDao;
    }

    private abstract class CoverArtRequest {

        protected File coverArt;

        private CoverArtRequest() {
        }

        private CoverArtRequest(String coverArtPath) {
            this.coverArt = coverArtPath == null ? null : new File(coverArtPath);
        }

        private File getCoverArt() {
            return coverArt;
        }

        public abstract String getKey();

        public abstract long lastModified();

        public BufferedImage createImage(int size) {
            if (coverArt != null) {
                InputStream in = null;
                try {
                    in = getImageInputStream(coverArt);
                    return scale(ImageIO.read(in), size, size);
                } catch (Throwable x) {
                    LOG.warn("Failed to process cover art " + coverArt + ": " + x, x);
                } finally {
                    IOUtils.closeQuietly(in);
                }
            }
            return createAutoCover(size);
        }

        protected BufferedImage createAutoCover(int size) {
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            AutoCover autoCover = new AutoCover(graphics, getKey(), getArtist(), getAlbum(), size);
            autoCover.paintCover();
            graphics.dispose();
            return image;
        }

        public abstract String getAlbum();

        public abstract String getArtist();
    }

    private class ArtistCoverArtRequest extends CoverArtRequest {

        private final Artist artist;

        private ArtistCoverArtRequest(Artist artist) {
            super(artist.getCoverArtPath());
            this.artist = artist;
        }

        @Override
        public String getKey() {
            return artist.getCoverArtPath() != null ? artist.getCoverArtPath() : (ARTIST_COVERART_PREFIX + artist.getId());
        }

        @Override
        public long lastModified() {
            return coverArt != null ? coverArt.lastModified() : artist.getLastScanned().getTime();
        }

        @Override
        public String getAlbum() {
            return null;
        }

        @Override
        public String getArtist() {
            return artist.getName();
        }

        @Override
        public String toString() {
            return "Artist " + artist.getId() + " - " + artist.getName();
        }
    }

    private class AlbumCoverArtRequest extends CoverArtRequest {

        private final Album album;

        private AlbumCoverArtRequest(Album album) {
            super(album.getCoverArtPath());
            this.album = album;
        }

        @Override
        public String getKey() {
            return album.getCoverArtPath() != null ? album.getCoverArtPath() : (ALBUM_COVERART_PREFIX + album.getId());
        }

        @Override
        public long lastModified() {
            return coverArt != null ? coverArt.lastModified() : album.getLastScanned().getTime();
        }

        @Override
        public String getAlbum() {
            return album.getName();
        }

        @Override
        public String getArtist() {
            return album.getArtist();
        }

        @Override
        public String toString() {
            return "Album " + album.getId() + " - " + album.getName();
        }
    }

    private class MediaFileCoverArtRequest extends CoverArtRequest {

        private final MediaFile mediaFile;
        private final MediaFile dir;

        private MediaFileCoverArtRequest(MediaFile mediaFile) {
            this.mediaFile = mediaFile;
            dir = mediaFile.isDirectory() ? mediaFile : mediaFileService.getParentOf(mediaFile);
            coverArt = mediaFileService.getCoverArt(mediaFile);
        }

        @Override
        public String getKey() {
            return coverArt != null ? coverArt.getPath() : dir.getPath();
        }

        @Override
        public long lastModified() {
            return coverArt != null ? coverArt.lastModified() : dir.getChanged().getTime();
        }

        @Override
        public String getAlbum() {
            return dir.getName();
        }

        @Override
        public String getArtist() {
            return dir.getAlbumArtist() != null ? dir.getAlbumArtist() : dir.getArtist();
        }

        @Override
        public String toString() {
            return "Media file " + mediaFile.getId() + " - " + mediaFile;
        }
    }

    static class AutoCover {

        private final static int[] COLORS = {0xCF8E25}; // 0x33B5E5, 0xAA66CC, 0x99CC00, 0xFFBB33, 0xFF4444};
        private final Graphics2D graphics;
        private final String artist;
        private final String album;
        private final int size;
        private final Color color;

        public AutoCover(Graphics2D graphics, String key, String artist, String album, int size) {
            this.graphics = graphics;
            this.artist = artist;
            this.album = album;
            this.size = size;

            int hash = key.hashCode();
            int rgb = COLORS[Math.abs(hash) % COLORS.length];
            this.color = new Color(rgb);
        }

        public void paintCover() {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            graphics.setPaint(color);
            graphics.fillRect(0, 0, size, size);

            int y = size * 1 / 3;
            graphics.setPaint(new GradientPaint(0, y, new Color(82, 82, 82), 0, size, Color.BLACK));
            graphics.fillRect(0, y, size, size / 1);

            graphics.setPaint(Color.WHITE);
            float fontSize = 3.0f + size * 0.06f;
            Font font = new Font(Font.SANS_SERIF, Font.BOLD, (int) fontSize);
            graphics.setFont(font);

            if (album != null) {
                graphics.drawString(album, size * 0.05f, size * 0.25f);
            }
            if (artist != null) {
                graphics.drawString(artist, size * 0.05f, size * 0.45f);
            }

            int borderWidth = size / 50;
             graphics.fillRect(0, 0, borderWidth, size);
            graphics.fillRect(size - borderWidth, 0, size - borderWidth, size);
            graphics.fillRect(0, 0, size, borderWidth);
            graphics.fillRect(0, size - borderWidth, size, size);

            graphics.setColor(Color.BLACK);
            graphics.drawRect(0, 0, size - 1, size - 1);
        }
    }
}
