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

 Copyright 2013 (C) Sindre Mehus
 */
package org.madsonic.controller;

import org.madsonic.domain.MediaFile;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public class AutoCoverDemo {

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        panel.add(new AlbumComponent(110));
        panel.add(new AlbumComponent(150));
        panel.add(new AlbumComponent(200));
        panel.add(new AlbumComponent(300));
        panel.add(new AlbumComponent(400));

        panel.setBackground(Color.LIGHT_GRAY);
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
