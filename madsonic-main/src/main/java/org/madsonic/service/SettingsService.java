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
package org.madsonic.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.TreeSet;

import org.madsonic.domain.MediaLibraryStatistics;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.jfree.util.Log;

import org.madsonic.Logger;
import org.madsonic.dao.AvatarDao;
import org.madsonic.dao.GroupDao;
import org.madsonic.dao.InternetRadioDao;
import org.madsonic.dao.MusicFolderDao;
import org.madsonic.dao.UserDao;
import org.madsonic.domain.Avatar;
import org.madsonic.domain.InternetRadio;
import org.madsonic.domain.LicenseInfo;
import org.madsonic.domain.MediaFile;
import org.madsonic.domain.MediaFileComparator;
import org.madsonic.domain.MusicFolderComparator;
import org.madsonic.domain.MusicFolder;
import org.madsonic.domain.Theme;
import org.madsonic.domain.UserSettings;
import org.madsonic.util.FileUtil;
import org.madsonic.util.StringUtil;
import org.madsonic.util.Util;

/**
 * Provides persistent storage of application settings and preferences.
 *
 * @author Sindre Mehus
 */
public class SettingsService {
 
    // Subsonic home directory.
    private static final File MADSONIC_HOME_WINDOWS = new File("c:/madsonic");
    private static final File MADSONIC_HOME_OTHER = new File("/var/madsonic");
    
    // Number of free trial days.
    private static final long TRIAL_DAYS = 30L;
	 
    // Global settings.
    private static final String KEY_PAGE_TITLE = "PageTitle";
    
    private static final String KEY_ALL_INDEX_STRING = "AllIndexString";
    private static final String KEY_MUSIC_INDEX_STRING = "MusicIndexString";
    private static final String KEY_VIDEO_INDEX_STRING = "VideoIndexString";
    
    private static final String KEY_INDEX1_STRING = "Index1String";
    private static final String KEY_INDEX2_STRING = "Index2String";
    private static final String KEY_INDEX3_STRING = "Index3String";
    private static final String KEY_INDEX4_STRING = "Index4String";
    
    private static final String KEY_IGNORED_ARTICLES = "IgnoredArticles";
    private static final String KEY_SHORTCUTS = "Shortcuts";
    private static final String KEY_UPLOAD_FOLDER = "UploadFolder";
    private static final String KEY_PLAYLIST_IMPORT_FOLDER = "PlaylistImportFolder";
    private static final String KEY_PLAYLIST_EXPORT_FOLDER = "PlaylistExportFolder";
    private static final String KEY_PLAYLIST_BACKUP_FOLDER = "PlaylistBackupFolder";
    private static final String KEY_MUSIC_FILE_TYPES = "MusicFileTypes";
    private static final String KEY_VIDEO_FILE_TYPES = "VideoFileTypes";
    private static final String KEY_COVER_ART_FILE_TYPES = "CoverArtFileTypes";
    private static final String KEY_COVER_ART_LIMIT = "CoverArtLimit";
    private static final String KEY_WELCOME_TITLE = "WelcomeTitle";
    private static final String KEY_WELCOME_SUBTITLE = "WelcomeSubtitle";
    private static final String KEY_WELCOME_MESSAGE = "WelcomeMessage2";
    private static final String KEY_LOGIN_MESSAGE = "LoginMessage";
    private static final String KEY_LOCALE_LANGUAGE = "LocaleLanguage";
    private static final String KEY_LOCALE_COUNTRY = "LocaleCountry";
    private static final String KEY_LOCALE_VARIANT = "LocaleVariant";
    private static final String KEY_THEME_ID = "Theme";
	private static final String KEY_LISTTYPE = "ListType";
	private static final String KEY_NEWADDED_TIMESPAN = "NewAddedTimeSpan";
    private static final String KEY_INDEX_CREATION_INTERVAL = "IndexCreationInterval";
    private static final String KEY_INDEX_CREATION_HOUR = "IndexCreationHour";
    private static final String KEY_FAST_CACHE_ENABLED = "FastCacheEnabled";
    private static final String KEY_PODCAST_UPDATE_INTERVAL = "PodcastUpdateInterval";
    private static final String KEY_PODCAST_FOLDER = "PodcastFolder";
    private static final String KEY_PODCAST_EPISODE_RETENTION_COUNT = "PodcastEpisodeRetentionCount";
    private static final String KEY_PODCAST_EPISODE_DOWNLOAD_COUNT = "PodcastEpisodeDownloadCount";
	private static final String KEY_PODCAST_EPISODE_DOWNLOAD_LIMIT = "PodcastEpisodeDownloadLimit";
    private static final String KEY_DOWNLOAD_BITRATE_LIMIT = "DownloadBitrateLimit";
    private static final String KEY_UPLOAD_BITRATE_LIMIT = "UploadBitrateLimit";
    private static final String KEY_STREAM_PORT = "StreamPort";
    private static final String KEY_LICENSE_EMAIL = "LicenseEmail";
    private static final String KEY_LICENSE_CODE = "LicenseCode";
    private static final String KEY_LICENSE_DATE = "LicenseDate";
    private static final String KEY_DOWNSAMPLING_COMMAND = "DownsamplingCommand4";
    private static final String KEY_HLS_COMMAND = "HlsCommand3";
    private static final String KEY_JUKEBOX_COMMAND = "JukeboxCommand2";
    private static final String KEY_REWRITE_URL = "RewriteUrl";
    private static final String KEY_LDAP_ENABLED = "LdapEnabled";
    private static final String KEY_LDAP_URL = "LdapUrl";
    private static final String KEY_LDAP_MANAGER_DN = "LdapManagerDn";
    private static final String KEY_LDAP_MANAGER_PASSWORD = "LdapManagerPassword";
    private static final String KEY_LDAP_SEARCH_FILTER = "LdapSearchFilter";
    private static final String KEY_LDAP_AUTO_SHADOWING = "LdapAutoShadowing";
    private static final String KEY_GETTING_STARTED_ENABLED = "GettingStartedEnabled";
    private static final String KEY_PORT_FORWARDING_ENABLED = "PortForwardingEnabled";
    private static final String KEY_PORT = "Port";
    private static final String KEY_HTTPS_PORT = "HttpsPort";
    private static final String KEY_URL_REDIRECTION_ENABLED = "UrlRedirectionEnabled";
    private static final String KEY_URL_REDIRECT_FROM = "UrlRedirectFrom";
    private static final String KEY_URL_REDIRECT_CONTEXT_PATH = "UrlRedirectContextPath";
    private static final String KEY_SERVER_ID = "ServerId";
    private static final String KEY_SETTINGS_CHANGED = "SettingsChanged";
    private static final String KEY_LAST_SCANNED = "LastScanned";
	private static final String KEY_MADSONIC_URL = "MadsonicUrl";
    private static final String KEY_ORGANIZE_BY_FOLDER_STRUCTURE = "OrganizeByFolderStructure";
    private static final String KEY_SHOW_ALBUMS_YEAR = "ShowAlbumsYear";
    private static final String KEY_SHOW_ALBUMS_YEAR_API = "ShowAlbumsYearAPI";
    private static final String KEY_SORT_ALBUMS_BY_FOLDER = "SortAlbumsByFolder";
    private static final String KEY_SORT_FILES_BY_FILENAME = "SortFilesByFilename";
    private static final String KEY_SORT_MEDIAFILEFOLDER = "SortMediaFileFolder";
	private static final String KEY_USE_PREMIUM_SERVICES = "UsePremiumServices";
	private static final String KEY_SHOW_GENERIC_ARTIST_ART = "ShowGenericArtistArt";
	private static final String KEY_SHOW_SHORTCUTS_ALWAYS = "ShowShortcutsAlways";
	private static final String KEY_SHOW_QUICK_EDIT = "ShowQuickEdit";
	
//	private static final String KEY_HTML5_PLAYER_ENABLED = "HTML5Enabled";
	private static final String KEY_USED_VIDEO_PLAYER = "usedVideoPlayer";
	
	private static final String KEY_OWN_GENRE_ENABLED = "ownGenreEnabled";
	private static final String KEY_PLAYLIST_ENABLED = "PlaylistEnabled";
		
    private static final String KEY_MEDIA_LIBRARY_STATISTICS = "MediaLibraryStatistics";
    private static final String KEY_TRIAL_EXPIRES = "TrialExpires";
    private static final String KEY_PLAYQUEUE_RESIZE = "PlayQueueResize";
    private static final String KEY_LEFTFRAME_RESIZE = "LeftFrameResize";
    private static final String KEY_PLAYQUEUE_SIZE = "PlayQueueSize";
    private static final String KEY_LEFTFRAME_SIZE = "LeftFrameSize";    
    private static final String KEY_CUSTOMSCROLLBAR = "CustomScrollbar";
    private static final String KEY_CUSTOMACCORDION = "CustomAccordion";
    
    private static final String KEY_ICON_HOME = "ShowIconHome";
    private static final String KEY_ICON_ARTIST = "ShowIconArtist";
    private static final String KEY_ICON_PLAYING = "ShowIconPlaying";
    private static final String KEY_ICON_STARRED = "ShowIconStarred";
    private static final String KEY_ICON_RADIO = "ShowIconRadio";
    private static final String KEY_ICON_PODAST = "ShowIconPodcast";
    private static final String KEY_ICON_SETTINGS = "ShowIconSettings";
    private static final String KEY_ICON_STATUS = "ShowIconStatus";
    private static final String KEY_ICON_SOCIAL = "ShowIconSocial";
    private static final String KEY_ICON_HISTORY = "ShowIconHistory";
    private static final String KEY_ICON_STATISTICS = "ShowIconStatistics";
    private static final String KEY_ICON_PLAYLISTS = "ShowIconPlaylists";
    private static final String KEY_ICON_PLAYLIST_EDITOR = "ShowIconPlaylistEditor";
    private static final String KEY_ICON_MORE = "ShowIconMore";    
    private static final String KEY_ICON_ABOUT = "ShowIconAbout";    
    private static final String KEY_ICON_GENRE = "ShowIconGenre";    
    private static final String KEY_ICON_MOODS = "ShowIconMoods";
    private static final String KEY_ICON_ADMINS = "ShowIconAdmins";  
    private static final String KEY_ICON_COVER = "ShowIconCover";
    private static final String KEY_DLNA_ENABLED = "DlnaEnabled";
    private static final String KEY_DLNA_SERVER_NAME = "DlnaServerName";

    private static final String KEY_FOLDERPARSING_ENABLED = "FolderParsingEnabled";
    private static final String KEY_ALBUMSETPARSING_ENABLED = "AlbumSetParsingEnabled";
    
    private static final String KEY_HOME_RANDOM = "ShowHomeRandom";    
    private static final String KEY_HOME_NEW_ADDED = "ShowHomeNewAdded"; 
    private static final String KEY_HOME_HOT_RATED = "ShowHomeHotRated"; 
    private static final String KEY_HOME_ALLARTIST = "ShowHomeAllArtist"; 
    private static final String KEY_HOME_STARRED_ARTIST = "ShowHomeStarredArtist"; 
    private static final String KEY_HOME_STARRED_ALBUM = "ShowHomeStarredAlbum"; 
    private static final String KEY_HOME_ALBUMTIP = "ShowHomeAblumTip"; 
    private static final String KEY_HOME_TOP_RATED = "ShowHomeTopRated"; 
    private static final String KEY_HOME_MOSTPLAYED = "ShowHomeMostPlayed"; 
    private static final String KEY_HOME_LASTPLAYED = "ShowHomeLastPlayed"; 
    private static final String KEY_HOME_DECADE = "ShowHomeDecade"; 
    private static final String KEY_HOME_GENRE = "ShowHomeGenre"; 
    private static final String KEY_HOME_NAME = "ShowHomeName"; 
    private static final String KEY_HOME_TOP100 = "ShowHomeTop100"; 
    private static final String KEY_HOME_NEW100 = "ShowHomeNew100"; 
    
    private static final String KEY_LOGFILE_REVERSE = "LogfileReverse";
    private static final String KEY_LOGFILE_LEVEL = "LogfileLevel";
    
    private static final String KEY_PANDORA_ARTIST_TOPTRACK = "PandoraResultArtistTopTrack";
    private static final String KEY_PANDORA_ALBUM = "PandoraResultAlbum";
    private static final String KEY_PANDORA_ARTIST = "PandoraResultArtist";
    private static final String KEY_PANDORA_GENRE = "PandoraResultGenre";
    private static final String KEY_PANDORA_MOOD = "PandoraResultMood";
    private static final String KEY_PANDORA_SIMILAR = "PandoraResultSimilar";

    private static final String KEY_LASTFM_RESULT_SIZE = "LastFMResultSize";
    
    // Madsonic Default values.
    private static final String DEFAULT_PAGE_TITLE = "Madsonic";
    private static final String DEFAULT_ALL_INDEX_STRING = "1";
    private static final String DEFAULT_MUSIC_INDEX_STRING = "1";
    private static final String DEFAULT_VIDEO_INDEX_STRING = "2";
    private static final String DEFAULT_INDEX1_STRING = "# ! 0 1 2 3 4 5 6 7 8 9 A B C D E F G H I J K L M N O P Q R S T U V W X Y Z";
    private static final String DEFAULT_INDEX2_STRING = "# A-F(ABCDEF) G-L(GHIJKL) M-S(MNOPQRS) T-Z(TUVWXYZ)";
    private static final String DEFAULT_INDEX3_STRING = "# 0-9(0123456789) A-Z(ABCDEFGHIJKLMNOPQRSTUVWXYZ)";
    private static final String DEFAULT_INDEX4_STRING = "# A-M(ABCDEFGHIJKLM) N-Z(NOPQRSTUVWXYZ)";
    
    private static final String DEFAULT_IGNORED_ARTICLES = "The El La Los Las Le Les";
    private static final String DEFAULT_SHORTCUTS = "New Incoming Podcast";

    private static String DEFAULT_UPLOAD_FOLDER = Util.getDefaultUploadFolder();
    
    private static final String DEFAULT_PLAYLIST_IMPORT_FOLDER = Util.getDefaultPlaylistImportFolder();
    private static final String DEFAULT_PLAYLIST_EXPORT_FOLDER = Util.getDefaultPlaylistExportFolder();
    private static final String DEFAULT_PLAYLIST_BACKUP_FOLDER = Util.getDefaultPlaylistBackupFolder();
	
    private static final String DEFAULT_MUSIC_FILE_TYPES = "mp3 ogg oga aac m4a flac wav wma aif aiff ape mpc shn alm 669 mdl far xm mod fnk imf it liq mod wow mtm ptm rtm stm s3m ult dmf dbm med okt emod sfx m15 mtn amf gdm stx gmc psm j2b umx amd rad hsc flx gtk mgt mtp";
    private static final String DEFAULT_VIDEO_FILE_TYPES = "avi divx flv hdrun m2ts m4v mkv mov mp4 mpeg mpg ogv tv url wmv wtv";
    private static final String DEFAULT_COVER_ART_FILE_TYPES = "front.jpg front.png cover.jpg cover.png folder.jpg folder.pnp folder.gif jpg jpeg gif png";
    private static final int DEFAULT_COVER_ART_LIMIT = 100;
    
    private static final String DEFAULT_WELCOME_TITLE = "Welcome to Madsonic!";
    private static final String DEFAULT_WELCOME_SUBTITLE = null;
    private static final String DEFAULT_WELCOME_MESSAGE = "__Welcome to Madsonic!__\n" +
            "\\\\ \\\\\n" +
            "Madsonic is a free, web-based media streamer, providing ubiquitous access to your music. \n" +
            "\\\\ \\\\\n" +
            "Use it to share your music with friends, or to listen to your own music while at work. You can stream to multiple " +
            "players simultaneously, for instance to one player in your kitchen and another in your living room.\n" +
            "\\\\ \\\\\n" +
            "To change or remove this message, log in with administrator rights and go to {link:Settings > General|generalSettings.view}.";
    private static final String DEFAULT_LOGIN_MESSAGE = null;
    private static final String DEFAULT_LOCALE_LANGUAGE = "en";
    private static final String DEFAULT_LOCALE_COUNTRY = "";
    private static final String DEFAULT_LOCALE_VARIANT = "";
    private static final String DEFAULT_THEME_ID = "madsonic_dark";
    private static final String DEFAULT_LISTTYPE = "random";
    private static final String DEFAULT_NEWADDED_TIMESPAN = "ThreeMonth";
    private static final boolean DEFAULT_SHOW_SHORTCUTS_ALWAYS = false;
    private static final int DEFAULT_INDEX_CREATION_INTERVAL = 1;
    private static final int DEFAULT_INDEX_CREATION_HOUR = 3;
    private static final boolean DEFAULT_FAST_CACHE_ENABLED = false;
    private static final int DEFAULT_PODCAST_UPDATE_INTERVAL = 24;
    private static final String DEFAULT_PODCAST_FOLDER = Util.getDefaultPodcastFolder();
    private static final int DEFAULT_PODCAST_EPISODE_RETENTION_COUNT = 10;
    private static final int DEFAULT_PODCAST_EPISODE_DOWNLOAD_COUNT = 1;
    private static final int DEFAULT_PODCAST_EPISODE_DOWNLOAD_LIMIT = 3;
    private static final long DEFAULT_DOWNLOAD_BITRATE_LIMIT = 0;
    private static final long DEFAULT_UPLOAD_BITRATE_LIMIT = 0;
    private static final long DEFAULT_STREAM_PORT = 0;
    private static final String DEFAULT_LICENSE_EMAIL = null;
    private static final String DEFAULT_LICENSE_CODE = null;
    private static final String DEFAULT_LICENSE_DATE = null;
    private static final String DEFAULT_DOWNSAMPLING_COMMAND = "ffmpeg -i %s -map 0:0 -b:a %bk -v 0 -f mp3 -";
    private static final String DEFAULT_HLS_COMMAND = "ffmpeg -ss %o -t %d -i %s -async 1 -b:v %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f mpegts -c:v libx264 -preset superfast -c:a libmp3lame -threads 0 -";
    private static final String DEFAULT_JUKEBOX_COMMAND = "ffmpeg -ss %o -i %s -map 0:0 -v 0 -f au -";
    private static final boolean DEFAULT_REWRITE_URL = true;
    private static final boolean DEFAULT_LDAP_ENABLED = false;
    private static final String DEFAULT_LDAP_URL = "ldap://host.domain.com:389/cn=Users,dc=domain,dc=com";
    private static final String DEFAULT_LDAP_MANAGER_DN = null;
    private static final String DEFAULT_LDAP_MANAGER_PASSWORD = null;
    private static final String DEFAULT_LDAP_SEARCH_FILTER = "(sAMAccountName={0})";
    private static final boolean DEFAULT_LDAP_AUTO_SHADOWING = false;
    private static final boolean DEFAULT_PORT_FORWARDING_ENABLED = false;
    private static final boolean DEFAULT_GETTING_STARTED_ENABLED = true;
    private static final int DEFAULT_PORT = 80;
    private static final int DEFAULT_HTTPS_PORT = 0;
    private static final boolean DEFAULT_URL_REDIRECTION_ENABLED = false;
    private static final String DEFAULT_URL_REDIRECT_FROM = "yourname";
    private static final String DEFAULT_URL_REDIRECT_CONTEXT_PATH = null;
    private static final String DEFAULT_SERVER_ID = null;
    private static final long DEFAULT_SETTINGS_CHANGED = 0L;
	private static final String DEFAULT_MADSONIC_URL = "http://localhost:4040";
    private static final boolean DEFAULT_ORGANIZE_BY_FOLDER_STRUCTURE = true;
	private static final boolean DEFAULT_SHOW_ALBUMS_YEAR = true;
	private static final boolean DEFAULT_SHOW_ALBUMS_YEAR_API = false;
	private static final boolean DEFAULT_SORT_ALBUMS_BY_FOLDER = false;
	private static final boolean DEFAULT_SORT_FILES_BY_FILENAME = false;
	private static final boolean DEFAULT_SORT_MEDIAFILEFOLDER = true;
	private static final boolean DEFAULT_USE_PREMIUM_SERVICES = false;
	private static final boolean DEFAULT_SHOW_GENERIC_ARTIST_ART = true;
	private static final boolean DEFAULT_SHOW_QUICK_EDIT = false;
	private static final boolean DEFAULT_OWN_GENRE_ENABLED = false;
	private static final boolean DEFAULT_PLAYLIST_ENABLED = false;
	private static final String DEFAULT_USED_VIDEO_PLAYER = "CHROMECAST";
	
    private static final String DEFAULT_MEDIA_LIBRARY_STATISTICS = "0 0 0 0 0 0 0 0 0 0";
    private static final String DEFAULT_TRIAL_EXPIRES = null;
    private static final boolean DEFAULT_PLAYQUEUE_RESIZE = false;
    private static final boolean DEFAULT_LEFTFRAME_RESIZE = false;
    private static final int DEFAULT_PLAYQUEUE_SIZE = 215;
    private static final int DEFAULT_LEFTFRAME_SIZE = 255;
    private static final boolean DEFAULT_CUSTOMSCROLLBAR = true;
    private static final boolean DEFAULT_CUSTOMACCORDION = false;
    private static final boolean DEFAULT_DLNA_ENABLED = false;
    private static final String DEFAULT_DLNA_SERVER_NAME = "Madsonic";
    private static final boolean DEFAULT_FOLDERPARSING_ENABLED = true;
    private static final boolean DEFAULT_ALBUMSETPARSING_ENABLED = true;
    
    private static final boolean DEFAULT_LOGFILE_REVERSE = false;
    private static final String DEFAULT_LOGFILE_LEVEL = "INFO";
    
    private static final boolean DEFAULT_ICON_HOME = true;
    private static final boolean DEFAULT_ICON_ARTIST = true;
    private static final boolean DEFAULT_ICON_PLAYING = true;
    private static final boolean DEFAULT_ICON_STARRED = true;
    private static final boolean DEFAULT_ICON_RADIO = true;
    private static final boolean DEFAULT_ICON_PODAST = true;
    private static final boolean DEFAULT_ICON_SETTINGS = true;
    private static final boolean DEFAULT_ICON_STATUS = true;
    private static final boolean DEFAULT_ICON_SOCIAL = true;
    private static final boolean DEFAULT_ICON_HISTORY = true;
    private static final boolean DEFAULT_ICON_STATISTICS = true;
    private static final boolean DEFAULT_ICON_PLAYLISTS = true;    
    private static final boolean DEFAULT_ICON_PLAYLIST_EDITOR = true;    
    private static final boolean DEFAULT_ICON_MORE = true;    
    private static final boolean DEFAULT_ICON_ABOUT = false;    
    private static final boolean DEFAULT_ICON_GENRE = true;    
    private static final boolean DEFAULT_ICON_MOODS = true;    
    private static final boolean DEFAULT_ICON_ADMINS = false;    
    private static final boolean DEFAULT_ICON_COVER = true;    
    
    private static final boolean DEFAULT_HOME_RANDOM = true;   
    private static final boolean DEFAULT_HOME_NEW_ADDED = true; 
    private static final boolean DEFAULT_HOME_HOT_RATED = true;
    private static final boolean DEFAULT_HOME_ALLARTIST = true;
    private static final boolean DEFAULT_HOME_STARRED_ARTIST = true;
    private static final boolean DEFAULT_HOME_STARRED_ALBUM = true;
    private static final boolean DEFAULT_HOME_ALBUMTIP = true;
    private static final boolean DEFAULT_HOME_TOP_RATED = true;
    private static final boolean DEFAULT_HOME_MOSTPLAYED = true;
    private static final boolean DEFAULT_HOME_LASTPLAYED = true;
    private static final boolean DEFAULT_HOME_DECADE = true;
    private static final boolean DEFAULT_HOME_GENRE = true;
    private static final boolean DEFAULT_HOME_NAME = true;
    private static final boolean DEFAULT_HOME_TOP100 = true;
    private static final boolean DEFAULT_HOME_NEW100 = true;
    
    private static final int DEFAULT_PANDORA_ALBUM = 1;
    private static final int DEFAULT_PANDORA_ARTIST = 1;
    private static final int DEFAULT_PANDORA_ARTIST_TOPTRACK = 2;
    private static final int DEFAULT_PANDORA_GENRE = 2;
    private static final int DEFAULT_PANDORA_MOOD = 2;
    private static final int DEFAULT_PANDORA_SIMILAR = 2;
    
    private static final int DEFAULT_GROUP_ALL = -1;
    private static final int DEFAULT_GROUP_NONE = 0;
    private static final int DEFAULT_GROUP_MUSIC = 1;
    private static final int DEFAULT_GROUP_VIDEO = 2;
    
    private static final int DEFAULT_LASTFM_RESULT_SIZE = 20;
    
    // Array of obsolete keys.  Used to clean property file.
    private static final List<String> OBSOLETE_KEYS = Arrays.asList("PortForwardingPublicPort", "PortForwardingLocalPort",
            "DownsamplingCommand", "DownsamplingCommand2", "DownsamplingCommand3", "AutoCoverBatch", "MusicMask",
            "VideoMask", "CoverArtMask, HlsCommand", "HlsCommand2", "JukeboxCommand", "UrlRedirectTrialExpires", "VideoTrialExpires", "PandoraResult", "HTML5Enabled");

    private static final String LOCALES_FILE = "/org/madsonic/i18n/locales.txt";
    private static final String THEMES_FILE = "/org/madsonic/theme/themes.txt";

    private static final Logger LOG = Logger.getLogger(SettingsService.class);

    private static Properties properties = new Properties();
    private List<Theme> themes;
    private List<Locale> locales;
    private InternetRadioDao internetRadioDao;
    private MusicFolderDao musicFolderDao;
    private GroupDao groupDao;
    private UserDao userDao;
    private AvatarDao avatarDao;
    private VersionService versionService;

    private String[] cachedCoverArtFileTypesArray;
    private String[] cachedMusicFileTypesArray;
    private String[] cachedVideoFileTypesArray;
    private List<MusicFolder> cachedMusicFolders;
    
    private static File madsonicHome;
	private static File madsonicUpload;
    
    private boolean licenseValidated = true;
    private Date licenseExpires;
    
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private static final long LOCAL_IP_LOOKUP_DELAY_SECONDS = 60;
    private String localIpAddress;

    public SettingsService() {
        File propertyFile = getPropertyFile();

        if (propertyFile.exists()) {
            FileInputStream in = null;
            try {
                in = new FileInputStream(propertyFile);
                properties.load(in);
            } catch (Exception x) {
                LOG.error("Unable to read from property file.", x);
            } finally {
                IOUtils.closeQuietly(in);
            }

            // Remove obsolete properties.
            for (Iterator<Object> iterator = properties.keySet().iterator(); iterator.hasNext();) {
                String key = (String) iterator.next();
                if (OBSOLETE_KEYS.contains(key)) {
                    LOG.debug("Removing obsolete property [" + key + ']');
                    iterator.remove();
                }
            }
        }

        // Start trial.
        if (getTrialExpires() == null) {
            Date expiryDate = new Date(System.currentTimeMillis() + TRIAL_DAYS * 24L * 3600L * 1000L);
            setTrialExpires(expiryDate);
        }

        save(false);
    }

    /**
     * Register in service locator so that non-Spring objects can access me.
     * This method is invoked automatically by Spring.
     */
    @SuppressWarnings("deprecation")
	public void init() {
        ServiceLocator.setSettingsService(this);
        scheduleLocalIpAddressLookup();
        validateLicenseAsync();
    }

    public void save() {
        save(true);
    }

    public void save(boolean updateChangedDate) {
        if (updateChangedDate) {
            setProperty(KEY_SETTINGS_CHANGED, String.valueOf(System.currentTimeMillis()));
        }

        OutputStream out = null;
        try {
            out = new FileOutputStream(getPropertyFile());
            properties.store(out, "Madsonic preferences.  NOTE: This file is automatically generated.");
        } catch (Exception x) {
            LOG.error("Unable to write to property file.", x);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    private File getPropertyFile() {
        return new File(getMadsonicHome(), "madsonic.properties");
    }

    /**
     * Returns the Madsonic home directory.
     *
     * @return The Madsonic home directory, if it exists.
     * @throws RuntimeException If directory doesn't exist.
     */
    public static synchronized File getMadsonicHome() {

        if (madsonicHome != null) {
            return madsonicHome;
        }

        File home;

        String overrideHome = System.getProperty("madsonic.home");
        if (overrideHome != null) {
            home = new File(overrideHome);
        } else {
            boolean isWindows = System.getProperty("os.name", "Windows").toLowerCase().startsWith("windows");
            home = isWindows ? MADSONIC_HOME_WINDOWS : MADSONIC_HOME_OTHER;
        }

        // Attempt to create home directory if it doesn't exist.
        if (!home.exists() || !home.isDirectory()) {
            boolean success = home.mkdirs();
            if (success) {
                madsonicHome = home;
            } else {
                String message = "The directory " + home + " does not exist. Please create it and make it writable. " +
                        "(You can override the directory location by specifying -Dmadsonic.home=... when " +
                        "starting the servlet container.)";
                System.err.println("ERROR: " + message);
            }
        } else {
            madsonicHome = home;
        }

        return home;
    }

    
    public synchronized static File getMadsonicUpload() {

//        if (madsonicUpload != null) {
//            return madsonicUpload;
//        }

        File upload;
        													 
        String overrideUpload = System.getProperty("madsonic.defaultUploadFolder");
        if (overrideUpload != null) {
            upload = new File(overrideUpload);
        } else {
        	
        	String tmp = properties.getProperty(KEY_UPLOAD_FOLDER, null);
        	
        	if (tmp != null) {
                upload = new File(tmp);
        		//;
        	} else
        	{
                upload = new File(Util.getDefaultUploadFolder());
        	}
        	
        }

        // Attempt to create upload directory if it doesn't exist.
        if (!upload.exists() || !upload.isDirectory()) {
            boolean success = upload.mkdirs();
            if (success) {
            	madsonicUpload = upload;
            } else {
                String message = "The directory " + upload + " does not exist. Please create it and make it writable. " +
                        "(You can override the directory location by specifying -Dmadsonic.defaultUploadFolder=... when " +
                        "starting the servlet container.)";
                System.err.println("ERROR: " + message);
            }
        } else {
        	madsonicUpload = upload;
        }

        // Save as Default
       // setUploadFolder(madbsonicUpload.getPath());
        return upload;
    }

    public String getPageTitle() {
        return properties.getProperty(KEY_PAGE_TITLE, DEFAULT_PAGE_TITLE);
    }
    
	public void setPageTitle(String pageTitle) {
		properties.setProperty(KEY_PAGE_TITLE, pageTitle);
	}
    
    public static String getUploadFolder() {
        return properties.getProperty(KEY_UPLOAD_FOLDER, DEFAULT_UPLOAD_FOLDER); //getMadsonicUpload().getPath());
    }

    public static void setUploadFolder(String uploadFolder) {
        properties.setProperty(KEY_UPLOAD_FOLDER, uploadFolder);
    }
   
    private boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.valueOf(properties.getProperty(key, String.valueOf(defaultValue)));
    }

    private void setBoolean(String key, boolean value) {
        setProperty(key, String.valueOf(value));
    }

    private String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    private void setString(String key, String value) {
        setProperty(key, value);
    }
    
    public String getAllIndexString() {
        return properties.getProperty(KEY_ALL_INDEX_STRING, DEFAULT_ALL_INDEX_STRING);
    }

    public void setAllIndexString(String indexString) {
        setProperty(KEY_ALL_INDEX_STRING, indexString);
    }    
    
    public String getMusicIndexString() {
        return properties.getProperty(KEY_MUSIC_INDEX_STRING, DEFAULT_MUSIC_INDEX_STRING);
    }

    public void setMusicIndexString(String indexString) {
        setProperty(KEY_MUSIC_INDEX_STRING, indexString);
    }
    
    public String getVideoIndexString() {
        return properties.getProperty(KEY_VIDEO_INDEX_STRING, DEFAULT_VIDEO_INDEX_STRING);
    }

    public void setVideoIndexString(String indexString) {
        setProperty(KEY_VIDEO_INDEX_STRING, indexString);
    }    
    

    public String getIndex1String() {
        return properties.getProperty(KEY_INDEX1_STRING, DEFAULT_INDEX1_STRING);
    }

    public void setIndex1String(String indexString) {
        setProperty(KEY_INDEX1_STRING, indexString);
    }

    public String getIndex2String() {
        return properties.getProperty(KEY_INDEX2_STRING, DEFAULT_INDEX2_STRING);
    }

    public void setIndex2String(String indexString) {
        setProperty(KEY_INDEX2_STRING, indexString);
    }

	public int getPandoraResultArtistTopTrack() {
        return Integer.valueOf(properties.getProperty(KEY_PANDORA_ARTIST_TOPTRACK, String.valueOf(DEFAULT_PANDORA_ARTIST_TOPTRACK)));
	}

    public void setPandoraResultArtistTopTrack(int size) {
        setProperty(KEY_PANDORA_ARTIST_TOPTRACK, String.valueOf(size));
    }
    
	public int getPandoraResultAlbum() {
        return Integer.valueOf(properties.getProperty(KEY_PANDORA_ALBUM, String.valueOf(DEFAULT_PANDORA_ALBUM)));
	}

    public void setPandoraResultAlbum(int size) {
        setProperty(KEY_PANDORA_ALBUM, String.valueOf(size));
    }
    
	public int getPandoraResultArtist() {
        return Integer.valueOf(properties.getProperty(KEY_PANDORA_ARTIST, String.valueOf(DEFAULT_PANDORA_ARTIST)));
	}

    public void setPandoraResultArtist(int size) {
        setProperty(KEY_PANDORA_ARTIST, String.valueOf(size));
    }
	public int getPandoraResultGenre() {
        return Integer.valueOf(properties.getProperty(KEY_PANDORA_GENRE, String.valueOf(DEFAULT_PANDORA_GENRE)));
	}

    public void setPandoraResultGenre(int size) {
        setProperty(KEY_PANDORA_GENRE, String.valueOf(size));
    }
	public int getPandoraResultMood() {
        return Integer.valueOf(properties.getProperty(KEY_PANDORA_MOOD, String.valueOf(DEFAULT_PANDORA_MOOD)));
	}

    public void setPandoraResultMood(int size) {
        setProperty(KEY_PANDORA_MOOD, String.valueOf(size));
    }
        
	public int getPandoraResultSimilar() {
        return Integer.valueOf(properties.getProperty(KEY_PANDORA_SIMILAR, String.valueOf(DEFAULT_PANDORA_SIMILAR)));
	}

    public void setPandoraResultSimilar(int size) {
        setProperty(KEY_PANDORA_SIMILAR, String.valueOf(size));
    }
    
    public String getIndex3String() {
        return properties.getProperty(KEY_INDEX3_STRING, DEFAULT_INDEX3_STRING);
    }

    public void setIndex3String(String indexString) {
        setProperty(KEY_INDEX3_STRING, indexString);
    }

    public String getIndex4String() {
        return properties.getProperty(KEY_INDEX4_STRING, DEFAULT_INDEX4_STRING);
    }

    public void setIndex4String(String indexString) {
        setProperty(KEY_INDEX4_STRING, indexString);
    }
	
	
    public String getIgnoredArticles() {
        return properties.getProperty(KEY_IGNORED_ARTICLES, DEFAULT_IGNORED_ARTICLES);
    }

    public String[] getIgnoredArticlesAsArray() {
        return getIgnoredArticles().split("\\s+");
    }

    public void setIgnoredArticles(String ignoredArticles) {
        setProperty(KEY_IGNORED_ARTICLES, ignoredArticles);
    }

    public String getShortcuts() {
        return properties.getProperty(KEY_SHORTCUTS, DEFAULT_SHORTCUTS);
    }

    public String[] getShortcutsAsArray() {
        return StringUtil.split(getShortcuts());
    }

    public void setShortcuts(String shortcuts) {
        setProperty(KEY_SHORTCUTS, shortcuts);
    }

    public String getPlaylistImportFolder() {
        return properties.getProperty(KEY_PLAYLIST_IMPORT_FOLDER, DEFAULT_PLAYLIST_IMPORT_FOLDER);
    }

    public void setPlaylistImportFolder(String playlistFolder) {
        setProperty(KEY_PLAYLIST_IMPORT_FOLDER, playlistFolder);
    }
	
	
    public void setPlaylistExportFolder(String playlistExportFolder) {
        setProperty(KEY_PLAYLIST_EXPORT_FOLDER, playlistExportFolder);
    }

    public String getPlaylistExportFolder() {
        return properties.getProperty(KEY_PLAYLIST_EXPORT_FOLDER, DEFAULT_PLAYLIST_EXPORT_FOLDER);
    }

    public void setPlaylistBackupFolder(String playlistExportFolder) {
        setProperty(KEY_PLAYLIST_BACKUP_FOLDER, playlistExportFolder);
    }

    public String getPlaylistBackupFolder() {
        return properties.getProperty(KEY_PLAYLIST_BACKUP_FOLDER, DEFAULT_PLAYLIST_BACKUP_FOLDER);
    }    
    
	
    public String getMusicFileTypes() {
        return properties.getProperty(KEY_MUSIC_FILE_TYPES, DEFAULT_MUSIC_FILE_TYPES);
    }

    public synchronized void setMusicFileTypes(String fileTypes) {
        setProperty(KEY_MUSIC_FILE_TYPES, fileTypes);
        cachedMusicFileTypesArray = null;
    }

    public synchronized String[] getMusicFileTypesAsArray() {
        if (cachedMusicFileTypesArray == null) {
            cachedMusicFileTypesArray = toStringArray(getMusicFileTypes());
        }
        return cachedMusicFileTypesArray;
    }

    public String getVideoFileTypes() {
        return properties.getProperty(KEY_VIDEO_FILE_TYPES, DEFAULT_VIDEO_FILE_TYPES);
    }

    public synchronized void setVideoFileTypes(String fileTypes) {
        setProperty(KEY_VIDEO_FILE_TYPES, fileTypes);
        cachedVideoFileTypesArray = null;
    }

    public synchronized String[] getVideoFileTypesAsArray() {
        if (cachedVideoFileTypesArray == null) {
            cachedVideoFileTypesArray = toStringArray(getVideoFileTypes());
        }
        return cachedVideoFileTypesArray;
    }

    public String getCoverArtFileTypes() {
        return properties.getProperty(KEY_COVER_ART_FILE_TYPES, DEFAULT_COVER_ART_FILE_TYPES);
    }

    public synchronized void setCoverArtFileTypes(String fileTypes) {
        setProperty(KEY_COVER_ART_FILE_TYPES, fileTypes);
        cachedCoverArtFileTypesArray = null;
    }

    public synchronized String[] getCoverArtFileTypesAsArray() {
        if (cachedCoverArtFileTypesArray == null) {
            cachedCoverArtFileTypesArray = toStringArray(getCoverArtFileTypes());
        }
        return cachedCoverArtFileTypesArray;
    }

    public int getCoverArtLimit() {
        return Integer.parseInt(properties.getProperty(KEY_COVER_ART_LIMIT, "" + DEFAULT_COVER_ART_LIMIT));
    }

    public void setCoverArtLimit(int limit) {
        setProperty(KEY_COVER_ART_LIMIT, "" + limit);
    }

    public String getWelcomeTitle() {
        return StringUtils.trimToNull(properties.getProperty(KEY_WELCOME_TITLE, DEFAULT_WELCOME_TITLE));
    }

    public void setWelcomeTitle(String title) {
        setProperty(KEY_WELCOME_TITLE, title);
    }

    public String getWelcomeSubtitle() {
        return StringUtils.trimToNull(properties.getProperty(KEY_WELCOME_SUBTITLE, DEFAULT_WELCOME_SUBTITLE));
    }

    public void setWelcomeSubtitle(String subtitle) {
        setProperty(KEY_WELCOME_SUBTITLE, subtitle);
    }

    public String getWelcomeMessage() {
        return StringUtils.trimToNull(properties.getProperty(KEY_WELCOME_MESSAGE, DEFAULT_WELCOME_MESSAGE));
    }

    public void setWelcomeMessage(String message) {
        setProperty(KEY_WELCOME_MESSAGE, message);
    }

    public String getLoginMessage() {
        return StringUtils.trimToNull(properties.getProperty(KEY_LOGIN_MESSAGE, DEFAULT_LOGIN_MESSAGE));
    }

    public void setLoginMessage(String message) {
        setProperty(KEY_LOGIN_MESSAGE, message);
    }

    /**
     * Returns the number of days between automatic index creation, of -1 if automatic index
     * creation is disabled.
     */
    public int getIndexCreationInterval() {
        return Integer.parseInt(properties.getProperty(KEY_INDEX_CREATION_INTERVAL, "" + DEFAULT_INDEX_CREATION_INTERVAL));
    }

    /**
     * Sets the number of days between automatic index creation, of -1 if automatic index
     * creation is disabled.
     */
    public void setIndexCreationInterval(int days) {
        setProperty(KEY_INDEX_CREATION_INTERVAL, String.valueOf(days));
    }

    /**
     * Returns the hour of day (0 - 23) when automatic index creation should run.
     */
    public int getIndexCreationHour() {
        return Integer.parseInt(properties.getProperty(KEY_INDEX_CREATION_HOUR, String.valueOf(DEFAULT_INDEX_CREATION_HOUR)));
    }

    /**
     * Sets the hour of day (0 - 23) when automatic index creation should run.
     */
    public void setIndexCreationHour(int hour) {
        setProperty(KEY_INDEX_CREATION_HOUR, String.valueOf(hour));
    }

    public boolean isFastCacheEnabled() {
        return getBoolean(KEY_FAST_CACHE_ENABLED, DEFAULT_FAST_CACHE_ENABLED);
    }

    public void setFastCacheEnabled(boolean enabled) {
        setBoolean(KEY_FAST_CACHE_ENABLED, enabled);
    }

    /**
     * Returns the number of hours between Podcast updates, of -1 if automatic updates
     * are disabled.
     */
    public int getPodcastUpdateInterval() {
        return Integer.parseInt(properties.getProperty(KEY_PODCAST_UPDATE_INTERVAL, String.valueOf(DEFAULT_PODCAST_UPDATE_INTERVAL)));
    }

    /**
     * Sets the number of hours between Podcast updates, of -1 if automatic updates
     * are disabled.
     */
    public void setPodcastUpdateInterval(int hours) {
        setProperty(KEY_PODCAST_UPDATE_INTERVAL, String.valueOf(hours));
    }

    /**
     * Returns the number of Podcast episodes to keep (-1 to keep all).
     */
    public int getPodcastEpisodeRetentionCount() {
        return Integer.parseInt(properties.getProperty(KEY_PODCAST_EPISODE_RETENTION_COUNT, String.valueOf(DEFAULT_PODCAST_EPISODE_RETENTION_COUNT)));
    }

    /**
     * Sets the number of Podcast episodes to keep (-1 to keep all).
     */
    public void setPodcastEpisodeRetentionCount(int count) {
        setProperty(KEY_PODCAST_EPISODE_RETENTION_COUNT, String.valueOf(count));
    }

    /**
     * Returns the number of Podcast episodes to download (-1 to download all).
     */
    public int getPodcastEpisodeDownloadCount() {
        return Integer.parseInt(properties.getProperty(KEY_PODCAST_EPISODE_DOWNLOAD_COUNT, String.valueOf(DEFAULT_PODCAST_EPISODE_DOWNLOAD_COUNT)));
    }

    /**
     * Sets the number of Podcast episodes to download (-1 to download all).
     */
    public void setPodcastEpisodeDownloadCount(int count) {
        setProperty(KEY_PODCAST_EPISODE_DOWNLOAD_COUNT, String.valueOf(count));
    }

    public int getPodcastEpisodeDownloadLimit() {
        return Integer.parseInt(properties.getProperty(KEY_PODCAST_EPISODE_DOWNLOAD_LIMIT, String.valueOf(DEFAULT_PODCAST_EPISODE_DOWNLOAD_LIMIT)));
	}

    public void setPodcastEpisodeDownloadLimit(int count) {
        setProperty(KEY_PODCAST_EPISODE_DOWNLOAD_LIMIT, String.valueOf(count));
    }    
    
	/**
     * Returns the Podcast download folder.
     */
    public String getPodcastFolder() {
        return properties.getProperty(KEY_PODCAST_FOLDER, DEFAULT_PODCAST_FOLDER);
    }

    /**
     * Sets the Podcast download folder.
     */
    public void setPodcastFolder(String folder) {
        setProperty(KEY_PODCAST_FOLDER, folder);
    }

    /**
     * @return The download bitrate limit in Kbit/s. Zero if unlimited.
     */
    public long getDownloadBitrateLimit() {
        return Long.parseLong(properties.getProperty(KEY_DOWNLOAD_BITRATE_LIMIT, "" + DEFAULT_DOWNLOAD_BITRATE_LIMIT));
    }

    /**
     * @param limit The download bitrate limit in Kbit/s. Zero if unlimited.
     */
    public void setDownloadBitrateLimit(long limit) {
        setProperty(KEY_DOWNLOAD_BITRATE_LIMIT, "" + limit);
    }

    /**
     * @return The upload bitrate limit in Kbit/s. Zero if unlimited.
     */
    public long getUploadBitrateLimit() {
        return Long.parseLong(properties.getProperty(KEY_UPLOAD_BITRATE_LIMIT, "" + DEFAULT_UPLOAD_BITRATE_LIMIT));
    }

    /**
     * @param limit The upload bitrate limit in Kbit/s. Zero if unlimited.
     */
    public void setUploadBitrateLimit(long limit) {
        setProperty(KEY_UPLOAD_BITRATE_LIMIT, "" + limit);
    }

    /**
     * @return The non-SSL stream port. Zero if disabled.
     */
    public int getStreamPort() {
        return Integer.parseInt(properties.getProperty(KEY_STREAM_PORT, "" + DEFAULT_STREAM_PORT));
    }

    /**
     * @param port The non-SSL stream port. Zero if disabled.
     */
    public void setStreamPort(int port) {
        setProperty(KEY_STREAM_PORT, "" + port);
    }

    public String getLicenseEmail() {
        return properties.getProperty(KEY_LICENSE_EMAIL, DEFAULT_LICENSE_EMAIL);
    }

    public void setLicenseEmail(String email) {
        setProperty(KEY_LICENSE_EMAIL, email);
    }

    public String getLicenseCode() {
        return properties.getProperty(KEY_LICENSE_CODE, DEFAULT_LICENSE_CODE);
    }

    public void setLicenseCode(String code) {
        setProperty(KEY_LICENSE_CODE, code);
    }

    public Date getLicenseDate() {
        String value = properties.getProperty(KEY_LICENSE_DATE, DEFAULT_LICENSE_DATE);
        return value == null ? null : new Date(Long.parseLong(value));
    }

    public void setLicenseDate(Date date) {
        String value = (date == null ? null : String.valueOf(date.getTime()));
        setProperty(KEY_LICENSE_DATE, value);
    }

    public boolean isLicenseValid() {
		if (!isUsePremiumServices()){
        return true;
		}
		else {
	        return isLicenseValid(getLicenseEmail(), getLicenseCode()) && licenseValidated;
		}
    }

    public boolean isLicenseValid(String email, String license) {
		if (!isUsePremiumServices()){
        return true;
		}
		else {
	       if (email == null || license == null) {
	            return false;
	        }
	        return license.equalsIgnoreCase(StringUtil.md5Hex(email.toLowerCase()));
		}
    }	
		
    public Date getLicenseExpires() {
        return licenseExpires;
    }

    public LicenseInfo getLicenseInfo() {
        Date trialExpires = getTrialExpires();
        Date now = new Date();
        boolean trialValid = trialExpires.after(now);
        long trialDaysLeft = trialValid ? (trialExpires.getTime() - now.getTime()) / (24L * 3600L * 1000L) : 0L;

        return new LicenseInfo(getLicenseEmail(), isLicenseValid(), trialExpires, trialDaysLeft, licenseExpires);
    }

    public String getDownsamplingCommand() {
        return properties.getProperty(KEY_DOWNSAMPLING_COMMAND, DEFAULT_DOWNSAMPLING_COMMAND);
    }

    public void setDownsamplingCommand(String command) {
        setProperty(KEY_DOWNSAMPLING_COMMAND, command);
    }

    public String getHlsCommand() {
        return properties.getProperty(KEY_HLS_COMMAND, DEFAULT_HLS_COMMAND);
    }

    public void setHlsCommand(String command) {
        setProperty(KEY_HLS_COMMAND, command);
    }

    public String getJukeboxCommand() {
        return properties.getProperty(KEY_JUKEBOX_COMMAND, DEFAULT_JUKEBOX_COMMAND);
    }

    public boolean isShowShortcuts() {
        return getBoolean(KEY_SHOW_SHORTCUTS_ALWAYS, DEFAULT_SHOW_SHORTCUTS_ALWAYS);
    }

    public void setShowShortcuts(boolean b) {
        setBoolean(KEY_SHOW_SHORTCUTS_ALWAYS, b);
    }

    public boolean isShowQuickEdit() {
        return getBoolean(KEY_SHOW_QUICK_EDIT, DEFAULT_SHOW_QUICK_EDIT);
    }

    public void setShowQuickEdit(boolean b) {
        setBoolean(KEY_SHOW_QUICK_EDIT, b);
    }

	public static String getUsedVideoPlayer() {
		  return properties.getProperty(KEY_USED_VIDEO_PLAYER, DEFAULT_USED_VIDEO_PLAYER);
	}    
    
    public void setUsedVideoPlayer(String s) {
        setString(KEY_USED_VIDEO_PLAYER, s);
    }
	
//    public boolean isHTML5PlayerEnabled() {
//        return getBoolean(KEY_HTML5_PLAYER_ENABLED, DEFAULT_HTML5_PLAYER_ENABLED);
//    }
//
//    public void setHTML5PlayerEnabled(boolean b) {
//        setBoolean(KEY_HTML5_PLAYER_ENABLED, b);
//    }
    
    public boolean isOwnGenreEnabled() {
        return getBoolean(KEY_OWN_GENRE_ENABLED, DEFAULT_OWN_GENRE_ENABLED);
    }

    public void setOwnGenreEnabled(boolean b) {
        setBoolean(KEY_OWN_GENRE_ENABLED, b);
    }

    public boolean isPlaylistEnabled() {
        return getBoolean(KEY_PLAYLIST_ENABLED, DEFAULT_PLAYLIST_ENABLED);
    }

    public void setPlaylistEnabled(boolean b) {
        setBoolean(KEY_PLAYLIST_ENABLED, b);
    }
    
    public boolean isRewriteUrlEnabled() {
        return getBoolean(KEY_REWRITE_URL, DEFAULT_REWRITE_URL);
    }

    public void setRewriteUrlEnabled(boolean rewriteUrl) {
        setBoolean(KEY_REWRITE_URL, rewriteUrl);
    }

    public boolean isLdapEnabled() {
        return getBoolean(KEY_LDAP_ENABLED, DEFAULT_LDAP_ENABLED);
    }

    public void setLdapEnabled(boolean ldapEnabled) {
        setBoolean(KEY_LDAP_ENABLED, ldapEnabled);
    }

    public String getLdapUrl() {
        return properties.getProperty(KEY_LDAP_URL, DEFAULT_LDAP_URL);
    }

    public void setLdapUrl(String ldapUrl) {
        properties.setProperty(KEY_LDAP_URL, ldapUrl);
    }

    public String getLdapSearchFilter() {
        return properties.getProperty(KEY_LDAP_SEARCH_FILTER, DEFAULT_LDAP_SEARCH_FILTER);
    }

    public void setLdapSearchFilter(String ldapSearchFilter) {
        properties.setProperty(KEY_LDAP_SEARCH_FILTER, ldapSearchFilter);
    }

    public String getLdapManagerDn() {
        return properties.getProperty(KEY_LDAP_MANAGER_DN, DEFAULT_LDAP_MANAGER_DN);
    }

    public void setLdapManagerDn(String ldapManagerDn) {
        properties.setProperty(KEY_LDAP_MANAGER_DN, ldapManagerDn);
    }

    public String getLdapManagerPassword() {
        String s = properties.getProperty(KEY_LDAP_MANAGER_PASSWORD, DEFAULT_LDAP_MANAGER_PASSWORD);
        try {
            return StringUtil.utf8HexDecode(s);
        } catch (Exception x) {
            LOG.warn("Failed to decode LDAP manager password.", x);
            return s;
        }
    }

    public void setLdapManagerPassword(String ldapManagerPassword) {
        try {
            ldapManagerPassword = StringUtil.utf8HexEncode(ldapManagerPassword);
        } catch (Exception x) {
            LOG.warn("Failed to encode LDAP manager password.", x);
        }
        properties.setProperty(KEY_LDAP_MANAGER_PASSWORD, ldapManagerPassword);
    }

    public boolean isLdapAutoShadowing() {
        return getBoolean(KEY_LDAP_AUTO_SHADOWING, DEFAULT_LDAP_AUTO_SHADOWING);
    }

    public void setLdapAutoShadowing(boolean ldapAutoShadowing) {
        setBoolean(KEY_LDAP_AUTO_SHADOWING, ldapAutoShadowing);
    }

    public boolean isGettingStartedEnabled() {
        return getBoolean(KEY_GETTING_STARTED_ENABLED, DEFAULT_GETTING_STARTED_ENABLED);
    }

    public void setGettingStartedEnabled(boolean isGettingStartedEnabled) {
        setBoolean(KEY_GETTING_STARTED_ENABLED, isGettingStartedEnabled);
    }

    public boolean isPortForwardingEnabled() {
        return getBoolean(KEY_PORT_FORWARDING_ENABLED, DEFAULT_PORT_FORWARDING_ENABLED);
    }

    public void setPortForwardingEnabled(boolean isPortForwardingEnabled) {
        setBoolean(KEY_PORT_FORWARDING_ENABLED, isPortForwardingEnabled);
    }

    public int getPort() {
        return Integer.valueOf(properties.getProperty(KEY_PORT, String.valueOf(DEFAULT_PORT)));
    }

    public void setPort(int port) {
        setProperty(KEY_PORT, String.valueOf(port));
    }

    public int getHttpsPort() {
        return Integer.valueOf(properties.getProperty(KEY_HTTPS_PORT, String.valueOf(DEFAULT_HTTPS_PORT)));
    }

    public void setHttpsPort(int httpsPort) {
        setProperty(KEY_HTTPS_PORT, String.valueOf(httpsPort));
    }

    public boolean isUrlRedirectionEnabled() {
        return getBoolean(KEY_URL_REDIRECTION_ENABLED, DEFAULT_URL_REDIRECTION_ENABLED);
    }

    public void setUrlRedirectionEnabled(boolean isUrlRedirectionEnabled) {
        setBoolean(KEY_URL_REDIRECTION_ENABLED, isUrlRedirectionEnabled);
    }

    public String getUrlRedirectFrom() {
        return properties.getProperty(KEY_URL_REDIRECT_FROM, DEFAULT_URL_REDIRECT_FROM);
    }

    public void setUrlRedirectFrom(String urlRedirectFrom) {
        properties.setProperty(KEY_URL_REDIRECT_FROM, urlRedirectFrom);
    }

    public Date getTrialExpires() {
        String value = properties.getProperty(KEY_TRIAL_EXPIRES, DEFAULT_TRIAL_EXPIRES);
        return value == null ? null : new Date(Long.parseLong(value));
    }

    public void setTrialExpires(Date date) {
        String value = (date == null ? null : String.valueOf(date.getTime()));
        setProperty(KEY_TRIAL_EXPIRES, value);
    }

    public String getUrlRedirectContextPath() {
        return properties.getProperty(KEY_URL_REDIRECT_CONTEXT_PATH, DEFAULT_URL_REDIRECT_CONTEXT_PATH);
    }

    public void setUrlRedirectContextPath(String contextPath) {
        properties.setProperty(KEY_URL_REDIRECT_CONTEXT_PATH, contextPath);
    }
    
    public String getMadsonicUrl() {
        return properties.getProperty(KEY_MADSONIC_URL, DEFAULT_MADSONIC_URL);
    }

    public void setMadsonicUrl(String madsonicUrl) {
        properties.setProperty(KEY_MADSONIC_URL, madsonicUrl);
    }

    public String getServerId() {
        return properties.getProperty(KEY_SERVER_ID, DEFAULT_SERVER_ID);
    }

    public void setServerId(String serverId) {
        properties.setProperty(KEY_SERVER_ID, serverId);
    }

    public long getSettingsChanged() {
        return Long.parseLong(properties.getProperty(KEY_SETTINGS_CHANGED, String.valueOf(DEFAULT_SETTINGS_CHANGED)));
    }

    public Date getLastScanned() {
        String lastScanned = properties.getProperty(KEY_LAST_SCANNED);
        return lastScanned == null ? null : new Date(Long.parseLong(lastScanned));
    }

    public void setLastScanned(Date date) {
        if (date == null) {
            properties.remove(KEY_LAST_SCANNED);
        } else {
            properties.setProperty(KEY_LAST_SCANNED, String.valueOf(date.getTime()));
        }
    }

    public boolean isOrganizeByFolderStructure() {
        return getBoolean(KEY_ORGANIZE_BY_FOLDER_STRUCTURE, DEFAULT_ORGANIZE_BY_FOLDER_STRUCTURE);
    }

    public void setOrganizeByFolderStructure(boolean b) {
        setBoolean(KEY_ORGANIZE_BY_FOLDER_STRUCTURE, b);
    }

    public boolean isShowAlbumsYear() {
        return getBoolean(KEY_SHOW_ALBUMS_YEAR, DEFAULT_SHOW_ALBUMS_YEAR);
    }
    
    public void setShowAlbumsYear(boolean b) {
        setBoolean(KEY_SHOW_ALBUMS_YEAR, b);
    }

    public boolean isShowAlbumsYearApi() {
		return getBoolean(KEY_SHOW_ALBUMS_YEAR_API, DEFAULT_SHOW_ALBUMS_YEAR_API);
	}

    public void setShowAlbumsYearApi(boolean b) {
        setBoolean(KEY_SHOW_ALBUMS_YEAR_API, b);
    }
    
	public boolean isSortAlbumsByFolder() {
        return getBoolean(KEY_SORT_ALBUMS_BY_FOLDER, DEFAULT_SORT_ALBUMS_BY_FOLDER);
    }

    public void setSortAlbumsByFolder(boolean b) {
        setBoolean(KEY_SORT_ALBUMS_BY_FOLDER, b);
    }

    public boolean isSortFilesByFilename() {
        return getBoolean(KEY_SORT_FILES_BY_FILENAME, DEFAULT_SORT_FILES_BY_FILENAME);
    }

    public void setSortFilesByFilename(boolean b) {
        setBoolean(KEY_SORT_FILES_BY_FILENAME, b);
    }

    public boolean isSortMediaFileFolder() {
        return getBoolean(KEY_SORT_MEDIAFILEFOLDER, DEFAULT_SORT_MEDIAFILEFOLDER);
    }

    public void setSortMediaFileFolder(boolean b) {
        setBoolean(KEY_SORT_MEDIAFILEFOLDER, b);
    }    
    
    public boolean isUsePremiumServices() {
        return getBoolean(KEY_USE_PREMIUM_SERVICES, DEFAULT_USE_PREMIUM_SERVICES);
    }

	public void setUsePremiumServices(boolean b) {
        setBoolean(KEY_USE_PREMIUM_SERVICES, b);
    }

    public boolean isShowGenericArtistArt() {
        return getBoolean(KEY_SHOW_GENERIC_ARTIST_ART, DEFAULT_SHOW_GENERIC_ARTIST_ART);
    }

	public void setShowGenericArtistArt(boolean b) {
        setBoolean(KEY_SHOW_GENERIC_ARTIST_ART, b);
    }	
	
    public int getLeftframeSize() {
        return Integer.valueOf(properties.getProperty(KEY_LEFTFRAME_SIZE, String.valueOf(DEFAULT_LEFTFRAME_SIZE)));
	}

    public void setLeftframeSize(int size) {
        setProperty(KEY_LEFTFRAME_SIZE, String.valueOf(size));
    }
    
    public boolean showIconHome() {
        return getBoolean(KEY_ICON_HOME, DEFAULT_ICON_HOME);
    }
    public void setshowIconHome(boolean b) {
        setBoolean(KEY_ICON_HOME, b);
    }    
    public boolean showIconArtist() {
        return getBoolean(KEY_ICON_ARTIST, DEFAULT_ICON_ARTIST);
    }
    public void setshowIconArtist(boolean b) {
        setBoolean(KEY_ICON_ARTIST, b);
    }    
    public boolean showIconPlaying() {
        return getBoolean(KEY_ICON_PLAYING, DEFAULT_ICON_PLAYING);
    }
    public void setshowIconPlaying(boolean b) {
        setBoolean(KEY_ICON_PLAYING, b);
    }
    public boolean showIconStarred() {
        return getBoolean(KEY_ICON_STARRED, DEFAULT_ICON_STARRED);
    }
    public void setshowIconStarred(boolean b) {
        setBoolean(KEY_ICON_STARRED, b);
    }        
    public boolean showIconRadio() {
        return getBoolean(KEY_ICON_RADIO, DEFAULT_ICON_RADIO);
    }
    public void setshowIconRadio(boolean b) {
        setBoolean(KEY_ICON_RADIO, b);
    }        
    public boolean showIconPodcast() {
        return getBoolean(KEY_ICON_PODAST, DEFAULT_ICON_PODAST);
    }
    public void setshowIconPodcast(boolean b) {
        setBoolean(KEY_ICON_PODAST, b);
    }        
    public boolean showIconSettings() {
        return getBoolean(KEY_ICON_SETTINGS, DEFAULT_ICON_SETTINGS);
    }
    public void setshowIconSettings(boolean b) {
        setBoolean(KEY_ICON_SETTINGS, b);
    }        
    public boolean showIconStatus() {
        return getBoolean(KEY_ICON_STATUS, DEFAULT_ICON_STATUS);
    }
    public void setshowIconStatus(boolean b) {
        setBoolean(KEY_ICON_STATUS, b);
    }        
    public boolean showIconSocial() {
        return getBoolean(KEY_ICON_SOCIAL, DEFAULT_ICON_SOCIAL);
    }
    public void setshowIconSocial(boolean b) {
        setBoolean(KEY_ICON_SOCIAL, b);
    }
    public boolean showIconHistory() {
        return getBoolean(KEY_ICON_HISTORY, DEFAULT_ICON_HISTORY);
    }
    public void setshowIconHistory(boolean b) {
        setBoolean(KEY_ICON_HISTORY, b);
    }        
    public boolean showIconStatistics() {
        return getBoolean(KEY_ICON_STATISTICS, DEFAULT_ICON_STATISTICS);
    }
    public void setshowIconStatistics(boolean b) {
        setBoolean(KEY_ICON_STATISTICS, b);
    }        
    public boolean showIconPlaylists() {
        return getBoolean(KEY_ICON_PLAYLISTS, DEFAULT_ICON_PLAYLISTS);
    }
    public void setshowIconPlaylists(boolean b) {
        setBoolean(KEY_ICON_PLAYLISTS, b);
    }        
    public boolean showIconPlaylistEditor() {
        return getBoolean(KEY_ICON_PLAYLIST_EDITOR, DEFAULT_ICON_PLAYLIST_EDITOR);
    }
    public void setshowIconPlaylistEditor(boolean b) {
        setBoolean(KEY_ICON_PLAYLIST_EDITOR, b);
    }        
    public boolean showIconMore() {
        return getBoolean(KEY_ICON_MORE, DEFAULT_ICON_MORE);
    }
    public void setshowIconMore(boolean b) {
        setBoolean(KEY_ICON_MORE, b);
    }        
    public boolean showIconAbout() {
        return getBoolean(KEY_ICON_ABOUT, DEFAULT_ICON_ABOUT);
    }
    public void setshowIconAbout(boolean b) {
        setBoolean(KEY_ICON_ABOUT, b);
    }  
    public boolean showIconGenre() {
        return getBoolean(KEY_ICON_GENRE, DEFAULT_ICON_GENRE);
    }
    public void setshowIconGenre(boolean b) {
        setBoolean(KEY_ICON_GENRE, b);
    }  
    public boolean showIconMoods() {
        return getBoolean(KEY_ICON_MOODS, DEFAULT_ICON_MOODS);
    }
    public void setshowIconMoods(boolean b) {
        setBoolean(KEY_ICON_MOODS, b);
    }      

    public boolean showIconCover() {
        return getBoolean(KEY_ICON_COVER, DEFAULT_ICON_COVER);
    }
    public void setshowIconCover(boolean b) {
        setBoolean(KEY_ICON_COVER, b);
    }      
    
    public boolean showIconAdmins() {
        return getBoolean(KEY_ICON_ADMINS, DEFAULT_ICON_ADMINS);
    }
    public void setshowIconAdmins(boolean b) {
        setBoolean(KEY_ICON_ADMINS, b);
    }      
    
    public boolean showHomeRandom() {
        return getBoolean(KEY_HOME_RANDOM, DEFAULT_HOME_RANDOM);
    }
    public void setHomeRandom(boolean b) {
        setBoolean(KEY_HOME_RANDOM, b);
    }      
	
    public boolean showHomeNewAdded() {
        return getBoolean(KEY_HOME_NEW_ADDED, DEFAULT_HOME_NEW_ADDED);
    }
    public void setHomeNewAdded(boolean b) {
        setBoolean(KEY_HOME_NEW_ADDED, b);
    } 

    public boolean showHomeHotRated() {
        return getBoolean(KEY_HOME_HOT_RATED, DEFAULT_HOME_HOT_RATED);
    }
    public void setHomeHotRated(boolean b) {
        setBoolean(KEY_HOME_HOT_RATED, b);
    }     
    
    public boolean showHomeAllArtist() {
        return getBoolean(KEY_HOME_ALLARTIST, DEFAULT_HOME_ALLARTIST);
    }
    public void setHomeAllArtist(boolean b) {
        setBoolean(KEY_HOME_ALLARTIST, b);
    }     

    public boolean showHomeStarredArtist() {
        return getBoolean(KEY_HOME_STARRED_ARTIST, DEFAULT_HOME_STARRED_ARTIST);
    }
    
    public void setHomeStarredArtist(boolean b) {
        setBoolean(KEY_HOME_STARRED_ARTIST, b);
    }     

    public boolean showHomeStarredAlbum() {
        return getBoolean(KEY_HOME_STARRED_ALBUM, DEFAULT_HOME_STARRED_ALBUM);
    }
    
    public void setHomeStarredAlbum(boolean b) {
        setBoolean(KEY_HOME_STARRED_ALBUM, b);
    }     

	public boolean showHomeAlbumTip() {
	    return getBoolean(KEY_HOME_ALBUMTIP, DEFAULT_HOME_ALBUMTIP);
	}
	
	public void setHomeAlbumTip(boolean b) {
	    setBoolean(KEY_HOME_ALBUMTIP, b);
	}  

	public boolean showHomeTopRated() {
	    return getBoolean(KEY_HOME_TOP_RATED, DEFAULT_HOME_TOP_RATED);
	}
	
	public void setHomeTopRated(boolean b) {
	    setBoolean(KEY_HOME_TOP_RATED, b);
	}  

	public boolean showHomeMostPlayed() {
	    return getBoolean(KEY_HOME_MOSTPLAYED, DEFAULT_HOME_MOSTPLAYED);
	}
	
	public void setHomeMostPlayed(boolean b) {
	    setBoolean(KEY_HOME_MOSTPLAYED, b);
	}
	
	public boolean showHomeLastPlayed() {
	    return getBoolean(KEY_HOME_LASTPLAYED, DEFAULT_HOME_LASTPLAYED);
	}
	
	public void setHomeLastPlayed(boolean b) {
	    setBoolean(KEY_HOME_LASTPLAYED, b);
	}

	public boolean showHomeDecade() {
	    return getBoolean(KEY_HOME_DECADE, DEFAULT_HOME_DECADE);
	}
	
	public void setHomeDecade(boolean b) {
	    setBoolean(KEY_HOME_DECADE, b);
	}

	public boolean showHomeGenre() {
	    return getBoolean(KEY_HOME_GENRE, DEFAULT_HOME_GENRE);
	}
	
	public void setHomeGenre(boolean b) {
	    setBoolean(KEY_HOME_GENRE, b);
	}

	public boolean showHomeName() {
	    return getBoolean(KEY_HOME_NAME, DEFAULT_HOME_GENRE);
	}
	
	public void setHomeName(boolean b) {
	    setBoolean(KEY_HOME_NAME, b);
	}
	
	public boolean showHomeTop100() {
	    return getBoolean(KEY_HOME_TOP100, DEFAULT_HOME_TOP100);
	}
	
	public void setHomeTop100(boolean b) {
	    setBoolean(KEY_HOME_TOP100, b);
	}
	
	public boolean showHomeNew100() {
	    return getBoolean(KEY_HOME_NEW100, DEFAULT_HOME_NEW100);
	}
	
	public void setHomeNew100(boolean b) {
	    setBoolean(KEY_HOME_NEW100, b);
	}
    
	public int getPlayqueueSize() {
        return Integer.valueOf(properties.getProperty(KEY_PLAYQUEUE_SIZE, String.valueOf(DEFAULT_PLAYQUEUE_SIZE)));
	}

    public void setPlayqueueSize(int size) {
        setProperty(KEY_PLAYQUEUE_SIZE, String.valueOf(size));
    }

	public MediaLibraryStatistics getMediaLibraryStatistics() {
        return MediaLibraryStatistics.parse(getString(KEY_MEDIA_LIBRARY_STATISTICS, DEFAULT_MEDIA_LIBRARY_STATISTICS));
    }

    public void setMediaLibraryStatistics(MediaLibraryStatistics statistics) {
        setString(KEY_MEDIA_LIBRARY_STATISTICS, statistics.format());
    }
    /**
     * Returns the locale (for language, date format etc).
     *
     * @return The locale.
     */
    public Locale getLocale() {
        String language = properties.getProperty(KEY_LOCALE_LANGUAGE, DEFAULT_LOCALE_LANGUAGE);
        String country = properties.getProperty(KEY_LOCALE_COUNTRY, DEFAULT_LOCALE_COUNTRY);
        String variant = properties.getProperty(KEY_LOCALE_VARIANT, DEFAULT_LOCALE_VARIANT);

        return new Locale(language, country, variant);
    }

    /**
     * Sets the locale (for language, date format etc.)
     *
     * @param locale The locale.
     */
    public void setLocale(Locale locale) {
        setProperty(KEY_LOCALE_LANGUAGE, locale.getLanguage());
        setProperty(KEY_LOCALE_COUNTRY, locale.getCountry());
        setProperty(KEY_LOCALE_VARIANT, locale.getVariant());
    }

    public String getListType() {
        return properties.getProperty(KEY_LISTTYPE, DEFAULT_LISTTYPE);
    }

    public void setListType(String listType) {
        setProperty(KEY_LISTTYPE, listType);
    }

	public void setNewaddedTimespan(String TimeSpan) {
        setProperty(KEY_NEWADDED_TIMESPAN, TimeSpan);
	}

	public String getNewaddedTimespan() {
		  return properties.getProperty(KEY_NEWADDED_TIMESPAN, DEFAULT_NEWADDED_TIMESPAN);
	}
	
	
	public boolean getPlayQueueResize() {
	      return getBoolean(KEY_PLAYQUEUE_RESIZE, DEFAULT_PLAYQUEUE_RESIZE);		
	}
    
	public void setPlayQueueResize(boolean ResizeEnabled) {
        setBoolean(KEY_PLAYQUEUE_RESIZE, ResizeEnabled);
	}

	public boolean getLeftFrameResize() {
	      return getBoolean(KEY_LEFTFRAME_RESIZE, DEFAULT_LEFTFRAME_RESIZE);		
	}
    
	public void setLeftFrameResize(boolean ResizeEnabled) {
        setBoolean(KEY_LEFTFRAME_RESIZE, ResizeEnabled);
	}
		

	public boolean getCustomScrollbar() {
	      return getBoolean(KEY_CUSTOMSCROLLBAR, DEFAULT_CUSTOMSCROLLBAR);		
	}
    
	public void setCustomScrollbar(boolean CustomScrollbarEnabled) {
        setBoolean(KEY_CUSTOMSCROLLBAR, CustomScrollbarEnabled);
	}

	public boolean getCustomAccordion() {
	      return getBoolean(KEY_CUSTOMACCORDION, DEFAULT_CUSTOMACCORDION);		
	}
  
	public void setCustomAccordion(boolean CustomAccordionEnabled) {
      setBoolean(KEY_CUSTOMACCORDION, CustomAccordionEnabled);
	}	
	
    /**
     * Returns the ID of the theme to use.
     *
     * @return The theme ID.
     */
    public String getThemeId() {
        return properties.getProperty(KEY_THEME_ID, DEFAULT_THEME_ID);
    }

    /**
     * Sets the ID of the theme to use.
     *
     * @param themeId The theme ID
     */
    public void setThemeId(String themeId) {
        setProperty(KEY_THEME_ID, themeId);
    }

    /**
     * Returns a list of available themes.
     *
     * @return A list of available themes.
     */
    public synchronized Theme[] getAvailableThemes() {
        if (themes == null) {
            themes = new ArrayList<Theme>();
            try {
                InputStream in = SettingsService.class.getResourceAsStream(THEMES_FILE);
                String[] lines = StringUtil.readLines(in);
                for (String line : lines) {
                    String[] elements = StringUtil.split(line);
                    if (elements.length == 2) {
                        themes.add(new Theme(elements[0], elements[1]));
                    } else {
                        LOG.warn("Failed to parse theme from line: [" + line + "].");
                    }
                }
            } catch (IOException x) {
                LOG.error("Failed to resolve list of themes.", x);
                themes.add(new Theme("default", "Subsonic default"));
            }
        }
        return themes.toArray(new Theme[themes.size()]);
    }

    /**
     * Returns a list of available locales.
     *
     * @return A list of available locales.
     */
    public synchronized Locale[] getAvailableLocales() {
        if (locales == null) {
            locales = new ArrayList<Locale>();
            try {
                InputStream in = SettingsService.class.getResourceAsStream(LOCALES_FILE);
                String[] lines = StringUtil.readLines(in);

                for (String line : lines) {
                    locales.add(parseLocale(line));
                }

            } catch (IOException x) {
                LOG.error("Failed to resolve list of locales.", x);
                locales.add(Locale.ENGLISH);
            }
        }
        return locales.toArray(new Locale[locales.size()]);
    }

    private Locale parseLocale(String line) {
        String[] s = line.split("_");
        String language = s[0];
        String country = "";
        String variant = "";

        if (s.length > 1) {
            country = s[1];
        }
        if (s.length > 2) {
            variant = s[2];
        }
        return new Locale(language, country, variant);
    }

    /**
     * Returns the "brand" name. Normally, this is just "Subsonic".
     *
     * @return The brand name.
     */
    public String getBrand() {
        return "Madsonic";
    }

    /**
     * Returns all music folders. Non-existing and disabled folders are not included.
     *
     * @return Possibly empty list of all music folders.
     */
    public List<MusicFolder> getAllMusicFolders() {
    	return getAllMusicFolders(false, false);
    }

    public List<MusicFolder> getAllMusicFolders(boolean includeDisabled, boolean includeNonExisting) {
        cachedMusicFolders = musicFolderDao.getAllMusicFolders();
        List<MusicFolder> result = new ArrayList<MusicFolder>(cachedMusicFolders.size());
        for (MusicFolder folder : cachedMusicFolders) {
            if ((includeDisabled || folder.isEnabled()) && (includeNonExisting || FileUtil.exists(folder.getPath()))) {
                result.add(folder);
            }
        }
        return result;
    }
	
    /**
     * Returns all music folders.
     *
     * @param includeDisabled Whether to include disabled folders.
     * @param includeNonExisting Whether to include non-existing folders.
     * @return Possibly empty list of all music folders.
     */
    public List<MusicFolder> getAllMusicFolders(boolean includeDisabled, boolean includeNonExisting, int user_group_id) {

    	cachedMusicFolders = null; // Turn of cache
   	 
    	if (cachedMusicFolders == null) {
    		
    		
    		//TODO:FOLDER
//            cachedMusicFolders = musicFolderDao.getAllMusicFolders(user_group_id, DEFAULT_GROUP_MUSIC);
            cachedMusicFolders = musicFolderDao.getAllMusicFolders(user_group_id);
        }
        
        List<MusicFolder> result = new ArrayList<MusicFolder>(cachedMusicFolders.size());
        for (MusicFolder folder : cachedMusicFolders) {
            if ((includeDisabled || folder.isEnabled()) && (includeNonExisting || FileUtil.exists(folder.getPath()))) {
                result.add(folder);
            }
        }
        return result;
    }

    public List<MusicFolder> getAllMusicFolders(int usergroupId, boolean sort) {
    	return getAllMusicFolders(usergroupId, sort, -1);
    }

    /**
     * Returns the music folder with the given group ID.
     *
     * @param usergroupId The ID.
     * @return The music folder with the given ID, or <code>null</code> if not found.
     */	
    public List<MusicFolder> getAllMusicFolders(int usergroupId, boolean sort, int groupby) {
    	 
    	 cachedMusicFolders = new ArrayList<MusicFolder>(); // Turn off cache
    	 
        if (cachedMusicFolders.size() < 1) {
        	if (groupby == DEFAULT_GROUP_ALL)  {cachedMusicFolders = musicFolderDao.getAllMusicFolders(usergroupId);}

        	if (groupby == DEFAULT_GROUP_MUSIC) {cachedMusicFolders = musicFolderDao.getAllMusicFolders(usergroupId, DEFAULT_GROUP_MUSIC);} 
        	if (groupby == DEFAULT_GROUP_VIDEO) {cachedMusicFolders = musicFolderDao.getAllMusicFolders(usergroupId, DEFAULT_GROUP_VIDEO);}
        	if (groupby == DEFAULT_GROUP_NONE)  {cachedMusicFolders = musicFolderDao.getAllMusicFolders(usergroupId, DEFAULT_GROUP_NONE );}
        }
        
        List<MusicFolder> result = new ArrayList<MusicFolder>(cachedMusicFolders.size());
        for (MusicFolder folder : cachedMusicFolders) {
            if ((false || folder.isEnabled()) && (false || FileUtil.exists(folder.getPath()))) {
                result.add(folder);
            }
        }
        if (sort){
        	
            // TODO: new musicFolder Sort
            Comparator<MusicFolder> comparator = new MusicFolderComparator();
            Set<MusicFolder> set = new TreeSet<MusicFolder>(comparator);
            set.addAll(result);
            result = new ArrayList<MusicFolder>(set);
        }
        return result;
	}    
    
    /**
     * Returns the music folder with the given ID.
     *
     * @param id The ID.
     * @return The music folder with the given ID, or <code>null</code> if not found.
     */
    public MusicFolder getMusicFolderById(Integer id) {
        List<MusicFolder> all = getAllMusicFolders();
        for (MusicFolder folder : all) {
            if (id.equals(folder.getId())) {
                return folder;
            }
        }
        return null;
    }

    /**
     * Creates a new music folder.
     *
     * @param musicFolder The music folder to create.
     */
    public void createMusicFolder(MusicFolder musicFolder) {
        musicFolderDao.createMusicFolder(musicFolder);
        LOG.info("## Created MediaFolder: " + musicFolder.getName());
        groupDao.insertMusicFolderAccess(musicFolderDao.getMusicFolderId(musicFolder.getPath().toString()));
        LOG.info("## Created Default Access for MediaFolder: " + musicFolder.getName());
        cachedMusicFolders = null; 
    }

   
    /**
     * Deletes the music folder with the given ID.
     *
     * @param id The ID of the music folder to delete.
     */
    public void deleteMusicFolder(Integer id) {
        musicFolderDao.deleteMusicFolder(id);
        cachedMusicFolders = null; 
    }

    /**
     * Updates the given music folder.
     *
     * @param musicFolder The music folder to update.
     */
    public void updateMusicFolder(MusicFolder musicFolder) {
        musicFolderDao.updateMusicFolder(musicFolder);
        cachedMusicFolders = null; 
    }

    /**
     * Returns all internet radio stations. Disabled stations are not returned.
     *
     * @return Possibly empty list of all internet radio stations.
     */
    public List<InternetRadio> getAllInternetRadios() {
        return getAllInternetRadios(false);
    }

    /**
     * Returns the internet radio station with the given ID.
     *
     * @param id The ID.
     * @return The internet radio station with the given ID, or <code>null</code> if not found.
     */
    public InternetRadio getInternetRadioById(Integer id) {
        for (InternetRadio radio : getAllInternetRadios()) {
            if (id.equals(radio.getId())) {
                return radio;
            }
        }
        return null;
    }

    /**
     * Returns all internet radio stations.
     *
     * @param includeAll Whether disabled stations should be included.
     * @return Possibly empty list of all internet radio stations.
     */
    public List<InternetRadio> getAllInternetRadios(boolean includeAll) {
        List<InternetRadio> all = internetRadioDao.getAllInternetRadios();
        List<InternetRadio> result = new ArrayList<InternetRadio>(all.size());
        for (InternetRadio folder : all) {
            if (includeAll || folder.isEnabled()) {
                result.add(folder);
            }
        }
        return result;
    }

    /**
     * Creates a new internet radio station.
     *
     * @param radio The internet radio station to create.
     */
    public void createInternetRadio(InternetRadio radio) {
        internetRadioDao.createInternetRadio(radio);
    }

    /**
     * Deletes the internet radio station with the given ID.
     *
     * @param id The internet radio station ID.
     */
    public void deleteInternetRadio(Integer id) {
        internetRadioDao.deleteInternetRadio(id);
    }

    /**
     * Updates the given internet radio station.
     *
     * @param radio The internet radio station to update.
     */
    public void updateInternetRadio(InternetRadio radio) {
        internetRadioDao.updateInternetRadio(radio);
    }

    /**
     * Returns settings for the given user.
     *
     * @param username The username.
     * @return User-specific settings. Never <code>null</code>.
     */
    public UserSettings getUserSettings(String username) {
        UserSettings settings = userDao.getUserSettings(username);
        return settings == null ? createDefaultUserSettings(username) : settings;
    }

    public UserSettings getDefaultUserSettings(String username) {
        UserSettings settings = userDao.getUserSettings(username);
        return settings == null ? cloneDefaultUserSettings(username) : settings;
    }    
    
    public UserSettings cloneDefaultUserSettings(String username) {
        UserSettings settings = new UserSettings(username);
        UserSettings defaultSettings = userDao.getUserSettings("default");
        
        settings.setFinalVersionNotificationEnabled(defaultSettings.isFinalVersionNotificationEnabled());
        settings.setBetaVersionNotificationEnabled(defaultSettings.isBetaVersionNotificationEnabled());
        settings.setShowNowPlayingEnabled(defaultSettings.isShowNowPlayingEnabled());
        settings.setShowChatEnabled(defaultSettings.isShowChatEnabled());
        settings.setAutoHideChat(defaultSettings.isAutoHideChat());
        settings.setPartyModeEnabled(defaultSettings.isPartyModeEnabled());
        settings.setNowPlayingAllowed(defaultSettings.isNowPlayingAllowed());
        settings.setLastFmEnabled(defaultSettings.isLastFmEnabled());
        settings.setLastFmUsername(defaultSettings.getLastFmUsername());
        settings.setLastFmPassword(defaultSettings.getLastFmPassword());
        settings.setChanged(new Date());
        settings.setLocale(defaultSettings.getLocale());
        settings.setThemeId(defaultSettings.getThemeId());
        settings.setListType(defaultSettings.getListType());
        settings.setListRows(defaultSettings.getListRows());
        settings.setListColumns(defaultSettings.getListColumns());
		settings.setPlayQueueResize(defaultSettings.getPlayQueueResize());
		settings.setLeftFrameResize(defaultSettings.getLeftFrameResize());
		settings.setCustomScrollbarEnabled(defaultSettings.isCustomScrollbarEnabled());
		settings.setCustomAccordionEnabled(defaultSettings.isCustomAccordionEnabled());
				
        UserSettings.Visibility playlist = settings.getPlaylistVisibility();
        
        playlist.setCaptionCutoff(defaultSettings.getPlaylistVisibility().getCaptionCutoff());
        playlist.setArtistVisible(defaultSettings.getPlaylistVisibility().isArtistVisible());
        playlist.setAlbumVisible(defaultSettings.getPlaylistVisibility().isAlbumVisible());
        playlist.setGenreVisible(defaultSettings.getPlaylistVisibility().isGenreVisible());
        playlist.setMoodVisible(defaultSettings.getPlaylistVisibility().isMoodVisible());
        playlist.setYearVisible(defaultSettings.getPlaylistVisibility().isYearVisible());
        playlist.setDurationVisible(defaultSettings.getPlaylistVisibility().isDurationVisible());
        playlist.setBitRateVisible(defaultSettings.getPlaylistVisibility().isBitRateVisible());
        playlist.setFormatVisible(defaultSettings.getPlaylistVisibility().isFormatVisible());
        playlist.setFileSizeVisible(defaultSettings.getPlaylistVisibility().isFileSizeVisible());

        UserSettings.Visibility main = settings.getMainVisibility();
        main.setCaptionCutoff(defaultSettings.getMainVisibility().getCaptionCutoff());
        main.setTrackNumberVisible(defaultSettings.getMainVisibility().isTrackNumberVisible());
        main.setArtistVisible(defaultSettings.getMainVisibility().isArtistVisible());
        main.setDurationVisible(defaultSettings.getMainVisibility().isDurationVisible());
        main.setGenreVisible(defaultSettings.getMainVisibility().isGenreVisible());
        main.setMoodVisible(defaultSettings.getMainVisibility().isMoodVisible());

        return settings;
    }
    
    
    private UserSettings createDefaultUserSettings(String username) {
        UserSettings settings = new UserSettings(username);
        settings.setFinalVersionNotificationEnabled(true);
        settings.setBetaVersionNotificationEnabled(true);
        settings.setShowNowPlayingEnabled(false);
        settings.setShowChatEnabled(false);
        settings.setAutoHideChat(false);
        settings.setPartyModeEnabled(false);
        settings.setNowPlayingAllowed(true);
        settings.setLastFmEnabled(false);
        settings.setLastFmUsername(null);
        settings.setLastFmPassword(null);
        settings.setChanged(new Date());
        settings.setListType(getListType());
        settings.setListRows(2);
        settings.setListColumns(6);
		settings.setPlayQueueResize(false);
		settings.setLeftFrameResize(false);
		settings.setCustomScrollbarEnabled(true);
		settings.setCustomAccordionEnabled(false);
		
        UserSettings.Visibility playlist = settings.getPlaylistVisibility();
        playlist.setCaptionCutoff(50);
        playlist.setArtistVisible(true);
        playlist.setAlbumVisible(true);
        playlist.setGenreVisible(true);
        playlist.setMoodVisible(true);
        playlist.setYearVisible(true);
        playlist.setDurationVisible(true);
        playlist.setBitRateVisible(true);
        playlist.setFormatVisible(true);
        playlist.setFileSizeVisible(true);

        UserSettings.Visibility main = settings.getMainVisibility();
        main.setCaptionCutoff(60);
        main.setTrackNumberVisible(false);
        main.setArtistVisible(true);
        main.setDurationVisible(true);
        main.setGenreVisible(true);
        main.setMoodVisible(true);
        
        UserSettings.ButtonVisibility button = settings.getButtonVisibility();
        button.setRankVisible(true);
        button.setStarredVisible(true);
        button.setPlayVisible(true);
        button.setPlayAddVisible(false);
        button.setPlayMoreVisible(false);
        button.setAddContextVisible(true);
        button.setAddNextVisible(false);
        button.setAddLastVisible(false);
        button.setDownloadVisible(true);
        button.setYoutubeVisible(false);

        return settings;
    }

    /**
     * Updates settings for the given username.
     *
     * @param settings The user-specific settings.
     */
    public void updateUserSettings(UserSettings settings) {
        userDao.updateUserSettings(settings);
    }

    /**
     * Returns all system avatars.
     *
     * @return All system avatars.
     */
    public List<Avatar> getAllSystemAvatars() {
        return avatarDao.getAllSystemAvatars();
    }

    /**
     * Returns the system avatar with the given ID.
     *
     * @param id The system avatar ID.
     * @return The avatar or <code>null</code> if not found.
     */
    public Avatar getSystemAvatar(int id) {
        return avatarDao.getSystemAvatar(id);
    }

    /**
     * Returns the custom avatar for the given user.
     *
     * @param username The username.
     * @return The avatar or <code>null</code> if not found.
     */
    public Avatar getCustomAvatar(String username) {
        return avatarDao.getCustomAvatar(username);
    }

    /**
     * Sets the custom avatar for the given user.
     *
     * @param avatar   The avatar, or <code>null</code> to remove the avatar.
     * @param username The username.
     */
    public void setCustomAvatar(Avatar avatar, String username) {
        avatarDao.setCustomAvatar(avatar, username);
    }

    public boolean isDlnaEnabled() {
        return getBoolean(KEY_DLNA_ENABLED, DEFAULT_DLNA_ENABLED);
    }

    public void setDlnaEnabled(boolean dlnaEnabled) {
        setBoolean(KEY_DLNA_ENABLED, dlnaEnabled);
    }


    public String getDlnaServerName() {
        return getString(KEY_DLNA_SERVER_NAME, DEFAULT_DLNA_SERVER_NAME);
    }

    public void setDlnaServerName(String dlnaServerName) {
        setString(KEY_DLNA_SERVER_NAME, dlnaServerName);
    }

    public boolean isFolderParsingEnabled() {
        return getBoolean(KEY_FOLDERPARSING_ENABLED, DEFAULT_FOLDERPARSING_ENABLED);
    }

    public void setFolderParsingEnabled(boolean folderParsingEnabledEnabled) {
        setBoolean(KEY_FOLDERPARSING_ENABLED, folderParsingEnabledEnabled);
    }    
    

    public boolean isAlbumSetParsingEnabled() {
        return getBoolean(KEY_ALBUMSETPARSING_ENABLED, DEFAULT_ALBUMSETPARSING_ENABLED);
    }

    public void setAlbumSetParsingEnabled(boolean albumSetParsingEnabledEnabled) {
        setBoolean(KEY_ALBUMSETPARSING_ENABLED, albumSetParsingEnabledEnabled);
    }        

    public boolean isLogfileReverse() {
        return getBoolean(KEY_LOGFILE_REVERSE, DEFAULT_LOGFILE_REVERSE);
    }

    public void setLogfileReverse(boolean logfileReverse) {
        setBoolean(KEY_LOGFILE_REVERSE, logfileReverse);
    }        
    
    public String getLogLevel() {
        return properties.getProperty(KEY_LOGFILE_LEVEL, DEFAULT_LOGFILE_LEVEL);
    }
    
    public static String getLogfileLevel() {
        return properties.getProperty(KEY_LOGFILE_LEVEL, DEFAULT_LOGFILE_LEVEL);
    }

    public void setLogfileLevel(String level) {
        setProperty(KEY_LOGFILE_LEVEL, level);
    }
    
    public String getLocalIpAddress() {
        return localIpAddress;
    }
	
    private void setProperty(String key, String value) {
        if (value == null) {
            properties.remove(key);
        } else {
            properties.setProperty(key, value);
        }
    }

    private String[] toStringArray(String s) {
        List<String> result = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(s, " ");
        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken());
        }

        return result.toArray(new String[result.size()]);
    }

    private void validateLicense() {
        String email = getLicenseEmail();
        Date date = getLicenseDate();

        if (email == null || date == null) {
            licenseValidated = false;
            return;
        }

        licenseValidated = true;

        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 120000);
        HttpConnectionParams.setSoTimeout(client.getParams(), 120000);
        HttpGet method = new HttpGet("http://subsonic.org/backend/validateLicense.view" + "?email=" + StringUtil.urlEncode(email) +
                "&date=" + date.getTime() + "&version=" + versionService.getLocalVersion());
        try {
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String content = client.execute(method, responseHandler);
            licenseValidated = content != null && content.contains("true");
            if (!licenseValidated) {
                LOG.warn("License key is not valid.");
            }
            String[] lines = StringUtils.split(content);
            if (lines.length > 1) {
                licenseExpires = new Date(Long.parseLong(lines[1]));
            }

        } catch (Throwable x) {
            LOG.warn("Failed to validate license.", x);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    public void validateLicenseAsync() {
        new Thread() {
            @Override
            public void run() {
                validateLicense();
            }
        }.start();
    }

    private void scheduleLocalIpAddressLookup() {
        Runnable task = new Runnable() {
            public void run() {
                localIpAddress = Util.getLocalIpAddress();
            }
        };
        executor.scheduleWithFixedDelay(task, 0L, LOCAL_IP_LOOKUP_DELAY_SECONDS, TimeUnit.SECONDS);
    }


    public void setInternetRadioDao(InternetRadioDao internetRadioDao) {
        this.internetRadioDao = internetRadioDao;
    }

    public void setMusicFolderDao(MusicFolderDao musicFolderDao) {
        this.musicFolderDao = musicFolderDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setAvatarDao(AvatarDao avatarDao) {
        this.avatarDao = avatarDao;
    }

    public void setVersionService(VersionService versionService) {
        this.versionService = versionService;
    }

	public GroupDao getGroupDao() {
		return groupDao;
	}    
    
	public void setGroupDao(GroupDao groupDao) {
		this.groupDao = groupDao;
	}

	public static void setSubsonicUpload(File subsonicUpload) {
		SettingsService.madsonicUpload = subsonicUpload;
	}

	public void setLastFMResultSize(int size) {
	    setProperty(KEY_LASTFM_RESULT_SIZE, "" + size);	
	}
	
	public int getLastFMResultSize() {
		  return Integer.parseInt(properties.getProperty(KEY_LASTFM_RESULT_SIZE, "" + DEFAULT_LASTFM_RESULT_SIZE));
	}
}
