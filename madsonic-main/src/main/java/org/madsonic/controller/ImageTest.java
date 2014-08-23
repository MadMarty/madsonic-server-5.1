package org.madsonic.controller;

import org.madsonic.domain.MediaFile;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public class ImageTest {

    public static void main(String[] args) throws IOException {

//        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
//
//        Graphics2D graphics = image.createGraphics();
//        paint(graphics);
//        graphics.dispose();
//
//        ImageIO.write(image, "png", new File("coverArt.png"));
        JFrame frame = new JFrame();

        JPanel panel = new JPanel();
        panel.add(new AlbumComponent(150));
        panel.add(new AlbumComponent(200));
        panel.add(new AlbumComponent(300));
        panel.add(new AlbumComponent(400));

        panel.setBackground(Color.DARK_GRAY);
        frame.add(panel);
        frame.setSize(1000, 800);
        frame.setVisible(true);
    }
 
    private static class AlbumComponent extends JComponent {
        private final int size;

        public AlbumComponent(int size) {
            this.size = size;
            setPreferredSize(new Dimension(size, size));
        }

        @Override
        protected void paintComponent(Graphics g) {
            MediaFile mediaFile = new MediaFile();
            mediaFile.setPath("/tmp");
            mediaFile.setAlbumArtist("Album artist");
            mediaFile.setAlbumArtist("Song artist");
            mediaFile.setTitle("Song title");
            mediaFile.setAlbumName("Album name");

            new CoverArtControllerEx.AutoCover((Graphics2D) g, mediaFile, size).paintCover();
        }
    }
}
