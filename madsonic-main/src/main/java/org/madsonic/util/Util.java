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
package org.madsonic.util;

import org.madsonic.Logger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.madsonic.Logger;
import org.madsonic.service.SettingsService;

/**
 * Miscellaneous general utility methods.
 *
 * @author Sindre Mehus
 */
public final class Util {

    private static final Logger LOG = Logger.getLogger(Util.class);
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    /**
     * Disallow external instantiation.
     */
    private Util() {
    }

    public static String getDefaultMusicFolder() {
        String def = isWindows() ? "c:\\media" : "/var/media/Artist";
        return System.getProperty("madsonic.defaultMusicFolder", def);
    }

    public static String getDefaultUploadFolder() {
        String def = isWindows() ? "c:\\media\\Incoming" : "/var/media/Incoming";
        return System.getProperty("madsonic.defaultUploadFolder", def);
    }
    
    public static String getDefaultPodcastFolder() {
        String def = isWindows() ? "c:\\media\\Podcast" : "/var/media/Podcast";
        return System.getProperty("madsonic.defaultPodcastFolder", def);
    }

    public static String getDefaultPlaylistImportFolder() {
        String def = isWindows() ? "c:\\media\\playlists\\import" : "/var/media/playlists/import";
        return System.getProperty("madsonic.defaultPlaylistImportFolder", def);
    }

    public static String getDefaultPlaylistExportFolder() {
        String def = isWindows() ? "c:\\media\\playlists\\export" : "/var/media/playlists/export";
        return System.getProperty("madsonic.defaultPlaylistExportFolder", def);
    }    

    public static String getDefaultPlaylistBackupFolder() {
        String def = isWindows() ? "c:\\media\\playlists\\backup" : "/var/media/playlists/backup";
        return System.getProperty("madsonic.defaultPlaylistBackupFolder", def);
    }        
    
    public static boolean isWindows() {
        return (System.getProperty("os.name").toLowerCase().indexOf( "win" ) >= 0);
        //return System.getProperty("os.name", "Windows").toLowerCase().startsWith("windows");
    }

    public static boolean isMac() {
        return (System.getProperty("os.name").toLowerCase().indexOf( "mac" ) >= 0); 
        //return System.getProperty("os.name", "Mac").toLowerCase().startsWith("mac");
    }

    public static boolean isUnix() {
	return (System.getProperty("os.name").toLowerCase().indexOf("nix") >=0 || 
                System.getProperty("os.name").toLowerCase().indexOf("nux") >=0);
        //return System.getProperty("os.name", "Mac").toLowerCase().startsWith("mac");
    }

    public static boolean isWindowsInstall() {
        return "true".equals(System.getProperty("madsonic.windowsInstall"));
    }

    /**
     * Similar to {@link ServletResponse#setContentLength(int)}, but this
     * method supports lengths bigger than 2GB.
     * <p/>
     * See http://blogger.ziesemer.com/2008/03/suns-version-of-640k-2gb.html
     *
     * @param response The HTTP response.
     * @param length   The content length.
     */
    public static void setContentLength(HttpServletResponse response, long length) {
        if (length <= Integer.MAX_VALUE) {
            response.setContentLength((int) length);
        } else {
            response.setHeader("Content-Length", String.valueOf(length));
        }
    }

    /**
     * Returns the local IP address.  Honours the "madsonic.host" system property.
     * <p/>
     * NOTE: For improved performance, use {@link SettingsService#getLocalIpAddress()} instead.
     *
     * @return The local IP, or the loopback address (127.0.0.1) if not found.
     */
    public static String getLocalIpAddress() {
        List<String> ipAddresses = getLocalIpAddresses();
        String madsonicHost = System.getProperty("madsonic.host");
        if (madsonicHost != null && ipAddresses.contains(madsonicHost)) {
            return madsonicHost;
        }
        return ipAddresses.get(0);
    }

    private static List<String> getLocalIpAddresses() {
        List<String> result = new ArrayList<String>();

        // Try the simple way first.
        try {
            InetAddress address = InetAddress.getLocalHost();
            if (!address.isLoopbackAddress()) {
                result.add(address.getHostAddress());
            }
        } catch (Throwable x) {
            LOG.warn("Failed to resolve local IP address.", x);
        }

        // Iterate through all network interfaces, looking for a suitable IP.
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        result.add(addr.getHostAddress());
                    }
                }
            }
        } catch (Throwable x) {
            LOG.warn("Failed to resolve local IP address.", x);
        }

        if (result.isEmpty()) {
            result.add("127.0.0.1");
        }

        return result;
    }

    public static int randomInt(int min, int max) {
        if (min >= max) {
            return 0;
        }
        return min + RANDOM.nextInt(max - min);
    }

    public static <T> Iterable<T> toIterable(final Enumeration<?> e) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return toIterator(e);
            }
        };
    }

    public static <T> Iterator<T> toIterator(final Enumeration<?> e) {
        return new Iterator<T>() {
            public boolean hasNext() {
                return e.hasMoreElements();
            }

            public T next() {
                return (T) e.nextElement();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}