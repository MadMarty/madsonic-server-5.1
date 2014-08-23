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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import org.madsonic.Logger;
import org.madsonic.ajax.ChatService;
import org.madsonic.ajax.LyricsInfo;
import org.madsonic.ajax.LyricsService;
import org.madsonic.ajax.PlayQueueService;
import org.madsonic.command.UserSettingsCommand;
import org.madsonic.controller.ShareSettingsController.ShareInfo;
import org.madsonic.dao.AlbumDao;
import org.madsonic.dao.ArtistDao;
import org.madsonic.dao.BookmarkDao;
import org.madsonic.dao.MediaFileDao;
import org.madsonic.domain.Album;
import org.madsonic.domain.Artist;
import org.madsonic.domain.Bookmark;
import org.madsonic.domain.Genre;
import org.madsonic.domain.GenreSearchCriteria;
import org.madsonic.domain.InternetRadio;
import org.madsonic.domain.MediaFile;
import org.madsonic.domain.MoodsSearchCriteria;
import org.madsonic.domain.MultiSearchCriteria;
import org.madsonic.domain.MusicFolder;
import org.madsonic.domain.MusicIndex;
import org.madsonic.domain.PlayQueue;
import org.madsonic.domain.Player;
import org.madsonic.domain.PlayerTechnology;
import org.madsonic.domain.Playlist;
import org.madsonic.domain.PodcastChannel;
import org.madsonic.domain.PodcastEpisode;
import org.madsonic.domain.RandomSearchCriteria;
import org.madsonic.domain.SearchCriteria;
import org.madsonic.domain.SearchResult;
import org.madsonic.domain.Share;
import org.madsonic.domain.TranscodeScheme;
import org.madsonic.domain.TransferStatus;
import org.madsonic.domain.User;
import org.madsonic.domain.UserSettings;
import org.madsonic.service.AudioScrobblerService;
import org.madsonic.service.HotService;
import org.madsonic.service.JukeboxService;
import org.madsonic.service.LastFMService;
import org.madsonic.service.MediaFileService;
import org.madsonic.service.MediaScannerService;
import org.madsonic.service.MusicIndexService;
import org.madsonic.service.PlayerService;
import org.madsonic.service.PlaylistService;
import org.madsonic.service.PodcastService;
import org.madsonic.service.RatingService;
import org.madsonic.service.SearchService;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;
import org.madsonic.service.ShareService;
import org.madsonic.service.StatusService;
import org.madsonic.service.TranscodingService;
import org.madsonic.util.Pair;
import org.madsonic.util.StringUtil;
import org.madsonic.util.XMLBuilder;
import static org.madsonic.domain.MediaFile.MediaType.MUSIC;

import java.util.concurrent.ConcurrentHashMap;

import static org.madsonic.security.RESTRequestParameterProcessingFilter.decrypt;
import static org.madsonic.util.XMLBuilder.Attribute;
import static org.madsonic.util.XMLBuilder.AttributeSet;
import static org.springframework.web.bind.ServletRequestUtils.*;

/**
 * Multi-controller used for the REST API.
 * <p/>
 * For documentation, please refer to api.jsp.
 * <p/>
 * Note: Exceptions thrown from the methods are intercepted by RESTFilter.
 *
 * @author Sindre Mehus
 */
public class RESTController extends MultiActionController {

	private static final Logger LOG = Logger.getLogger(RESTController.class);

	private SettingsService settingsService;
	private SecurityService securityService;
	private PlayerService playerService;

	private MediaScannerService mediaScannerService;
	private MediaFileService mediaFileService;
	private MusicIndexService musicIndexService;
	private TranscodingService transcodingService;
	private DownloadController downloadController;
	private CoverArtController coverArtController;
	private AvatarController avatarController;
	private UserSettingsController userSettingsController;
	private LeftController leftController;
	private HomeController homeController;
	private StatusService statusService;
	private StreamController streamController;
	private HLSController hlsController;
	private ShareService shareService;
	private PlaylistService playlistService;
	private ChatService chatService;
	private LyricsService lyricsService;
	private PlayQueueService playQueueService;
	private JukeboxService jukeboxService;
	private AudioScrobblerService audioScrobblerService;
	private PodcastService podcastService;
	private RatingService ratingService;
	private SearchService searchService;
	private HotService hotService;
	private LastFMService lastFMService;    
	private MediaFileDao mediaFileDao;
	private ArtistDao artistDao;
	private AlbumDao albumDao;
	private BookmarkDao bookmarkDao;

	private final Map<BookmarkKey, Bookmark> bookmarkCache = new ConcurrentHashMap<BookmarkKey, Bookmark>();

	public void init() {
		refreshBookmarkCache();
	}

	private void refreshBookmarkCache() {
		bookmarkCache.clear();
		for (Bookmark bookmark : bookmarkDao.getBookmarks()) {
			bookmarkCache.put(BookmarkKey.forBookmark(bookmark), bookmark);
		}
	}

	public void ping(HttpServletRequest request, HttpServletResponse response) throws Exception {
		XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
		response.getWriter().print(builder);
	}

	public void getLicense(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		XMLBuilder builder = createXMLBuilder(request, response, true);

		String email = settingsService.getLicenseEmail();
		String key = settingsService.getLicenseCode();
		Date date = settingsService.getLicenseDate();
		boolean valid = settingsService.isLicenseValid();

		AttributeSet attributes = new AttributeSet();
		attributes.add("valid", valid);
		if (valid) {
			attributes.add("email", email);
			attributes.add("key", key);
			attributes.add("date", StringUtil.toISO8601(date));
		}

		builder.add("license", attributes, true);
		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getMusicFolders(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		XMLBuilder builder = createXMLBuilder(request, response, true);
		builder.add("musicFolders", false);
		int userGroupId = securityService.getCurrentUserGroupId(request);


		for (MusicFolder musicFolder : settingsService.getAllMusicFolders(userGroupId, settingsService.isSortMediaFileFolder())) {
			AttributeSet attributes = new AttributeSet();
			attributes.add("id", musicFolder.getId());
			attributes.add("name", musicFolder.getName());
			builder.add("musicFolder", attributes, true);
		}
		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getIndexes(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		XMLBuilder builder = createXMLBuilder(request, response, true);

		String username = securityService.getCurrentUser(request).getUsername();
		int userGroupId = securityService.getCurrentUserGroupId(request);

		long ifModifiedSince = getLongParameter(request, "ifModifiedSince", 0L);
		long lastModified = leftController.getLastModified(request);

		if (lastModified <= ifModifiedSince) {
			builder.endAll();
			response.getWriter().print(builder);
			return;
		}

		builder.add("indexes", false, new Attribute("lastModified", lastModified),
				new Attribute("ignoredArticles", settingsService.getIgnoredArticles()));
		List<MusicFolder> musicFolders = settingsService.getAllMusicFolders(userGroupId, settingsService.isSortMediaFileFolder());
		Integer musicFolderId = getIntParameter(request, "musicFolderId");
		if (musicFolderId != null) {
			for (MusicFolder musicFolder : musicFolders) {
				if (musicFolderId.equals(musicFolder.getId())) {
					musicFolders = Arrays.asList(musicFolder);
					break;
				}
			}
		}

		List<MediaFile> shortcuts = leftController.getShortcuts(musicFolders, settingsService.getShortcutsAsArray());
		for (MediaFile shortcut : shortcuts) {
			builder.add("shortcut", createAttributesForArtist(shortcut, username), true);
		}

		SortedMap<MusicIndex, SortedSet<MusicIndex.SortableArtistforGenre>> indexedArtists =
				leftController.getMusicFolderContent(musicFolders, null, "1", false).getIndexedArtists();

		for (Map.Entry<MusicIndex, SortedSet<MusicIndex.SortableArtistforGenre>> entry : indexedArtists.entrySet()) {
			builder.add("index", "name", entry.getKey().getIndex(), false);

			for (MusicIndex.SortableArtistforGenre artist : entry.getValue()) {
				for (MediaFile mediaFile : artist.getMediaFiles()) {
					if (mediaFile.isDirectory()) {
						Date starredDate = mediaFileDao.getMediaFileStarredDate(mediaFile.getId(), username);
						builder.add("artist", true,
								new Attribute("name", artist.getName()),
								new Attribute("id", mediaFile.getId()),
								new Attribute("starred", StringUtil.toISO8601(starredDate)));
					}
				}
			}
			builder.end();
		}

		// Add children
		Player player = playerService.getPlayer(request, response);
		List<MediaFile> singleSongs = leftController.getSingleSongs(musicFolders, false);

		for (MediaFile singleSong : singleSongs) {
			builder.add("child", createAttributesForMediaFile(player, singleSong, username), true);
		}

		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getGenres(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		XMLBuilder builder = createXMLBuilder(request, response, true);

		builder.add("genres", false);

		for (Genre genre : mediaFileDao.getGenres()) {
			
			String genreName = StringEscapeUtils.escapeXml(genre.getName().toString());
			
			AttributeSet attributes = new AttributeSet();
			attributes.add("songCount", genre.getSongCount());
			attributes.add("albumCount", genre.getAlbumCount());
			attributes.add("artistCount", genre.getArtistCount());
			
			builder.add("genre", (Iterable<Attribute>) attributes, genreName, true);
		}
		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getArtistGenres(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		XMLBuilder builder = createXMLBuilder(request, response, true);

		builder.add("artistsgenres", false);

		for (String genre : mediaFileDao.getArtistGenres()) {
			genre = StringEscapeUtils.escapeXml(genre);
			builder.add("genre", (Iterable<Attribute>) null, genre, true);
		}
		builder.endAll();
		response.getWriter().print(builder);
	}    

	public void getArtistsByGenre(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);

        UserSettings userSettings = settingsService.getUserSettings(username);
        int userGroupId = securityService.getCurrentUserGroupId(request);
		
		XMLBuilder builder = createXMLBuilder(request, response, true);
		builder.add("artistsByGenre", false);

		String genre = getRequiredStringParameter(request, "genre");

		int offset = getIntParameter(request, "offset", 0);
		int count = getIntParameter(request, "count", 10);
		count = Math.max(0, Math.min(count, 500));

		for (MediaFile mediaFile : mediaFileDao.getArtistsByGenre(genre, offset, count, userGroupId )) {
			AttributeSet attributes = createAttributesForMediaFile(player, mediaFile, username);
			builder.add("song", attributes, true);
		}
		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getSongsByGenre(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);

		XMLBuilder builder = createXMLBuilder(request, response, true);
		builder.add("songsByGenre", false);

		String genre = getRequiredStringParameter(request, "genre");
		int offset = getIntParameter(request, "offset", 0);
		int count = getIntParameter(request, "count", 10);
		count = Math.max(0, Math.min(count, 500));

		for (MediaFile mediaFile : mediaFileDao.getSongsByGenre(genre, offset, count)) {
			AttributeSet attributes = createAttributesForMediaFile(player, mediaFile, username);
			builder.add("song", attributes, true);
		}
		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getArtists(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		XMLBuilder builder = createXMLBuilder(request, response, true);
		String username = securityService.getCurrentUsername(request);

		builder.add("artists", "ignoredArticles", settingsService.getIgnoredArticles(), false);

		List<Artist> artists = artistDao.getAlphabetialArtists(0, Integer.MAX_VALUE);
		SortedMap<MusicIndex, SortedSet<MusicIndex.SortableArtistWithArtist>> indexedArtists = musicIndexService.getIndexedArtists(artists, 1); //TODO: fixed Default Index 
		for (Map.Entry<MusicIndex, SortedSet<MusicIndex.SortableArtistWithArtist>> entry : indexedArtists.entrySet()) {

			builder.add("index", "name", entry.getKey().getIndex(), false);
			for (MusicIndex.SortableArtistWithArtist sortableArtist : entry.getValue()) {
				AttributeSet attributes = createAttributesForArtist(sortableArtist.getArtist(), username);
				builder.add("artist", attributes, true);
			}
			builder.end();
		}

		builder.endAll();
		response.getWriter().print(builder);
	}

	private AttributeSet createAttributesForArtist(Artist artist, String username) {
		AttributeSet attributes = new AttributeSet();
		attributes.add("id", artist.getId());
		attributes.add("name", artist.getName());
		attributes.add("genre", artist.getGenre());
		if (artist.getCoverArtPath() != null) {
			attributes.add("coverArt", CoverArtController.ARTIST_COVERART_PREFIX + artist.getId());
		}
		attributes.add("albumCount", artist.getAlbumCount());
		attributes.add("starred", StringUtil.toISO8601(artistDao.getArtistStarredDate(artist.getId(), username)));
		return attributes;
	}

	private AttributeSet createAttributesForArtist(MediaFile artist, String username) {
		AttributeSet attributes = new AttributeSet();
		attributes.add("id", artist.getId());
		attributes.add("name", artist.getName());
		attributes.add("starred", StringUtil.toISO8601(mediaFileDao.getMediaFileStarredDate(artist.getId(), username)));
		return attributes;
	}

	public void getArtist(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		XMLBuilder builder = createXMLBuilder(request, response, true);

		String username = securityService.getCurrentUsername(request);
		int id = getRequiredIntParameter(request, "id");
		Artist artist = artistDao.getArtist(id);
		if (artist == null) {
			error(request, response, ErrorCode.NOT_FOUND, "Artist not found.");
			return;
		}

		builder.add("artist", createAttributesForArtist(artist, username), false);
		for (Album album : albumDao.getAlbumsForArtist(artist.getName())) {
			builder.add("album", createAttributesForAlbum(album, username), true);
		}

		builder.endAll();
		response.getWriter().print(builder);
	}

	private AttributeSet createAttributesForAlbum(Album album, String username) {
		AttributeSet attributes;
		attributes = new AttributeSet();
		attributes.add("id", album.getId());
		attributes.add("name", album.getName());
		attributes.add("artist", album.getArtist());
		if (album.getArtist() != null) {
			Artist artist = artistDao.getArtist(album.getArtist());
			if (artist != null) {
				attributes.add("artistId", artist.getId());
			}
		}
		if (album.getCoverArtPath() != null) {
			attributes.add("coverArt", CoverArtController.ALBUM_COVERART_PREFIX + album.getId());
		}
		attributes.add("songCount", album.getSongCount());
		attributes.add("duration", album.getDurationSeconds());
		attributes.add("created", StringUtil.toISO8601(album.getCreated()));
		attributes.add("starred", StringUtil.toISO8601(albumDao.getAlbumStarredDate(album.getId(), username)));
		attributes.add("year", album.getYear());
		attributes.add("genre", album.getGenre());

		return attributes;
	}

	private AttributeSet createAttributesForPlaylist(Playlist playlist) {
		AttributeSet attributes;
		attributes = new AttributeSet();
		attributes.add("id", playlist.getId());
		attributes.add("name", playlist.getName());
		attributes.add("comment", playlist.getComment());
		attributes.add("owner", playlist.getUsername());
		attributes.add("public", playlist.isPublic());
		attributes.add("songCount", playlist.getFileCount());
		attributes.add("duration", playlist.getDurationSeconds());
		attributes.add("created", StringUtil.toISO8601(playlist.getCreated()));
		return attributes;
	}

	public void getAlbum(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);
		XMLBuilder builder = createXMLBuilder(request, response, true);

		int id = getRequiredIntParameter(request, "id");
		Album album = albumDao.getAlbum(id);
		if (album == null) {
			error(request, response, ErrorCode.NOT_FOUND, "Album not found.");
			return;
		}

		builder.add("album", createAttributesForAlbum(album, username), false);

		//TODO:REST albumname
		for (MediaFile mediaFile : mediaFileDao.getSongsForAlbum(album.getArtist(), album.getNameid3())) {
			builder.add("song", createAttributesForMediaFile(player, mediaFile, username), true);
		}

		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getSong(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);
		XMLBuilder builder = createXMLBuilder(request, response, true);

		int id = getRequiredIntParameter(request, "id");
		MediaFile song = mediaFileDao.getMediaFile(id);
		if (song == null || song.isDirectory()) {
			error(request, response, ErrorCode.NOT_FOUND, "Song not found.");
			return;
		}
		builder.add("song", createAttributesForMediaFile(player, song, username), true);

		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getMusicDirectory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);
		int userGroupId = securityService.getCurrentUserGroupId(request);

		int id = getRequiredIntParameter(request, "id");
		MediaFile dir = mediaFileService.getMediaFile(id, userGroupId);
		if (dir == null) {
			error(request, response, ErrorCode.NOT_FOUND, "Directory not found");
			return;
		}

		MediaFile parent = mediaFileService.getParentOf(dir);
		AttributeSet attributes = new AttributeSet();
		attributes.add("id", id);
		try {
			if (!mediaFileService.isRoot(parent)) {
				attributes.add("parent", parent.getId());
			}
		} catch (SecurityException x) {
			// Ignored.
		}
		attributes.add("name", dir.getName());
		attributes.add("starred", StringUtil.toISO8601(mediaFileDao.getMediaFileStarredDate(id, username)));

		XMLBuilder builder = createXMLBuilder(request, response, true);
		builder.add("directory", attributes, false);

		for (MediaFile child : mediaFileService.getChildrenOf(dir, true, true, true)) {
			attributes = createAttributesForMediaFile(player, child, username);
			builder.add("child", attributes, true);
		}
		builder.endAll();
		response.getWriter().print(builder);
	}

	@Deprecated
	public void search(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		XMLBuilder builder = createXMLBuilder(request, response, true);
		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);
		int userGroupId = securityService.getCurrentUserGroupId(request);

		String any = request.getParameter("any");
		String artist = request.getParameter("artist");
		String album = request.getParameter("album");
		String title = request.getParameter("title");

		StringBuilder query = new StringBuilder();
		if (any != null) {
			query.append(any).append(" ");
		}
		if (artist != null) {
			query.append(artist).append(" ");
		}
		if (album != null) {
			query.append(album).append(" ");
		}
		if (title != null) {
			query.append(title);
		}

		SearchCriteria criteria = new SearchCriteria();
		criteria.setQuery(query.toString().trim());
		criteria.setCount(getIntParameter(request, "count", 20));
		criteria.setOffset(getIntParameter(request, "offset", 0));

		SearchResult result = searchService.search(criteria, SearchService.IndexType.SONG, userGroupId);
		builder.add("searchResult", false,
				new Attribute("offset", result.getOffset()),
				new Attribute("totalHits", result.getTotalHits()));

		for (MediaFile mediaFile : result.getMediaFiles()) {
			AttributeSet attributes = createAttributesForMediaFile(player, mediaFile, username);
			builder.add("match", attributes, true);
		}
		builder.endAll();
		response.getWriter().print(builder);
	}

	public void search2(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		XMLBuilder builder = createXMLBuilder(request, response, true);
		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);
		int userGroupId = securityService.getCurrentUserGroupId(request);

		builder.add("searchResult2", false);

		String query = request.getParameter("query");
		SearchCriteria criteria = new SearchCriteria();
		criteria.setQuery(StringUtils.trimToEmpty(query));
		criteria.setCount(getIntParameter(request, "artistCount", 20));
		criteria.setOffset(getIntParameter(request, "artistOffset", 0));
		SearchResult artists = searchService.search(criteria, SearchService.IndexType.ARTIST, userGroupId);
		for (MediaFile mediaFile : artists.getMediaFiles()) {
			builder.add("artist", createAttributesForArtist(mediaFile, username), true);
		}

		criteria.setCount(getIntParameter(request, "albumCount", 20));
		criteria.setOffset(getIntParameter(request, "albumOffset", 0));
		SearchResult albums = searchService.search(criteria, SearchService.IndexType.ALBUM, userGroupId);
		for (MediaFile mediaFile : albums.getMediaFiles()) {
			AttributeSet attributes = createAttributesForMediaFile(player, mediaFile, username);
			builder.add("album", attributes, true);
		}

		criteria.setCount(getIntParameter(request, "songCount", 20));
		criteria.setOffset(getIntParameter(request, "songOffset", 0));
		SearchResult songs = searchService.search(criteria, SearchService.IndexType.SONG, userGroupId);
		for (MediaFile mediaFile : songs.getMediaFiles()) {
			AttributeSet attributes = createAttributesForMediaFile(player, mediaFile, username);
			builder.add("song", attributes, true);
		}

		builder.endAll();
		response.getWriter().print(builder);
	}

	public void search3(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		XMLBuilder builder = createXMLBuilder(request, response, true);
		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);
		int userGroupId = securityService.getCurrentUserGroupId(request);

		builder.add("searchResult3", false);

		String query = request.getParameter("query");
		SearchCriteria criteria = new SearchCriteria();
		criteria.setQuery(StringUtils.trimToEmpty(query));
		criteria.setCount(getIntParameter(request, "artistCount", 20));
		criteria.setOffset(getIntParameter(request, "artistOffset", 0));
		SearchResult searchResult = searchService.search(criteria, SearchService.IndexType.ARTIST_ID3, userGroupId);
		for (Artist artist : searchResult.getArtists()) {
			builder.add("artist", createAttributesForArtist(artist, username), true);
		}

		criteria.setCount(getIntParameter(request, "albumCount", 20));
		criteria.setOffset(getIntParameter(request, "albumOffset", 0));
		searchResult = searchService.search(criteria, SearchService.IndexType.ALBUM_ID3, userGroupId);
		for (Album album : searchResult.getAlbums()) {
			builder.add("album", createAttributesForAlbum(album, username), true);
		}

		criteria.setCount(getIntParameter(request, "songCount", 20));
		criteria.setOffset(getIntParameter(request, "songOffset", 0));
		searchResult = searchService.search(criteria, SearchService.IndexType.SONG, userGroupId);
		for (MediaFile song : searchResult.getMediaFiles()) {
			builder.add("song", createAttributesForMediaFile(player, song, username), true);
		}

		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getPlaylists(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		XMLBuilder builder = createXMLBuilder(request, response, true);

		User user = securityService.getCurrentUser(request);

		String authenticatedUsername = user.getUsername();
		String requestedUsername = request.getParameter("username");

		if (requestedUsername == null) {
			requestedUsername = authenticatedUsername;
		} else if (!user.isAdminRole()) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, authenticatedUsername + " is not authorized to get playlists for " + requestedUsername);
			return;
		}

		builder.add("playlists", false);

		for (Playlist playlist : playlistService.getReadablePlaylistsForUser(requestedUsername, "name asc")) {
			List<String> sharedUsers = playlistService.getPlaylistUsers(playlist.getId());
			builder.add("playlist", createAttributesForPlaylist(playlist), sharedUsers.isEmpty());
			if (!sharedUsers.isEmpty()) {
				for (String username : sharedUsers) {
					builder.add("allowedUser", (Iterable<Attribute>) null, username, true);
				}
				builder.end();
			}
		}

		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getPlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);

		XMLBuilder builder = createXMLBuilder(request, response, true);

		int id = getRequiredIntParameter(request, "id");

		Playlist playlist = playlistService.getPlaylist(id);
		if (playlist == null) {
			error(request, response, ErrorCode.NOT_FOUND, "Playlist not found: " + id);
			return;
		}
		if (!playlistService.isReadAllowed(playlist, username)) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, "Permission denied for playlist " + id);
			return;
		}
		builder.add("playlist", createAttributesForPlaylist(playlist), false);
		for (String allowedUser : playlistService.getPlaylistUsers(playlist.getId())) {
			builder.add("allowedUser", (Iterable<Attribute>) null, allowedUser, true);
		}
		for (MediaFile mediaFile : playlistService.getFilesInPlaylist(id)) {
			AttributeSet attributes = createAttributesForMediaFile(player, mediaFile, username);
			builder.add("entry", attributes, true);
		}

		builder.endAll();
		response.getWriter().print(builder);
	}

	public void scanstatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request, true);

		User user = securityService.getCurrentUser(request);
		if (!user.isAdminRole()) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to get Status.");
			return;
		}

		XMLBuilder builder = createXMLBuilder(request, response, true);

		builder.add("scan", null, false);
		int Counter = mediaScannerService.getScanCount();

		if (mediaScannerService.isScanning() == true) {
			builder.add("status", false, new Attribute("started", true), new Attribute("count", Counter) );}
		else { builder.add("status", false, new Attribute("started", false) ); }

		builder.endAll();
		response.getWriter().print(builder);

	} 


	public void startRescan(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request, true);

		User user = securityService.getCurrentUser(request);
		if (!user.isAdminRole()) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to rescan.");
			return;
		}

		Date lastScan = settingsService.getLastScanned();

		mediaScannerService.scanLibrary();

		XMLBuilder builder = createXMLBuilder(request, response, true);

		builder.add("scan", null, false);

		if (mediaScannerService.isScanning() == true) {

			AttributeSet attributes = new AttributeSet();
			attributes.add("running", mediaScannerService.isScanning());
			attributes.add("count", mediaScannerService.getScanCount());
			builder.add("status", attributes, false);
			builder.end();

			AttributeSet attributesStatus = new AttributeSet();
			builder.add("Scan", attributesStatus, false);

			//FIXME: ScanDate
			//				attributes.add("lastScan", "xxxxx"); ///lastScan.toString());
			//				attributes.add("activeScan", mediaFileService.(mediaFileDao) "14.01.2013 12:30");

		} else
		{   builder.add("status", false, new Attribute("started", false) ); }

		builder.endAll();
		response.getWriter().print(builder);

	} 	    



	public void jukeboxControl(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request, true);

		User user = securityService.getCurrentUser(request);
		if (!user.isJukeboxRole()) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to use jukebox.");
			return;
		}

		boolean returnPlaylist = false;
		String action = getRequiredStringParameter(request, "action");
		if ("start".equals(action)) {
			playQueueService.doStart(request, response);
		} else if ("stop".equals(action)) {
			playQueueService.doStop(request, response);
		} else if ("skip".equals(action)) {
			int index = getRequiredIntParameter(request, "index");
			int offset = getIntParameter(request, "offset", 0);
			playQueueService.doSkip(request, response, index, offset);
		} else if ("add".equals(action)) {
			int[] ids = getIntParameters(request, "id");
			playQueueService.doAdd(request, response, ids, null);
		} else if ("set".equals(action)) {
			int[] ids = getIntParameters(request, "id");
			playQueueService.doSet(request, response, ids);
		} else if ("clear".equals(action)) {
			playQueueService.doClear(request, response);
		} else if ("remove".equals(action)) {
			int index = getRequiredIntParameter(request, "index");
			playQueueService.doRemove(request, response, index);
		} else if ("shuffle".equals(action)) {
			playQueueService.doShuffle(request, response);
		} else if ("setGain".equals(action)) {
			float gain = getRequiredFloatParameter(request, "gain");
			jukeboxService.setGain(gain);
		} else if ("get".equals(action)) {
			returnPlaylist = true;
		} else if ("status".equals(action)) {
			// No action necessary.
		} else {
			throw new Exception("Unknown jukebox action: '" + action + "'.");
		}

		XMLBuilder builder = createXMLBuilder(request, response, true);

		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);
		Player jukeboxPlayer = jukeboxService.getPlayer();
		boolean controlsJukebox = jukeboxPlayer != null && jukeboxPlayer.getId().equals(player.getId());
		PlayQueue playQueue = player.getPlayQueue();

		List<Attribute> attrs = new ArrayList<Attribute>(Arrays.asList(
				new Attribute("currentIndex", controlsJukebox && !playQueue.isEmpty() ? playQueue.getIndex() : -1),
				new Attribute("playing", controlsJukebox && !playQueue.isEmpty() && playQueue.getStatus() == PlayQueue.Status.PLAYING),
				new Attribute("gain", jukeboxService.getGain()),
				new Attribute("position", controlsJukebox && !playQueue.isEmpty() ? jukeboxService.getPosition() : 0)));

		if (returnPlaylist) {
			builder.add("jukeboxPlaylist", attrs, false);
			List<MediaFile> result;
			synchronized (playQueue) {
				result = playQueue.getFiles();
			}
			for (MediaFile mediaFile : result) {
				AttributeSet attributes = createAttributesForMediaFile(player, mediaFile, username);
				builder.add("entry", attributes, true);
			}
		} else {
			builder.add("jukeboxStatus", attrs, false);
		}

		builder.endAll();
		response.getWriter().print(builder);
	}

	public void createPlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request, true);
		String username = securityService.getCurrentUsername(request);

		Integer playlistId = getIntParameter(request, "playlistId");
		String name = request.getParameter("name");
		if (playlistId == null && name == null) {
			error(request, response, ErrorCode.MISSING_PARAMETER, "Playlist ID or name must be specified.");
			return;
		}

		Playlist playlist;
		if (playlistId != null) {
			playlist = playlistService.getPlaylist(playlistId);
			if (playlist == null) {
				error(request, response, ErrorCode.NOT_FOUND, "Playlist not found: " + playlistId);
				return;
			}
			if (!playlistService.isWriteAllowed(playlist, username)) {
				error(request, response, ErrorCode.NOT_AUTHORIZED, "Permission denied for playlist " + playlistId);
				return;
			}
		} else {
			playlist = new Playlist();
			playlist.setName(name);
			playlist.setCreated(new Date());
			playlist.setChanged(new Date());
			playlist.setPublic(false);
			playlist.setUsername(username);
			playlistService.createPlaylist(playlist);
		}

		List<MediaFile> songs = new ArrayList<MediaFile>();
		for (int id : getIntParameters(request, "songId")) {
			MediaFile song = mediaFileService.getMediaFile(id);
			if (song != null) {
				songs.add(song);
			}
		}
		playlistService.setFilesInPlaylist(playlist.getId(), songs);

		XMLBuilder builder = createXMLBuilder(request, response, true);
		builder.endAll();
		response.getWriter().print(builder);
	}

	public void updatePlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request, true);
		String username = securityService.getCurrentUsername(request);

		int id = getRequiredIntParameter(request, "playlistId");
		Playlist playlist = playlistService.getPlaylist(id);
		if (playlist == null) {
			error(request, response, ErrorCode.NOT_FOUND, "Playlist not found: " + id);
			return;
		}
		if (!playlistService.isWriteAllowed(playlist, username)) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, "Permission denied for playlist " + id);
			return;
		}

		String name = request.getParameter("name");
		if (name != null) {
			playlist.setName(name);
		}
		String comment = request.getParameter("comment");
		if (comment != null) {
			playlist.setComment(comment);
		}
		Boolean isPublic = getBooleanParameter(request, "public");
		if (isPublic != null) {
			playlist.setPublic(isPublic);
		}
		playlistService.updatePlaylist(playlist);

		// TODO: Add later
		//            for (String usernameToAdd : ServletRequestUtils.getStringParameters(request, "usernameToAdd")) {
		//                if (securityService.getUserByName(usernameToAdd) != null) {
		//                    playlistService.addPlaylistUser(id, usernameToAdd);
		//                }
		//            }
		//            for (String usernameToRemove : ServletRequestUtils.getStringParameters(request, "usernameToRemove")) {
		//                if (securityService.getUserByName(usernameToRemove) != null) {
		//                    playlistService.deletePlaylistUser(id, usernameToRemove);
		//                }
		//            }
		List<MediaFile> songs = playlistService.getFilesInPlaylist(id);
		boolean songsChanged = false;

		SortedSet<Integer> tmp = new TreeSet<Integer>();
		for (int songIndexToRemove : getIntParameters(request, "songIndexToRemove")) {
			tmp.add(songIndexToRemove);
		}
		List<Integer> songIndexesToRemove = new ArrayList<Integer>(tmp);
		Collections.reverse(songIndexesToRemove);
		for (Integer songIndexToRemove : songIndexesToRemove) {
			songs.remove(songIndexToRemove.intValue());
			songsChanged = true;
		}
		for (int songToAdd : getIntParameters(request, "songIdToAdd")) {
			MediaFile song = mediaFileService.getMediaFile(songToAdd);
			if (song != null) {
				songs.add(song);
				songsChanged = true;
			}
		}
		if (songsChanged) {
			playlistService.setFilesInPlaylist(id, songs);
		}

		XMLBuilder builder = createXMLBuilder(request, response, true);
		builder.endAll();
		response.getWriter().print(builder);
	}

	public void deletePlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request, true);
		String username = securityService.getCurrentUsername(request);

		int id = getRequiredIntParameter(request, "id");
		Playlist playlist = playlistService.getPlaylist(id);
		if (playlist == null) {
			error(request, response, ErrorCode.NOT_FOUND, "Playlist not found: " + id);
			return;
		}
		if (!playlistService.isWriteAllowed(playlist, username)) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, "Permission denied for playlist " + id);
			return;
		}
		playlistService.deletePlaylist(id);

		XMLBuilder builder = createXMLBuilder(request, response, true);
		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getAlbumList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);
		int userGroupId = securityService.getCurrentUserGroupId(request);        

		XMLBuilder builder = createXMLBuilder(request, response, true);
		builder.add("albumList", false);

		int size = getIntParameter(request, "size", 10);
		int offset = getIntParameter(request, "offset", 0);
		size = Math.max(0, Math.min(size, 1000));
		String type = getRequiredStringParameter(request, "type");

		List<MediaFile> albums = Collections.emptyList();
		//        List<org.madsonic.controller.HomeController.Album> HCalbums;

		if ("highest".equals(type)) {
			albums = ratingService.getHighestRated(offset, size, userGroupId);
		} else if ("frequent".equals(type)) {
			albums = mediaFileService.getMostFrequentlyPlayedAlbums(offset, size, userGroupId );
		} else if ("recent".equals(type)) {
			albums = mediaFileService.getMostRecentlyPlayedAlbums(offset, size, userGroupId);
		} else if ("newest".equals(type)) {
			albums = mediaFileService.getNewestAlbums(null, offset, size, userGroupId);
		} else if ("tip".equals(type)) {
			albums = hotService.getRandomHotRated(0, size, userGroupId );
		} else if ("hot".equals(type)) {
			albums = hotService.getHotRated(null, offset, size, userGroupId);
		} else if ("starred".equals(type)) {
			albums = mediaFileService.getStarredAlbums(offset, size, username);
		} else if ("alphabeticalByArtist".equals(type)) {
			albums = mediaFileService.getAlphabetialAlbums(offset, size, true, userGroupId);
		} else if ("alphabeticalByName".equals(type)) {
			albums = mediaFileService.getAlphabetialAlbums(offset, size, false, userGroupId);
		} else if ("byGenre".equals(type)) {
			albums = mediaFileService.getAlbumsByGenre(offset, size, getRequiredStringParameter(request, "genre"), userGroupId);
		} else if ("byYear".equals(type)) {
			albums = mediaFileService.getAlbumsByYear(offset, size, getRequiredIntParameter(request, "fromYear"), getRequiredIntParameter(request, "toYear"), userGroupId);
		} else if ("allArtist".equals(type)) {
			albums = mediaFileService.getArtists(offset, size, username, userGroupId);
		} else if ("starredArtist".equals(type)) {
			albums = mediaFileService.getStarredArtists(offset, size, username);
		} else if ("random".equals(type)) {
			albums = searchService.getRandomAlbums(null, size, userGroupId);
		} else {
			throw new Exception("Invalid list type: " + type);
		}

		for (MediaFile album : albums) {
			//	MediaFile mediaFile = mediaFileService.getMediaFile(album.getPath());
			AttributeSet attributes = createAttributesForMediaFile(player, album, username);
			builder.add("album", attributes, true);
		}
		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getAlbumList2(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);

		XMLBuilder builder = createXMLBuilder(request, response, true);
		builder.add("albumList2", false);

		int size = getIntParameter(request, "size", 10);
		int offset = getIntParameter(request, "offset", 0);
		size = Math.max(0, Math.min(size, 500));
		String type = getRequiredStringParameter(request, "type");
		String username = securityService.getCurrentUsername(request);

		List<Album> albums;
		if ("frequent".equals(type)) {
			albums = albumDao.getMostFrequentlyPlayedAlbums(offset, size);
		} else if ("recent".equals(type)) {
			albums = albumDao.getMostRecentlyPlayedAlbums(offset, size);
		} else if ("newest".equals(type)) {
			albums = albumDao.getNewestAlbums(offset, size);
		} else if ("alphabeticalByArtist".equals(type)) {
			albums = albumDao.getAlphabetialAlbums(offset, size, true);
		} else if ("alphabeticalByName".equals(type)) {
			albums = albumDao.getAlphabetialAlbums(offset, size, false);
		} else if ("byGenre".equals(type)) {
			albums = albumDao.getAlbumsByGenre(offset, size, getRequiredStringParameter(request, "genre"));
		} else if ("byYear".equals(type)) {
			albums = albumDao.getAlbumsByYear(offset, size, getRequiredIntParameter(request, "fromYear"),
					getRequiredIntParameter(request, "toYear"));
		} else if ("starred".equals(type)) {
			albums = albumDao.getStarredAlbums(offset, size, securityService.getCurrentUser(request).getUsername());
		} else if ("random".equals(type)) {
			albums = searchService.getRandomAlbumsId3(size);
		} else {
			throw new Exception("Invalid list type: " + type);
		}
		for (Album album : albums) {
			builder.add("album", createAttributesForAlbum(album, username), true);
		}
		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getTopTrackSongs(HttpServletRequest request, HttpServletResponse response) throws Exception {

		request = wrapRequest(request);

		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);

		//TODO: made secure
		int userGroupId = securityService.getCurrentUserGroupId(request);   

		XMLBuilder builder = createXMLBuilder(request, response, true);
		builder.add("topTrackSongs", false);

		String artist = getStringParameter(request, "artist", "unknown");
		int id = getIntParameter(request, "id", 0);
		int size = getIntParameter(request, "size", 20);
		size = Math.max(0, Math.min(size, 500));

		List <MediaFile> resultList = new ArrayList<MediaFile>(); 

		if (id > 1) {
			MediaFile mediaFile = mediaFileService.getMediaFile(id);
			if (mediaFile != null) {
				artist = mediaFile.getArtist();
			}
		}
		if (artist != "unknown") {
			resultList = lastFMService.updateTopTrackEntries(artist, size, userGroupId);		
		}

		for (MediaFile mediaFile : resultList) {
			AttributeSet attributes = createAttributesForMediaFile(player, mediaFile, username);
			builder.add("song", attributes, true);
		}

		builder.endAll();
		response.getWriter().print(builder);
	}

	private List<MediaFile> getMediaFiles(HttpServletRequest request, int user_group_id) {
		List<MediaFile> mediaFiles = new ArrayList<MediaFile>();
		for (int id : ServletRequestUtils.getIntParameters(request, "id")) {
			MediaFile mediaFile = mediaFileService.getMediaFile(id, user_group_id);
			if (mediaFile != null) {
				mediaFiles.add(mediaFile);
			}
		}
		return mediaFiles;
	}


	public void getPandoraSongs(HttpServletRequest request, HttpServletResponse response) throws Exception {

		request = wrapRequest(request);

		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);

		List<String> _artist = new ArrayList<String>();
		List<String> _albums = new ArrayList<String>();
		List<String> _genres = new ArrayList<String>();
		List<String> _mood = new ArrayList<String>();

		int userGroupId = securityService.getCurrentUserGroupId(request);   

		XMLBuilder builder = createXMLBuilder(request, response, true);
		builder.add("pandoraSongs", false);

		List <MediaFile> resultList = new ArrayList<MediaFile>(); 
		List<MediaFile> mediaFiles = null;
		try{
			mediaFiles = getMediaFiles(request, userGroupId);
		} catch (SecurityException x) {
			// Ignored
		}

		for (MediaFile media : mediaFiles) {

			MediaFile mediaFile = mediaFileService.getMediaFile(media.getId());

			if (mediaFile != null) {
				_artist.add( media.getArtist());
				_albums.add( media.getAlbumName());
				_genres.add( media.getGenre());
				_mood.add( media.getMood());

			} 	
		}

		String[] artist = _artist.toArray(new String[_artist.size()]);
		String[] albums = _albums.toArray(new String[_albums.size()]);
		String[] genres = _genres.toArray(new String[_genres.size()]);
		String[] mood = _mood.toArray(new String[_mood.size()]);

		if (mediaFiles.size() > 0) {
		
		
		//RESULT: ALBUM
		MultiSearchCriteria criteria1 = new MultiSearchCriteria (settingsService.getPandoraResultAlbum(), artist, albums, null, null, null, null, null, userGroupId);
		resultList.addAll(searchService.getRandomSongs(criteria1));
		if ("TEST".contains(SettingsService.getLogfileLevel()))     {System.out.println("-=Pandora-R1=-");
		for (MediaFile m : searchService.getRandomSongs(criteria1)) {System.out.println( m.getPath());}
		}

		//RESULT: ARTIST
		MultiSearchCriteria criteria2 = new MultiSearchCriteria (settingsService.getPandoraResultArtist(), artist, null, null, null, null, null, null, userGroupId);
		resultList.addAll(searchService.getRandomSongs(criteria2));
		if ("TEST".contains(SettingsService.getLogfileLevel()))     {System.out.println("-=Pandora-R2=-");
		for (MediaFile m : searchService.getRandomSongs(criteria2)) {System.out.println( m.getPath());}
		}
		//RESULT: GENRE
		GenreSearchCriteria criteria3 = new GenreSearchCriteria (settingsService.getPandoraResultGenre(), null, genres, null, null, null, userGroupId);
		resultList.addAll(searchService.getRandomSongs(criteria3));
		if ("TEST".contains(SettingsService.getLogfileLevel()))     {System.out.println("-=Pandora-R3=-");
		for (MediaFile m : searchService.getRandomSongs(criteria3)) {System.out.println( m.getPath());}
		}
		//RESULT: MOODS
		MoodsSearchCriteria criteria4 = new MoodsSearchCriteria (settingsService.getPandoraResultMood(), null, mood, null, null, null, userGroupId);
		resultList.addAll(searchService.getRandomSongs(criteria4));
		if ("TEST".contains(SettingsService.getLogfileLevel()))     {System.out.println("-=Pandora-R4=-");
		for (MediaFile m : searchService.getRandomSongs(criteria4)) {System.out.println( m.getPath());}
		}        	

		//RESULT: OTHER
		List<String> allArtists = new ArrayList<String>();
		List<String> similar = new ArrayList<String>();

		for (String __artist : artist) {
			similar = lastFMService.getSimilarArtist(__artist) ;
			allArtists.addAll(similar);
			if (allArtists.size() > 10) {
				break;
			}
		} 
		String[] array = allArtists.toArray(new String[allArtists.size()]);

		//RESULT: RANDOM
		MultiSearchCriteria criteria5 = new MultiSearchCriteria (settingsService.getPandoraResultSimilar(), array, null, null, null, null, null, null, userGroupId);
		resultList.addAll(searchService.getRandomSongs(criteria5));
		if ("TEST".contains(SettingsService.getLogfileLevel()))     {System.out.println("-=Pandora-R5=-");
		for (MediaFile m : searchService.getRandomSongs(criteria5)) {System.out.println( m.getPath());}
		}        	


		//RESULT: ARTIST TOPTRACKS
		List<MediaFile> resultTopTrack = new ArrayList<MediaFile>();

		if ( mediaFileDao.getTopTracks(artist[0], userGroupId).size() == 0) {
			resultTopTrack = lastFMService.getTopTrack(artist[0], 20, userGroupId);
		}

		if (artist.length > 1) {
			for (String __artist : artist) {
				// resultTopTrack = lastFMService.getTopTrack(_artist, 20, userGroupId);
				resultTopTrack = mediaFileDao.getTopTracks(__artist, userGroupId);
				if (resultTopTrack.size() > 0) {				
					for(int i = 0; i < settingsService.getPandoraResultArtistTopTrack(); i++) {
						Random myRandomizer = new Random();
						MediaFile randomTopTrack = resultTopTrack.get(myRandomizer.nextInt(resultTopTrack.size()));
						resultList.add(randomTopTrack);
					}
					if ("TEST".contains(SettingsService.getLogfileLevel())) {System.out.println("-=Pandora-R6=-");
					for (MediaFile m : resultTopTrack) {System.out.println( m.getPath());}
					}   				
				}
			}			
		}

		if (artist.length == 1) {		
			// resultTopTrack = lastFMService.getTopTrack(artist[0], settingsService.getPandoraResultArtistTopTrack(), userGroupId);
			resultTopTrack = mediaFileDao.getTopTracks(artist[0], userGroupId);
			if (resultTopTrack.size() > 0) {
				for(int i = 0; i < settingsService.getPandoraResultArtistTopTrack(); i++) {
					Random myRandomizer = new Random();
					MediaFile randomTopTrack = resultTopTrack.get(myRandomizer.nextInt(resultTopTrack.size()));
					resultList.add(randomTopTrack);
				}
				if ("TEST".contains(SettingsService.getLogfileLevel())) {System.out.println("-=Pandora-R7=-");
				for (MediaFile m : resultTopTrack) {System.out.println( m.getPath());}
				}  			
			}
		}

		}
		
		// Filter out duplicates
		HashSet<MediaFile> hs = new HashSet<MediaFile>();
		hs.addAll(resultList);
		resultList.clear();
		resultList.addAll(hs);        		

		for (MediaFile mediaFile : resultList) {
			AttributeSet attributes = createAttributesForMediaFile(player, mediaFile, username);
			builder.add("song", attributes, true);
		}

		builder.endAll();
		response.getWriter().print(builder);
	}

	
	public void getRandomSongs(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);
		int userGroupId = securityService.getCurrentUserGroupId(request);   

		XMLBuilder builder = createXMLBuilder(request, response, true);
		builder.add("randomSongs", false);

		int size = getIntParameter(request, "size", 20);
		size = Math.max(0, Math.min(size, 500));
		String genre = getStringParameter(request, "genre");
		Integer fromYear = getIntParameter(request, "fromYear");
		Integer toYear = getIntParameter(request, "toYear");
		Integer musicFolderId = getIntParameter(request, "musicFolderId");

		if (musicFolderId == null){
			List<MusicFolder> musicFolders = settingsService.getAllMusicFolders(userGroupId, settingsService.isSortMediaFileFolder());
			Random generator = new Random();
			int index = generator.nextInt (musicFolders.size());
			if ( index >-1 ) { musicFolderId = musicFolders.get(index).getId();	}
		}

		RandomSearchCriteria criteria = new RandomSearchCriteria(size, genre, fromYear, toYear, musicFolderId);
		List <MediaFile> resultList; 
		int loop = 0;

		do { 
			resultList = searchService.getRandomSongs(criteria);
			loop++;
			List<MusicFolder> musicFolders = settingsService.getAllMusicFolders(userGroupId, settingsService.isSortMediaFileFolder());
			Random generator = new Random();
			int index = generator.nextInt (musicFolders.size());
			if ( index >-1 ) { musicFolderId = musicFolders.get(index).getId();	}
			RandomSearchCriteria tmpCriteria = new RandomSearchCriteria(size, genre, fromYear, toYear, musicFolderId);
			resultList = searchService.getRandomSongs(tmpCriteria);
			if ( loop>10 ){ 
				//System.out.println("BREAK"); 
				break; }
		} while (resultList.size()<1);

		for (MediaFile mediaFile : resultList) {
			AttributeSet attributes = createAttributesForMediaFile(player, mediaFile, username);
			builder.add("song", attributes, true);
		}

		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getVideos(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);

		XMLBuilder builder = createXMLBuilder(request, response, true);
		builder.add("videos", false);
		int size = getIntParameter(request, "size", Integer.MAX_VALUE);
		int offset = getIntParameter(request, "offset", 0);

		for (MediaFile mediaFile : mediaFileDao.getVideos(size, offset)) {
			builder.add("video", createAttributesForMediaFile(player, mediaFile, username), true);
		}
		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getNowPlaying(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		XMLBuilder builder = createXMLBuilder(request, response, true);
		builder.add("nowPlaying", false);

		for (TransferStatus status : statusService.getAllStreamStatuses()) {

			Player player = status.getPlayer();
			File file = status.getFile();
			if (player != null && player.getUsername() != null && file != null) {

				String username = player.getUsername();
				UserSettings userSettings = settingsService.getUserSettings(username);
				if (!userSettings.isNowPlayingAllowed()) {
					continue;
				}

				MediaFile mediaFile = mediaFileService.getMediaFile(file);

				long minutesAgo = status.getMillisSinceLastUpdate() / 1000L / 60L;
				if (minutesAgo < 60) {
					AttributeSet attributes = createAttributesForMediaFile(player, mediaFile, username);
					attributes.add("username", username);
					attributes.add("playerId", player.getId());
					attributes.add("playerName", player.getName());
					attributes.add("minutesAgo", minutesAgo);
					builder.add("entry", attributes, true);
				}
			}
		}

		builder.endAll();
		response.getWriter().print(builder);
	}

	private AttributeSet createAttributesForMediaFile(Player player, MediaFile mediaFile, String username) {
		MediaFile parent = mediaFileService.getParentOf(mediaFile);
		AttributeSet attributes = new AttributeSet();
		attributes.add("id", mediaFile.getId());
		try {
			if (!mediaFileService.isRoot(parent)) {
				attributes.add("parent", parent.getId());
			}
		} catch (SecurityException x) {
			// Ignored.
		}

		if (mediaFile.isAlbum() || mediaFile.isAlbumSet()) 
		{
			if (settingsService.isShowAlbumsYearApi()) {
				attributes.add("title", "[" + mediaFile.getYear() + "] " + mediaFile.getAlbumName()); 
				attributes.add("album", mediaFile.getAlbumSetName());
			}else
			{
				attributes.add("title", mediaFile.getAlbumName()); 
				attributes.add("album", mediaFile.getAlbumSetName());
				attributes.add("path", mediaFile.getPath().substring(mediaFile.getPath().lastIndexOf('\\') + 1)  );              
			}
		}
		else {
			attributes.add("title", mediaFile.getName()); 
			attributes.add("album", parent.getAlbumSetName());
		}

		attributes.add("artist", mediaFile.getArtist());
		attributes.add("isDir", mediaFile.isDirectory());
		attributes.add("coverArt", findCoverArt(mediaFile, parent));
		attributes.add("year", mediaFile.getYear());
		attributes.add("genre", mediaFile.getGenre());
		attributes.add("created", StringUtil.toISO8601(mediaFile.getCreated()));
		attributes.add("starred", StringUtil.toISO8601(mediaFileDao.getMediaFileStarredDate(mediaFile.getId(), username)));
		attributes.add("userRating", ratingService.getRatingForUser(username, mediaFile));
		attributes.add("averageRating", ratingService.getAverageRating(mediaFile));

		if (mediaFile.isSingleArtist() || mediaFile.isMultiArtist()) 
		{
			attributes.add("isArtist", true);
			attributes.add("title", mediaFile.getArtist());
			attributes.add("artist", mediaFile.getGenre());
		}        

		if (mediaFile.isFile()) {
			attributes.add("duration", mediaFile.getDurationSeconds());
			attributes.add("bitRate", mediaFile.getBitRate());
			attributes.add("track", mediaFile.getTrackNumber());
			attributes.add("discNumber", mediaFile.getDiscNumber());
			attributes.add("size", mediaFile.getFileSize());
			attributes.add("rank", mediaFile.getRank());
			String suffix = mediaFile.getFormat();
			attributes.add("suffix", suffix);
			attributes.add("contentType", StringUtil.getMimeType(suffix));
			attributes.add("isVideo", mediaFile.isVideo());
			attributes.add("data", mediaFile.getData());
			
			attributes.add("path", getRelativePath(mediaFile));

			Bookmark bookmark = bookmarkCache.get(new BookmarkKey(username, mediaFile.getId()));
			if (bookmark != null) {
				attributes.add("bookmarkPosition", bookmark.getPositionMillis());
			}

			if (mediaFile.getAlbumArtist() != null && mediaFile.getAlbumName() != null) {
				Album album = albumDao.getAlbum(mediaFile.getAlbumArtist(), mediaFile.getAlbumName());
				if (album != null) {
					attributes.add("albumId", album.getId());
				}
			}
			if (mediaFile.getArtist() != null) {
				Artist artist = artistDao.getArtist(mediaFile.getArtist());
				if (artist != null) {
					attributes.add("artistId", artist.getId());
				}
			}
			switch (mediaFile.getMediaType()) {
			case MUSIC:
				attributes.add("type", "music");
				break;
			case PODCAST:
				attributes.add("type", "podcast");
				break;
			case AUDIOBOOK:
				attributes.add("type", "audiobook");
				break;
			default:
				break;
			}

			if (transcodingService.isTranscodingRequired(mediaFile, player)) {
				String transcodedSuffix = transcodingService.getSuffix(player, mediaFile, null);
				attributes.add("transcodedSuffix", transcodedSuffix);
				attributes.add("transcodedContentType", StringUtil.getMimeType(transcodedSuffix));
			}
		}
		return attributes;
	}

	private Integer findCoverArt(MediaFile mediaFile, MediaFile parent) {
		MediaFile dir = mediaFile.isDirectory() ? mediaFile : parent;
		if (dir != null && dir.getCoverArtPath() != null) {
			return dir.getId();
		}
		return null;
	}

	private String getRelativePath(MediaFile musicFile) {

		String filePath = musicFile.getPath();

		// Convert slashes.
		filePath = filePath.replace('\\', '/');

		String filePathLower = filePath.toLowerCase();

		List<MusicFolder> musicFolders = settingsService.getAllMusicFolders(false, true);
		for (MusicFolder musicFolder : musicFolders) {
			String folderPath = musicFolder.getPath().getPath();
			folderPath = folderPath.replace('\\', '/');
			String folderPathLower = folderPath.toLowerCase();
			if (!folderPathLower.endsWith("/")) {
				folderPathLower += "/";
			}

			if (filePathLower.startsWith(folderPathLower)) {
				String relativePath = filePath.substring(folderPath.length());
				return relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
			}
		}

		return null;
	}

	public ModelAndView download(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		User user = securityService.getCurrentUser(request);
		if (!user.isDownloadRole()) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to download files.");
			return null;
		}

		long ifModifiedSince = request.getDateHeader("If-Modified-Since");
		long lastModified = downloadController.getLastModified(request);

		if (ifModifiedSince != -1 && lastModified != -1 && lastModified <= ifModifiedSince) {
			response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
			return null;
		}

		if (lastModified != -1) {
			response.setDateHeader("Last-Modified", lastModified);
		}

		return downloadController.handleRequest(request, response);
	}

	public ModelAndView stream(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		User user = securityService.getCurrentUser(request);
		if (!user.isStreamRole()) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to play files.");
			return null;
		}

		streamController.handleRequest(request, response);
		return null;
	}

	public ModelAndView hls(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		User user = securityService.getCurrentUser(request);
		if (!user.isStreamRole()) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to play files.");
			return null;
		}
		hlsController.handleRequest(request, response);
		return null;
	}

	public void scrobble(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		XMLBuilder builder = createXMLBuilder(request, response, true);

		Player player = playerService.getPlayer(request, response);

		if (!settingsService.getUserSettings(player.getUsername()).isLastFmEnabled()) {
			error(request, response, ErrorCode.GENERIC, "Scrobbling is not enabled for " + player.getUsername() + ".");
			return;
		}

		boolean submission = getBooleanParameter(request, "submission", true);
		int[] ids = getRequiredIntParameters(request, "id");
		long[] times = getLongParameters(request, "time");
		if (times.length > 0 && times.length != ids.length) {
			error(request, response, ErrorCode.GENERIC, "Wrong number of timestamps: " + times.length);
			return;
		}

		for (int i = 0; i < ids.length; i++) {
			int id = ids[i];
			MediaFile file = mediaFileService.getMediaFile(id);
			if (file == null) {
				LOG.warn("File to scrobble not found: " + id);
				continue;
			}
			Date time = times.length == 0 ? null : new Date(times[i]);
			audioScrobblerService.register(file, player.getUsername(), submission, time);
		}

		builder.endAll();
		response.getWriter().print(builder);
	}

	public void star(HttpServletRequest request, HttpServletResponse response) throws Exception {
		starOrUnstar(request, response, true);
	}

	public void unstar(HttpServletRequest request, HttpServletResponse response) throws Exception {
		starOrUnstar(request, response, false);
	}

	private void starOrUnstar(HttpServletRequest request, HttpServletResponse response, boolean star) throws Exception {
		request = wrapRequest(request);
		XMLBuilder builder = createXMLBuilder(request, response, true);

		String username = securityService.getCurrentUser(request).getUsername();
		for (int id : getIntParameters(request, "id")) {
			MediaFile mediaFile = mediaFileDao.getMediaFile(id);
			if (mediaFile == null) {
				error(request, response, ErrorCode.NOT_FOUND, "Media file not found: " + id);
				return;
			}
			if (star) {
				mediaFileDao.starMediaFile(id, username);
			} else {
				mediaFileDao.unstarMediaFile(id, username);
			}
		}
		for (int albumId : getIntParameters(request, "albumId")) {
			Album album = albumDao.getAlbum(albumId);
			if (album == null) {
				error(request, response, ErrorCode.NOT_FOUND, "Album not found: " + albumId);
				return;
			}
			if (star) {
				albumDao.starAlbum(albumId, username);
			} else {
				albumDao.unstarAlbum(albumId, username);
			}
		}
		for (int artistId : getIntParameters(request, "artistId")) {
			Artist artist = artistDao.getArtist(artistId);
			if (artist == null) {
				error(request, response, ErrorCode.NOT_FOUND, "Artist not found: " + artistId);
				return;
			}
			if (star) {
				artistDao.starArtist(artistId, username);
			} else {
				artistDao.unstarArtist(artistId, username);
			}
		}
		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getNewaddedSongs(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);
		int userGroupId = securityService.getCurrentUserGroupId(request);
		int size = getIntParameter(request, "size", 50);
		int offset = getIntParameter(request, "offset", 0);

		XMLBuilder builder = createXMLBuilder(request, response, true);
		builder.add("newadded", false);

		for (MediaFile song : mediaFileDao.getHistory(offset, size, userGroupId, MUSIC.name())) {
			builder.add("song", createAttributesForMediaFile(player, song, username), true);
		}
		builder.endAll();
		response.getWriter().print(builder);
	}    

	public void getLastplayedSongs(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);
		int size = getIntParameter(request, "size", 50);
		int offset = getIntParameter(request, "offset", 0);

		XMLBuilder builder = createXMLBuilder(request, response, true);
		builder.add("lastplayed", false);

		for (MediaFile song : mediaFileDao.getLastPlayedCountForUser(offset, size, username)) {
			builder.add("song", createAttributesForMediaFile(player, song, username), true);
		}
		builder.endAll();
		response.getWriter().print(builder);
	}    


	public void getStarred(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);

		XMLBuilder builder = createXMLBuilder(request, response, true);
		builder.add("starred", false);
		for (MediaFile artist : mediaFileDao.getStarredDirectories(0, Integer.MAX_VALUE, username)) {
			builder.add("artist", createAttributesForArtist(artist, username), true);
		}
		for (MediaFile album : mediaFileDao.getStarredAlbums(0, Integer.MAX_VALUE, username)) {
			builder.add("album", createAttributesForMediaFile(player, album, username), true);
		}
		for (MediaFile song : mediaFileDao.getStarredFiles(0, Integer.MAX_VALUE, username)) {
			builder.add("song", createAttributesForMediaFile(player, song, username), true);
		}
		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getStarred2(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);

		XMLBuilder builder = createXMLBuilder(request, response, true);
		builder.add("starred2", false);
		for (Artist artist : artistDao.getStarredArtists(0, Integer.MAX_VALUE, username)) {
			builder.add("artist", createAttributesForArtist(artist, username), true);
		}
		for (Album album : albumDao.getStarredAlbums(0, Integer.MAX_VALUE, username)) {
			builder.add("album", createAttributesForAlbum(album, username), true);
		}
		for (MediaFile song : mediaFileDao.getStarredFiles(0, Integer.MAX_VALUE, username)) {
			builder.add("song", createAttributesForMediaFile(player, song, username), true);
		}
		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getPodcasts(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);
		boolean includeEpisodes = getBooleanParameter(request, "includeEpisodes", true);
		Integer channelId = getIntParameter(request, "id");

		XMLBuilder builder = createXMLBuilder(request, response, true);
		builder.add("podcasts", false);

		for (PodcastChannel channel : podcastService.getAllChannels()) {
			if (channelId == null || channelId.equals(channel.getId())) {
				AttributeSet channelAttrs = new AttributeSet();
				channelAttrs.add("id", channel.getId());
				channelAttrs.add("url", channel.getUrl());
				channelAttrs.add("status", channel.getStatus().toString().toLowerCase());
				channelAttrs.add("title", channel.getTitle());
				channelAttrs.add("description", channel.getDescription());
				channelAttrs.add("errorMessage", channel.getErrorMessage());
				builder.add("channel", channelAttrs, false);

				if (includeEpisodes) {
					List<PodcastEpisode> episodes = podcastService.getEpisodes(channel.getId(), false);
					for (PodcastEpisode episode : episodes) {
						AttributeSet episodeAttrs = new AttributeSet();

						String path = episode.getPath();
						if (path != null) {
							MediaFile mediaFile = mediaFileService.getMediaFile(path);
							episodeAttrs.addAll(createAttributesForMediaFile(player, mediaFile, username));
							episodeAttrs.add("streamId", mediaFile.getId());
						}

						episodeAttrs.add("id", episode.getId());  // Overwrites the previous "id" attribute.
						episodeAttrs.add("status", episode.getStatus().toString().toLowerCase());
						episodeAttrs.add("title", episode.getTitle());
						episodeAttrs.add("description", episode.getDescription());
						episodeAttrs.add("publishDate", episode.getPublishDate());

						builder.add("episode", episodeAttrs, true);
					}
				}
				builder.end(); // <channel>
			}
		}
		builder.endAll();
		response.getWriter().print(builder);
	}

	public void refreshPodcasts(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		User user = securityService.getCurrentUser(request);
		if (!user.isPodcastRole()) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to administrate podcasts.");
			return;
		}
		podcastService.refreshAllChannels(true);
		XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
		response.getWriter().print(builder);
	}

	public void createPodcastChannel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		User user = securityService.getCurrentUser(request);
		if (!user.isPodcastRole()) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to administrate podcasts.");
			return;
		}

		String url = getRequiredStringParameter(request, "url");
		podcastService.createChannel(url);
		XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
		response.getWriter().print(builder);
	}

	public void deletePodcastChannel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		User user = securityService.getCurrentUser(request);
		if (!user.isPodcastRole()) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to administrate podcasts.");
			return;
		}

		int id = getRequiredIntParameter(request, "id");
		podcastService.deleteChannel(id);
		XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
		response.getWriter().print(builder);
	}

	public void deletePodcastEpisode(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		User user = securityService.getCurrentUser(request);
		if (!user.isPodcastRole()) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to administrate podcasts.");
			return;
		}

		int id = getRequiredIntParameter(request, "id");
		podcastService.deleteEpisode(id, true);
		XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
		response.getWriter().print(builder);
	}

	public void downloadPodcastEpisode(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		User user = securityService.getCurrentUser(request);
		if (!user.isPodcastRole()) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to administrate podcasts.");
			return;
		}

		int id = getRequiredIntParameter(request, "id");
		PodcastEpisode episode = podcastService.getEpisode(id, true);
		if (episode == null) {
			error(request, response, ErrorCode.NOT_FOUND, "Podcast episode " + id + " not found.");
			return;
		}

		podcastService.downloadEpisode(episode);
		XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
		response.getWriter().print(builder);
	}

	public void getInternetRadioStations(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		XMLBuilder builder = createXMLBuilder(request, response, true);

		builder.add("internetRadioStations", false);
		for (InternetRadio radio : settingsService.getAllInternetRadios()) {
			AttributeSet attrs = new AttributeSet();
			attrs.add("id", radio.getId());
			attrs.add("name", radio.getName());
			attrs.add("streamUrl", radio.getStreamUrl());
			attrs.add("homePageUrl", radio.getHomepageUrl());
			builder.add("internetRadioStation", attrs, true);
		}
		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getBookmarks(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);

		XMLBuilder builder = createXMLBuilder(request, response, true);

		builder.add("bookmarks", false);
		for (Bookmark bookmark : bookmarkDao.getBookmarks(username)) {
			builder.add("bookmark", createAttributesForBookmark(bookmark), false);
			MediaFile mediaFile = mediaFileService.getMediaFile(bookmark.getMediaFileId());
			AttributeSet attributes = createAttributesForMediaFile(player, mediaFile, username);
			builder.add("entry", attributes, true);
			builder.end();
		}
		builder.endAll();
		response.getWriter().print(builder);
	}

	public void createBookmark(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		String username = securityService.getCurrentUsername(request);
		int mediaFileId = getRequiredIntParameter(request, "id");
		long position = getRequiredLongParameter(request, "position");
		String comment = request.getParameter("comment");
		Date now = new Date();

		Bookmark bookmark = new Bookmark(0, mediaFileId, position, username, comment, now, now);
		bookmarkDao.createOrUpdateBookmark(bookmark);
		refreshBookmarkCache();
		XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
		response.getWriter().print(builder);
	}

	public void deleteBookmark(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		XMLBuilder builder = createXMLBuilder(request, response, true);

		String username = securityService.getCurrentUsername(request);
		int mediaFileId = getRequiredIntParameter(request, "id");
		bookmarkDao.deleteBookmark(username, mediaFileId);
		refreshBookmarkCache();

		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getShares(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);

		User user = securityService.getCurrentUser(request);
		XMLBuilder builder = createXMLBuilder(request, response, true);

		builder.add("shares", false);
		for (Share share : shareService.getSharesForUser(user)) {
			builder.add("share", createAttributesForShare(share), false);

			for (MediaFile mediaFile : shareService.getSharedFiles(share.getId())) {
				AttributeSet attributes = createAttributesForMediaFile(player, mediaFile, username);
				builder.add("entry", attributes, true);
			}

			builder.end();
		}
		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getSharedFiles(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);

		String shareName = getRequiredStringParameter(request, "share");
		Share share = shareService.getShareByName(shareName);

		XMLBuilder builder = createXMLBuilder(request, response, true);
		builder.add("sharedFiles", false);

		for (MediaFile mediaFile : shareService.getSharedFiles(share.getId())) {

			if ( mediaFile.isDirectory() ) {
				for(MediaFile child : mediaFileService.getChildrenOf(mediaFile, true, false, false)){
					builder.add("song", createAttributesForMediaFile(player, child, username), true);
				};
			} 
			else {
				builder.add("song", createAttributesForMediaFile(player, mediaFile, username), true);
			}

		}
		builder.endAll();
		response.getWriter().print(builder);

	}

	public void createShare(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		Player player = playerService.getPlayer(request, response);
		String username = securityService.getCurrentUsername(request);

		User user = securityService.getCurrentUser(request);
		if (!user.isShareRole()) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to share media.");
			return;
		}

		//        if (!settingsService.isUrlRedirectionEnabled()) {
		//            error(request, response, ErrorCode.GENERIC, "Sharing is only supported for *.subsonic.org domain names.");
		//            return;
		//        }

		XMLBuilder builder = createXMLBuilder(request, response, true);

		List<MediaFile> files = new ArrayList<MediaFile>();
		for (int id : getRequiredIntParameters(request, "id")) {
			files.add(mediaFileService.getMediaFile(id));
		}

		Share share = shareService.createShare(request, files);
		share.setDescription(request.getParameter("description"));
		long expires = getLongParameter(request, "expires", 0L);
		if (expires != 0) {
			share.setExpires(new Date(expires));
		}
		shareService.updateShare(share);

		builder.add("shares", false);
		builder.add("share", createAttributesForShare(share), false);

		for (MediaFile mediaFile : shareService.getSharedFiles(share.getId())) {
			AttributeSet attributes = createAttributesForMediaFile(player, mediaFile, username);
			builder.add("entry", attributes, true);
		}

		builder.endAll();
		response.getWriter().print(builder);
	}

	public void deleteShare(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		User user = securityService.getCurrentUser(request);
		int id = getRequiredIntParameter(request, "id");

		Share share = shareService.getShareById(id);
		if (share == null) {
			error(request, response, ErrorCode.NOT_FOUND, "Shared media not found.");
			return;
		}
		if (!user.isAdminRole() && !share.getUsername().equals(user.getUsername())) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, "Not authorized to delete shared media.");
			return;
		}

		shareService.deleteShare(id);
		XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
		response.getWriter().print(builder);
	}

	public void updateShare(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		User user = securityService.getCurrentUser(request);
		int id = getRequiredIntParameter(request, "id");

		Share share = shareService.getShareById(id);
		if (share == null) {
			error(request, response, ErrorCode.NOT_FOUND, "Shared media not found.");
			return;
		}
		if (!user.isAdminRole() && !share.getUsername().equals(user.getUsername())) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, "Not authorized to modify shared media.");
			return;
		}

		share.setDescription(request.getParameter("description"));
		String expiresString = request.getParameter("expires");
		if (expiresString != null) {
			long expires = Long.parseLong(expiresString);
			share.setExpires(expires == 0L ? null : new Date(expires));
		}
		shareService.updateShare(share);
		XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
		response.getWriter().print(builder);
	}

	private List<Attribute> createAttributesForShare(Share share) {
		List<Attribute> attributes = new ArrayList<Attribute>();

		attributes.add(new Attribute("id", share.getId()));
		attributes.add(new Attribute("url", shareService.getShareUrl(share)));
		attributes.add(new Attribute("username", share.getUsername()));
		attributes.add(new Attribute("created", StringUtil.toISO8601(share.getCreated())));
		attributes.add(new Attribute("visitCount", share.getVisitCount()));
		attributes.add(new Attribute("description", share.getDescription()));
		attributes.add(new Attribute("expires", StringUtil.toISO8601(share.getExpires())));
		attributes.add(new Attribute("lastVisited", StringUtil.toISO8601(share.getLastVisited())));

		return attributes;
	}

	private List<Attribute> createAttributesForBookmark(Bookmark bookmark) {
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(new Attribute("position", bookmark.getPositionMillis()));
		attributes.add(new Attribute("username", bookmark.getUsername()));
		attributes.add(new Attribute("comment", bookmark.getComment()));
		attributes.add(new Attribute("created", StringUtil.toISO8601(bookmark.getCreated())));
		attributes.add(new Attribute("changed", StringUtil.toISO8601(bookmark.getChanged())));
		return attributes;
	}

	public ModelAndView videoPlayer(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);

		Map<String, Object> map = new HashMap<String, Object>();
		int id = getRequiredIntParameter(request, "id");
		MediaFile file = mediaFileService.getMediaFile(id);

		int timeOffset = getIntParameter(request, "timeOffset", 0);
		timeOffset = Math.max(0, timeOffset);
		Integer duration = file.getDurationSeconds();
		if (duration != null) {
			map.put("skipOffsets", VideoPlayerController.createSkipOffsets(duration));
			timeOffset = Math.min(duration, timeOffset);
			duration -= timeOffset;
		}

		map.put("id", request.getParameter("id"));
		map.put("u", request.getParameter("u"));
		map.put("p", request.getParameter("p"));
		map.put("c", request.getParameter("c"));
		map.put("v", request.getParameter("v"));
		map.put("video", file);
		map.put("maxBitRate", getIntParameter(request, "maxBitRate", VideoPlayerController.DEFAULT_BIT_RATE));
		map.put("duration", duration);
		map.put("timeOffset", timeOffset);
		map.put("bitRates", VideoPlayerController.BIT_RATES);
		map.put("autoplay", getBooleanParameter(request, "autoplay", true));

		ModelAndView result = new ModelAndView();

		if (SettingsService.getUsedVideoPlayer().equalsIgnoreCase("HTML5")) {

			result.setViewName("rest/videoPlayerH5");

		} else {
			result.setViewName("rest/videoPlayer");
		}

		result.addObject("model", map);
		return result;
	}

	public ModelAndView getCoverArt(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		return coverArtController.handleRequest(request, response);
	}

	public ModelAndView getAvatar(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		return avatarController.handleRequest(request, response);
	}

	public void changePassword(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);

		String username = getRequiredStringParameter(request, "username");
		String password = decrypt(getRequiredStringParameter(request, "password"));

        User authUser = securityService.getCurrentUser(request);

        boolean allowed = authUser.isAdminRole()
                || username.equals(authUser.getUsername()) && authUser.isSettingsRole();

        if (!allowed) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, authUser.getUsername() + " is not authorized to change password for " + username);
            return;
        }

		User user = securityService.getUserByName(username);
		user.setPassword(password);
		securityService.updateUser(user);

		XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
		response.getWriter().print(builder);
	}

	public void getUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);

		String username = getRequiredStringParameter(request, "username");

		User currentUser = securityService.getCurrentUser(request);
		if (!username.equals(currentUser.getUsername()) && !currentUser.isAdminRole()) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, currentUser.getUsername() + " is not authorized to get details for other users.");
			return;
		}

		User requestedUser = securityService.getUserByName(username);
		if (requestedUser == null) {
			error(request, response, ErrorCode.NOT_FOUND, "No such user: " + username);
			return;
		}

		UserSettings userSettings = settingsService.getUserSettings(username);

		XMLBuilder builder = createXMLBuilder(request, response, true);
		List<Attribute> attributes = createAttributesForUser(requestedUser, userSettings);

		builder.add("user", attributes, true);
		builder.endAll();
		response.getWriter().print(builder);
	}

	public void getUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);

		User currentUser = securityService.getCurrentUser(request);
		if (!currentUser.isAdminRole()) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, currentUser.getUsername() + " is not authorized to get details for other users.");
			return;
		}

		XMLBuilder builder = createXMLBuilder(request, response, true);
		builder.add("users", false);
		for (User user : securityService.getAllUsers()) {
			UserSettings userSettings = settingsService.getUserSettings(user.getUsername());
			List<Attribute> attributes = createAttributesForUser(user, userSettings);
			builder.add("user", attributes, true);

		}
		builder.endAll();
		response.getWriter().print(builder);
	}

	private List<Attribute> createAttributesForUser(User user, UserSettings userSettings) {
		return Arrays.asList(
				new Attribute("username", user.getUsername()),
				new Attribute("email", user.getEmail()),
				new Attribute("scrobblingEnabled", userSettings.isLastFmEnabled()),
				new Attribute("adminRole", user.isAdminRole()),
				new Attribute("settingsRole", user.isSettingsRole()),
				new Attribute("downloadRole", user.isDownloadRole()),
				new Attribute("uploadRole", user.isUploadRole()),
				new Attribute("playlistRole", true),  // Since 1.8.0
				new Attribute("coverArtRole", user.isCoverArtRole()),
				new Attribute("commentRole", user.isCommentRole()),
				new Attribute("podcastRole", user.isPodcastRole()),
				new Attribute("streamRole", user.isStreamRole()),
				new Attribute("jukeboxRole", user.isJukeboxRole()),
				new Attribute("shareRole", user.isShareRole()),
				new Attribute("lastFMRole", user.isLastFMRole())
				);
	}

	public void createUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		User user = securityService.getCurrentUser(request);
		if (!user.isAdminRole()) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to create new users.");
			return;
		}

		UserSettingsCommand command = new UserSettingsCommand();
		command.setUsername(getRequiredStringParameter(request, "username"));
		command.setPassword(decrypt(getRequiredStringParameter(request, "password")));
		command.setEmail(getRequiredStringParameter(request, "email"));
		command.setLdapAuthenticated(getBooleanParameter(request, "ldapAuthenticated", false));
		command.setAdminRole(getBooleanParameter(request, "adminRole", false));
		command.setCommentRole(getBooleanParameter(request, "commentRole", false));
		command.setCoverArtRole(getBooleanParameter(request, "coverArtRole", false));
		command.setDownloadRole(getBooleanParameter(request, "downloadRole", false));
		command.setStreamRole(getBooleanParameter(request, "streamRole", true));
		command.setUploadRole(getBooleanParameter(request, "uploadRole", false));
		command.setJukeboxRole(getBooleanParameter(request, "jukeboxRole", false));
		command.setPodcastRole(getBooleanParameter(request, "podcastRole", false));
		command.setSettingsRole(getBooleanParameter(request, "settingsRole", true));
		command.setShareRole(getBooleanParameter(request, "shareRole", false));
		command.setSearchRole(getBooleanParameter(request, "searchRole", false));
		command.setLastFMRole(getBooleanParameter(request, "lastFMRole", false));
		command.setTranscodeSchemeName(getStringParameter(request, "transcodeScheme", TranscodeScheme.OFF.name()));
		command.setGroupId(getIntParameter(request, "groupId", 0));

		userSettingsController.createUser(command);
		XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
		response.getWriter().print(builder);
	}

	public void updateUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		User user = securityService.getCurrentUser(request);
		if (!user.isAdminRole()) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to update users.");
			return;
		}

		String username = getRequiredStringParameter(request, "username");
		User u = securityService.getUserByName(username);
		UserSettings s = settingsService.getUserSettings(username);

		if (u == null) {
			error(request, response, ErrorCode.NOT_FOUND, "No such user: " + username);
			return;
		} else if (User.USERNAME_ADMIN.equals(username)) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, "Not allowed to change admin user");
			return;
		}

		UserSettingsCommand command = new UserSettingsCommand();
		command.setUsername(username);
		command.setEmail(getStringParameter(request, "email", u.getEmail()));
		command.setLdapAuthenticated(getBooleanParameter(request, "ldapAuthenticated", u.isLdapAuthenticated()));
		command.setAdminRole(getBooleanParameter(request, "adminRole", u.isAdminRole()));
		command.setCommentRole(getBooleanParameter(request, "commentRole", u.isCommentRole()));
		command.setCoverArtRole(getBooleanParameter(request, "coverArtRole", u.isCoverArtRole()));
		command.setDownloadRole(getBooleanParameter(request, "downloadRole", u.isDownloadRole()));
		command.setStreamRole(getBooleanParameter(request, "streamRole", u.isDownloadRole()));
		command.setUploadRole(getBooleanParameter(request, "uploadRole", u.isUploadRole()));
		command.setJukeboxRole(getBooleanParameter(request, "jukeboxRole", u.isJukeboxRole()));
		command.setPodcastRole(getBooleanParameter(request, "podcastRole", u.isPodcastRole()));
		command.setSettingsRole(getBooleanParameter(request, "settingsRole", u.isSettingsRole()));
		command.setShareRole(getBooleanParameter(request, "shareRole", u.isShareRole()));
		command.setLastFMRole(getBooleanParameter(request, "lastFMRole", u.isLastFMRole()));
		command.setTranscodeSchemeName(s.getTranscodeScheme().name());

		if (hasParameter(request, "password")) {
			command.setPassword(decrypt(getRequiredStringParameter(request, "password")));
			command.setPasswordChange(true);
		}

		userSettingsController.updateUser(command);
		XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
		response.getWriter().print(builder);
	}

	private boolean hasParameter(HttpServletRequest request, String name) {
		return request.getParameter(name) != null;
	}

	public void deleteUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		User user = securityService.getCurrentUser(request);
		if (!user.isAdminRole()) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to delete users.");
			return;
		}

		String username = getRequiredStringParameter(request, "username");
		if (User.USERNAME_ADMIN.equals(username)) {
			error(request, response, ErrorCode.NOT_AUTHORIZED, "Not allowed to delete admin user");
			return;
		}

		securityService.deleteUser(username);

		XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
		response.getWriter().print(builder);
	}

	public void getChatMessages(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		XMLBuilder builder = createXMLBuilder(request, response, true);

		long since = getLongParameter(request, "since", 0L);

		builder.add("chatMessages", false);

		for (ChatService.Message message : chatService.getMessages(0L).getMessages()) {
			long time = message.getDate().getTime();
			if (time > since) {
				builder.add("chatMessage", true, new Attribute("username", message.getUsername()),
						new Attribute("time", time), new Attribute("message", message.getContent()));
			}
		}
		builder.endAll();
		response.getWriter().print(builder);
	}

	public void addChatMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		chatService.doAddMessage(getRequiredStringParameter(request, "message"), request);
		XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
		response.getWriter().print(builder);
	}

	public void getLyrics(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);

		String artist = request.getParameter("artist");
		String title = request.getParameter("title");
		String id = request.getParameter("id");

		LyricsInfo lyrics = new LyricsInfo();
		if (id != null) {
			lyrics = lyricsService.getLyrics(id, artist, title);
		} else {
			lyrics = lyricsService.getLyrics(artist, title);
		}

		XMLBuilder builder = createXMLBuilder(request, response, true);
		AttributeSet attributes = new AttributeSet();
		attributes.add("artist", lyrics.getArtist());
		attributes.add("title", lyrics.getTitle());
		builder.add("lyrics", attributes, lyrics.getLyrics(), true);

		builder.endAll();
		response.getWriter().print(builder);
	}

	public void setRating(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request = wrapRequest(request);
		Integer rating = getRequiredIntParameter(request, "rating");
		if (rating == 0) {
			rating = null;
		}

		int id = getRequiredIntParameter(request, "id");
		MediaFile mediaFile = mediaFileService.getMediaFile(id);
		if (mediaFile == null) {
			error(request, response, ErrorCode.NOT_FOUND, "File not found: " + id);
			return;
		}

		String username = securityService.getCurrentUsername(request);
		ratingService.setRatingForUser(username, mediaFile, rating);

		XMLBuilder builder = createXMLBuilder(request, response, true).endAll();
		response.getWriter().print(builder);
	}

	private HttpServletRequest wrapRequest(HttpServletRequest request) {
		return wrapRequest(request, false);
	}

	private HttpServletRequest wrapRequest(final HttpServletRequest request, boolean jukebox) {
		final String playerId = createPlayerIfNecessary(request, jukebox);
		return new HttpServletRequestWrapper(request) {
			@Override
			public String getParameter(String name) {
				// Returns the correct player to be used in PlayerService.getPlayer()
				if ("player".equals(name)) {
					return playerId;
				}

				// Support old style ID parameters.
				if ("id".equals(name)) {
					return mapId(request.getParameter("id"));
				}

				return super.getParameter(name);
			}
		};
	}

	private String mapId(String id) {
		if (id == null || id.startsWith(CoverArtController.ALBUM_COVERART_PREFIX) ||
				id.startsWith(CoverArtController.ARTIST_COVERART_PREFIX) || StringUtils.isNumeric(id)) {
			return id;
		}

		try {
			String path = StringUtil.utf8HexDecode(id);
			MediaFile mediaFile = mediaFileService.getMediaFile(path);
			return String.valueOf(mediaFile.getId());
		} catch (Exception x) {
			return id;
		}
	}

	public static void error(HttpServletRequest request, HttpServletResponse response, ErrorCode code, String message) throws IOException {
		XMLBuilder builder = createXMLBuilder(request, response, false);
		builder.add("error", true,
				new XMLBuilder.Attribute("code", code.getCode()),
				new XMLBuilder.Attribute("message", message));
		builder.end();
		response.getWriter().print(builder);
	}

	private static XMLBuilder createXMLBuilder(HttpServletRequest request, HttpServletResponse response, boolean ok) throws IOException {
		String format = getStringParameter(request, "f", "xml");
		boolean json = "json".equals(format);
		boolean jsonp = "jsonp".equals(format);
		XMLBuilder builder;

		response.setCharacterEncoding(StringUtil.ENCODING_UTF8);

		if (json) {
			builder = XMLBuilder.createJSONBuilder();
			response.setContentType("application/json");
		} else if (jsonp) {
			builder = XMLBuilder.createJSONPBuilder(request.getParameter("callback"));
			response.setContentType("text/javascript");
		} else {
			builder = XMLBuilder.createXMLBuilder();
			response.setContentType("text/xml");
		}

		builder.preamble(StringUtil.ENCODING_UTF8);
		builder.add("subsonic-response", false,
				new Attribute("xmlns", "http://madsonic.org/restapi"),
				new Attribute("status", ok ? "ok" : "failed"),
				new Attribute("type", "madsonic"),
				new Attribute("version", StringUtil.getRESTProtocolVersion()));

		return builder;
	}

	private String createPlayerIfNecessary(HttpServletRequest request, boolean jukebox) {
		String username = request.getRemoteUser();
		String clientId = request.getParameter("c");
		if (jukebox) {
			clientId += "-jukebox";
		}

		List<Player> players = playerService.getPlayersForUserAndClientId(username, clientId);

		// If not found, create it.
		if (players.isEmpty()) {
			Player player = new Player();
			player.setIpAddress(request.getRemoteAddr());
			player.setUsername(username);
			player.setClientId(clientId);
			player.setName(clientId);
			player.setTechnology(jukebox ? PlayerTechnology.JUKEBOX : PlayerTechnology.EXTERNAL_WITH_PLAYLIST);
			playerService.createPlayer(player);
			players = playerService.getPlayersForUserAndClientId(username, clientId);
		}

		// Return the player ID.
		return !players.isEmpty() ? players.get(0).getId() : null;
	}

	public void setSettingsService(SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public void setPlayerService(PlayerService playerService) {
		this.playerService = playerService;
	}

	public void setTranscodingService(TranscodingService transcodingService) {
		this.transcodingService = transcodingService;
	}

	public void setDownloadController(DownloadController downloadController) {
		this.downloadController = downloadController;
	}

	public void setCoverArtController(CoverArtController coverArtController) {
		this.coverArtController = coverArtController;
	}

	public void setUserSettingsController(UserSettingsController userSettingsController) {
		this.userSettingsController = userSettingsController;
	}

	public void setLeftController(LeftController leftController) {
		this.leftController = leftController;
	}

	public void setStatusService(StatusService statusService) {
		this.statusService = statusService;
	}

	public void setPlaylistService(PlaylistService playlistService) {
		this.playlistService = playlistService;
	}

	public void setStreamController(StreamController streamController) {
		this.streamController = streamController;
	}

	public void setHlsController(HLSController hlsController) {
		this.hlsController = hlsController;
	}

	public void setChatService(ChatService chatService) {
		this.chatService = chatService;
	}

	public void setHomeController(HomeController homeController) {
		this.homeController = homeController;
	}

	public void setLyricsService(LyricsService lyricsService) {
		this.lyricsService = lyricsService;
	}

	public void setPlayQueueService(PlayQueueService playQueueService) {
		this.playQueueService = playQueueService;
	}

	public void setJukeboxService(JukeboxService jukeboxService) {
		this.jukeboxService = jukeboxService;
	}

	public void setAudioScrobblerService(AudioScrobblerService audioScrobblerService) {
		this.audioScrobblerService = audioScrobblerService;
	}

	public void setPodcastService(PodcastService podcastService) {
		this.podcastService = podcastService;
	}

	public void setRatingService(RatingService ratingService) {
		this.ratingService = ratingService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setShareService(ShareService shareService) {
		this.shareService = shareService;
	}

	public void setMediaFileService(MediaFileService mediaFileService) {
		this.mediaFileService = mediaFileService;
	}

	public void setHotService(HotService hotService) {
		this.hotService = hotService;
	}    

	public void setLastFMService(LastFMService lastFMService) {
		this.lastFMService = lastFMService;
	}   

	public void setAvatarController(AvatarController avatarController) {
		this.avatarController = avatarController;
	}

	public void setArtistDao(ArtistDao artistDao) {
		this.artistDao = artistDao;
	}

	public void setAlbumDao(AlbumDao albumDao) {
		this.albumDao = albumDao;
	}

	public void setMediaFileDao(MediaFileDao mediaFileDao) {
		this.mediaFileDao = mediaFileDao;
	}

	public void setMusicIndexService(MusicIndexService musicIndexService) {
		this.musicIndexService = musicIndexService;
	}

	public void setMediaScannerService(MediaScannerService mediaScannerService) {
		this.mediaScannerService = mediaScannerService;
	}


	public void setBookmarkDao(BookmarkDao bookmarkDao) {
		this.bookmarkDao = bookmarkDao;
	}


	public static enum ErrorCode {

		GENERIC(0, "A generic error."),
		MISSING_PARAMETER(10, "Required parameter is missing."),
		PROTOCOL_MISMATCH_CLIENT_TOO_OLD(20, "Incompatible Subsonic REST protocol version. Client must upgrade."),
		PROTOCOL_MISMATCH_SERVER_TOO_OLD(30, "Incompatible Subsonic REST protocol version. Server must upgrade."),
		NOT_AUTHENTICATED(40, "Wrong username or password."),
		NOT_AUTHORIZED(50, "User is not authorized for the given operation."),
		NOT_LICENSED(60, "The trial period for the Subsonic server is over. Please upgrade to Subsonic Premium. Visit subsonic.org for details."),
		NOT_FOUND(70, "Requested data was not found.");

		private final int code;
		private final String message;

		ErrorCode(int code, String message) {
			this.code = code;
			this.message = message;
		}

		public int getCode() {
			return code;
		}

		public String getMessage() {
			return message;
		}
	}

	private static class BookmarkKey extends Pair<String, Integer> {
		private BookmarkKey(String username, int mediaFileId) {
			super(username, mediaFileId);
		}

		static BookmarkKey forBookmark(Bookmark b) {
			return new BookmarkKey(b.getUsername(), b.getMediaFileId());
		}
	}
}
