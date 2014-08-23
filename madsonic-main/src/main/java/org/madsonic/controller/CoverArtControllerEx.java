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

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.LastModified;

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
import org.madsonic.util.FileUtil;
import org.madsonic.util.StringUtil;

/**
 * Controller which produces cover art images.
 *
 * @author Sindre Mehus
 */
public class CoverArtControllerEx implements Controller, LastModified {

    public static final String ALBUM_COVERART_PREFIX = "al-";
    public static final String ARTIST_COVERART_PREFIX = "ar-";

    private static final Logger LOG = Logger.getLogger(CoverArtController.class);

    private SecurityService securityService;
    private MediaFileService mediaFileService;
    private ArtistDao artistDao;
    private AlbumDao albumDao;

    public long getLastModified(HttpServletRequest request) {
        try {
            File file = getImageFile(request);
            if (file == null) {
                return 0;  // Request for the default image.
            }
            if (!FileUtil.exists(file)) {
                return -1;
            }

            return FileUtil.lastModified(file);
        } catch (Exception e) {
            return  -1;
        }
    }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        File file = getImageFile(request);

        if (file != null && !FileUtil.exists(file)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        // Check access.
        if (file != null && !securityService.isReadAllowed(file)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }

        // Send default image if no path is given. (No need to cache it, since it will be cached in browser.)
        Integer size = ServletRequestUtils.getIntParameter(request, "size");
		boolean typArtist = ServletRequestUtils.getBooleanParameter(request, "typArtist", false);
		
		if (typArtist == true) {
			if (file == null) {
				sendDefaultArtist(size, response);
				return null;
			}
		}
		else
		{
			if (file == null) {
				sendDefault(size, request, response);
				return null;
			}
		}

        // Optimize if no scaling is required.
        if (size == null) {
            sendUnscaled(file, response);
            return null;
        }

        // Send cached image, creating it if necessary.
        try {
            File cachedImage = getCachedImage(file, size);
            sendImage(cachedImage, response);
        } catch (IOException e) {
            sendDefault(size, request, response);
        }

        return null;
    }

    private File getImageFile(HttpServletRequest request) {
        String id = request.getParameter("id");
        if (id != null) {
            if (id.startsWith(ALBUM_COVERART_PREFIX)) {
                return getAlbumImage(Integer.valueOf(id.replace(ALBUM_COVERART_PREFIX, "")));
            }
            if (id.startsWith(ARTIST_COVERART_PREFIX)) {
                return getArtistImage(Integer.valueOf(id.replace(ARTIST_COVERART_PREFIX, "")));
            }
            
            return getMediaFileImage(Integer.valueOf(id));
            	
        }

        String path = StringUtils.trimToNull(request.getParameter("path"));
        return path != null ? new File(path) : null;
    }

    private File getArtistImage(int id) {
        Artist artist = artistDao.getArtist(id);
        return artist == null || artist.getCoverArtPath() == null ? null : new File(artist.getCoverArtPath());
    }

    private File getAlbumImage(int id) {
        Album album = albumDao.getAlbum(id);
        return album == null || album.getCoverArtPath() == null ? null : new File(album.getCoverArtPath());
    }

    private File getMediaFileImage(int id) {
        MediaFile mediaFile = mediaFileService.getMediaFile(id);
        return mediaFile == null ? null : mediaFileService.getCoverArt(mediaFile);
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

    private void sendDefault(Integer size, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            MediaFile mediaFile = mediaFileService.getMediaFile(id);
            sendAutoGenerated(size, mediaFile, response);
        } catch (Throwable x) {
            sendFallback(size, response);
        }
    }

    private void sendAutoGenerated(Integer size, MediaFile mediaFile, HttpServletResponse response) throws IOException {
        if (mediaFile.isFile()) {
            mediaFile = mediaFileService.getParentOf(mediaFile);
        }
        if (size == null) {
            size = CoverArtScheme.MEDIUM.getSize() * 2;
        }
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        AutoCover autoCover = new AutoCover(graphics, mediaFile, size);
        autoCover.paintCover();
        graphics.dispose();

        response.setContentType(StringUtil.getMimeType("png"));
        ImageIO.write(image, "png", response.getOutputStream());
    }

    private void sendFallback(Integer size, HttpServletResponse response) throws IOException {
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
	
    private void sendUnscaled(File file, HttpServletResponse response) throws IOException {
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

    private File getCachedImage(File file, int size) throws IOException {
        String md5 = DigestUtils.md5Hex(file.getPath());
        File cachedImage = new File(getImageCacheDirectory(size), md5 + ".jpeg");

        // Is cache missing or obsolete?
        if (!cachedImage.exists() || FileUtil.lastModified(file) > cachedImage.lastModified()) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = getImageInputStream(file);
                out = new FileOutputStream(cachedImage);
                BufferedImage image = ImageIO.read(in);
                if (image == null) {
                    throw new Exception("Unable to decode image.");
                }

                image = scale(image, size, size);
                ImageIO.write(image, "jpeg", out);

            } catch (Throwable x) {
                // Delete corrupt (probably empty) thumbnail cache.
                LOG.warn("Failed to create thumbnail for " + file, x);
                IOUtils.closeQuietly(out);
                cachedImage.delete();
                throw new IOException("Failed to create thumbnail for " + file + ". " + x.getMessage());

            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
            }
        }
        return cachedImage;
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

    static class AutoCover {

        private final static int[] COLORS = {0xCF8E25}; // 0x33B5E5, 0xAA66CC, 0x99CC00, 0xFFBB33, 0xFF4444};
        private final Graphics2D graphics;
        private final MediaFile mediaFile;
        private final int size;
        private final Color color;

        public AutoCover(Graphics2D graphics, MediaFile mediaFile, int size) {
            this.graphics = graphics;
            this.mediaFile = mediaFile;
            this.size = size;

            int rgb = COLORS[Math.abs(mediaFile.getId()) % COLORS.length];
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

            String album = mediaFile.getAlbumName();
            if (album != null) {
                graphics.drawString(album, size * 0.05f, size * 0.25f);
            }
            String artist = mediaFile.getAlbumArtist();
            if (artist == null) {
                artist = mediaFile.getArtist();
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
