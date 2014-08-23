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
package org.madsonic.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import org.madsonic.Logger;
import org.madsonic.domain.AvatarScheme;
import org.madsonic.domain.TranscodeScheme;
import org.madsonic.domain.User;
import org.madsonic.domain.UserSettings;
import org.madsonic.util.StringUtil;

/**
 * Provides user-related database services.
 *
 * @author Sindre Mehus
 */
public class UserDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(UserDao.class);
    private static final String USER_COLUMNS = "username, password, email, ldap_authenticated, bytes_streamed, bytes_downloaded, bytes_uploaded, group_id, locked, comment";
    private static final String USER_SETTINGS_COLUMNS = "username, locale, theme_id, final_version_notification, beta_version_notification, " +
            "main_caption_cutoff, main_disc_number, main_track_number, main_artist, main_album, main_genre, main_mood, " +
            "main_year, main_bit_rate, main_duration, main_format, main_file_size, " +
            "playlist_caption_cutoff, playlist_disc_number, playlist_track_number, playlist_artist, playlist_album, playlist_genre, playlist_mood, " +
            "playlist_year, playlist_bit_rate, playlist_duration, playlist_format, playlist_file_size, " +
            "button_rank, button_starred, button_play, button_playadd, button_playmore, button_addcontext, button_addnext, button_addlast, button_download, button_youtube, " + 
            "last_fm_enabled, last_fm_username, last_fm_password, transcode_scheme, show_now_playing, selected_music_folder_id, selected_genre, " +
            "party_mode_enabled, now_playing_allowed, avatar_scheme, system_avatar_id, changed, show_chat, autohide_chat, list_type, " +
            "list_rows, list_columns, playqueue_resize, leftframe_resize, customscrollbar, customaccordion";

    private static final Integer ROLE_ID_ADMIN = 1;
    private static final Integer ROLE_ID_DOWNLOAD = 2;
    private static final Integer ROLE_ID_UPLOAD = 3;
    private static final Integer ROLE_ID_PLAYLIST = 4;
    private static final Integer ROLE_ID_COVER_ART = 5;
    private static final Integer ROLE_ID_COMMENT = 6;
    private static final Integer ROLE_ID_PODCAST = 7;
    private static final Integer ROLE_ID_STREAM = 8;
    private static final Integer ROLE_ID_SETTINGS = 9;
    private static final Integer ROLE_ID_JUKEBOX = 10;
    private static final Integer ROLE_ID_SHARE = 11;
    private static final Integer ROLE_ID_SEARCH = 12;
    private static final Integer ROLE_ID_LASTFM = 13;

    private UserRowMapper userRowMapper = new UserRowMapper();
    private UserSettingsRowMapper userSettingsRowMapper = new UserSettingsRowMapper();

    /**
     * Returns the user with the given username.
     *
     * @param username The username used when logging in.
     * @return The user, or <code>null</code> if not found.
     */
    public User getUserByName(String username) {
        String sql = "select " + USER_COLUMNS + " from user where username=?";
        return queryOne(sql, userRowMapper, username);
    }

    /**
     * Returns the user with the given email address.
     *
     * @param email The email address.
     * @return The user, or <code>null</code> if not found.
     */
    public User getUserByEmail(String email) {
        String sql = "select " + USER_COLUMNS + " from user where email=?";
        return queryOne(sql, userRowMapper, email);
    }

    public int getUserAccess(String username) {
        String sql = "select group_id from user where username=?";
        return queryForInt(sql, 0, username);
    }    

    /**
     * Returns all users.
     *
     * @return Possibly empty array of all users.
     */
    public List<User> getAllUsers() {
        String sql = "select " + USER_COLUMNS + " from user";
        return query(sql, userRowMapper);
    }

    /**
     * Creates a new user.
     *
     * @param user The user to create.
     */
    public void createUser(User user) {
        String sql = "insert into user (" + USER_COLUMNS + ") values (" + questionMarks(USER_COLUMNS) + ')';
        update(sql, user.getUsername(), encrypt(user.getPassword()), user.getEmail(), user.isLdapAuthenticated(),
                user.getBytesStreamed(), user.getBytesDownloaded(), user.getBytesUploaded(), user.getGroupId(), user.isLocked(), user.getComment());
        writeRoles(user);
    }

    /**
     * Deletes the user with the given username.
     *
     * @param username The username.
     */
    public void deleteUser(String username) {
        if (User.USERNAME_ADMIN.equals(username)) {
            throw new IllegalArgumentException("Can't delete admin user.");
        }

        String sql = "delete from user_role where username=?";
        update(sql, username);

        sql = "delete from user where username=?";
        update(sql, username);
    }

    /**
     * Updates the given user.
     *
     * @param user The user to update.
     */
    public void updateUser(User user) {
        String sql = "update user set password=?, email=?, ldap_authenticated=?, bytes_streamed=?, bytes_downloaded=?, bytes_uploaded=?, group_id=?, locked=?, comment=? " +
                "where username=?";
        getJdbcTemplate().update(sql, new Object[]{encrypt(user.getPassword()), user.getEmail(), user.isLdapAuthenticated(),
                user.getBytesStreamed(), user.getBytesDownloaded(), user.getBytesUploaded(),
                user.getGroupId(), user.isLocked(), user.getComment(), user.getUsername() });
        writeRoles(user);
    }

    /**
     * Returns the name of the roles for the given user.
     *
     * @param username The user name.
     * @return Roles the user is granted.
     */
    public String[] getRolesForUser(String username) {
        String sql = "select r.name from role r, user_role ur " +
                "where ur.username=? and ur.role_id=r.id";
        List<?> roles = getJdbcTemplate().queryForList(sql, new Object[]{username}, String.class);
        String[] result = new String[roles.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = (String) roles.get(i);
        }
        return result;
    }

    /**
     * Returns settings for the given user.
     *
     * @param username The username.
     * @return User-specific settings, or <code>null</code> if no such settings exist.
     */
    public UserSettings getUserSettings(String username) {
        String sql = "select " + USER_SETTINGS_COLUMNS + " from user_settings where username=?";
        return queryOne(sql, userSettingsRowMapper, username);
    }

  
    /**
     * Updates settings for the given username, creating it if necessary.
     *
     * @param settings The user-specific settings.
     */
    public void updateUserSettings(UserSettings settings) {
        getJdbcTemplate().update("delete from user_settings where username=?", new Object[]{settings.getUsername()});

        String sql = "insert into user_settings (" + USER_SETTINGS_COLUMNS + ") values (" + questionMarks(USER_SETTINGS_COLUMNS) + ')';
        String locale = settings.getLocale() == null ? null : settings.getLocale().toString();
        UserSettings.Visibility main = settings.getMainVisibility();
        UserSettings.Visibility playlist = settings.getPlaylistVisibility();
        UserSettings.ButtonVisibility button = settings.getButtonVisibility();
        getJdbcTemplate().update(sql, new Object[]{settings.getUsername(), locale, settings.getThemeId(),
                settings.isFinalVersionNotificationEnabled(), settings.isBetaVersionNotificationEnabled(),
                main.getCaptionCutoff(), main.isDiscNumberVisible(), main.isTrackNumberVisible(), main.isArtistVisible(), main.isAlbumVisible(),
                main.isGenreVisible(), main.isMoodVisible(), main.isYearVisible(), main.isBitRateVisible(), main.isDurationVisible(),
                main.isFormatVisible(), main.isFileSizeVisible(),
                playlist.getCaptionCutoff(), playlist.isDiscNumberVisible(), playlist.isTrackNumberVisible(), playlist.isArtistVisible(), playlist.isAlbumVisible(),
                playlist.isGenreVisible(), playlist.isMoodVisible(), playlist.isYearVisible(), playlist.isBitRateVisible(), playlist.isDurationVisible(),
                playlist.isFormatVisible(), playlist.isFileSizeVisible(),
                button.isRankVisible(), button.isStarredVisible(), button.isPlayVisible(), button.isPlayAddVisible(),button.isPlayMoreVisible(),button.isAddContextVisible(), button.isAddNextVisible(), button.isAddLastVisible(), button.isDownloadVisible(), button.isYoutubeVisible(), 
                settings.isLastFmEnabled(), settings.getLastFmUsername(), encrypt(settings.getLastFmPassword()),
                settings.getTranscodeScheme().name(), settings.isShowNowPlayingEnabled(),
                settings.getSelectedMusicFolderId(), settings.getSelectedGenre(), settings.isPartyModeEnabled(), settings.isNowPlayingAllowed(),
                settings.getAvatarScheme().name(), settings.getSystemAvatarId(), settings.getChanged(), 
                settings.isShowChatEnabled(), settings.isAutoHideChat(), settings.getListType(), settings.getListRows(), settings.getListColumns(), 
                settings.getPlayQueueResize(), settings.getLeftFrameResize(), settings.isCustomScrollbarEnabled(), settings.isCustomAccordionEnabled() });
    }

    private static String encrypt(String s) {
        if (s == null) {
            return null;
        }
        try {
            return "enc:" + StringUtil.utf8HexEncode(s);
        } catch (Exception e) {
            return s;
        }
    }

    private static String decrypt(String s) {
        if (s == null) {
            return null;
        }
        if (!s.startsWith("enc:")) {
            return s;
        }
        try {
            return StringUtil.utf8HexDecode(s.substring(4));
        } catch (Exception e) {
            return s;
        }
    }

    private void readRoles(User user) {
        synchronized (user.getUsername().intern()) {
            String sql = "select role_id from user_role where username=?";
            List<?> roles = getJdbcTemplate().queryForList(sql, new Object[]{user.getUsername()}, Integer.class);
            for (Object role : roles) {
                if (ROLE_ID_ADMIN.equals(role)) {
                    user.setAdminRole(true);
                } else if (ROLE_ID_DOWNLOAD.equals(role)) {
                    user.setDownloadRole(true);
                } else if (ROLE_ID_UPLOAD.equals(role)) {
                    user.setUploadRole(true);
                } else if (ROLE_ID_PLAYLIST.equals(role)) {
                    user.setPlaylistRole(true);
                } else if (ROLE_ID_COVER_ART.equals(role)) {
                    user.setCoverArtRole(true);
                } else if (ROLE_ID_COMMENT.equals(role)) {
                    user.setCommentRole(true);
                } else if (ROLE_ID_PODCAST.equals(role)) {
                    user.setPodcastRole(true);
                } else if (ROLE_ID_STREAM.equals(role)) {
                    user.setStreamRole(true);
                } else if (ROLE_ID_SETTINGS.equals(role)) {
                    user.setSettingsRole(true);
                } else if (ROLE_ID_JUKEBOX.equals(role)) {
                    user.setJukeboxRole(true);
                } else if (ROLE_ID_SHARE.equals(role)) {
                    user.setShareRole(true);
                } else if (ROLE_ID_SEARCH.equals(role)) {
                    user.setSearchRole(true);
                } else if (ROLE_ID_LASTFM.equals(role)) {
                    user.setLastFMRole(true);
                } else {
                    LOG.warn("Unknown role: '" + role + '\'');
                }
            }
        }
    }

    private void writeRoles(User user) {
        synchronized (user.getUsername().intern()) {
            String sql = "delete from user_role where username=?";
            getJdbcTemplate().update(sql, new Object[]{user.getUsername()});
            sql = "insert into user_role (username, role_id) values(?, ?)";
            if (user.isAdminRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_ADMIN});
            }
            if (user.isDownloadRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_DOWNLOAD});
            }
            if (user.isUploadRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_UPLOAD});
            }
            if (user.isPlaylistRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_PLAYLIST});
            }
            if (user.isCoverArtRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_COVER_ART});
            }
            if (user.isCommentRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_COMMENT});
            }
            if (user.isPodcastRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_PODCAST});
            }
            if (user.isStreamRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_STREAM});
            }
            if (user.isJukeboxRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_JUKEBOX});
            }
            if (user.isSettingsRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_SETTINGS});
            }
            if (user.isShareRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_SHARE});
            }
            if (user.isSearchRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_SEARCH});
            }
            if (user.isLastFMRole()) {
                getJdbcTemplate().update(sql, new Object[]{user.getUsername(), ROLE_ID_LASTFM});
            }
        }
    }

    private class UserRowMapper implements ParameterizedRowMapper<User> {
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User(rs.getString(1), decrypt(rs.getString(2)), rs.getString(3), rs.getBoolean(4),
                                 rs.getLong(5), rs.getLong(6), rs.getLong(7), rs.getInt(8), rs.getBoolean(9),rs.getString(10) );
            readRoles(user);
            return user;
        }
    }

    private static class UserSettingsRowMapper implements ParameterizedRowMapper<UserSettings> {
        public UserSettings mapRow(ResultSet rs, int rowNum) throws SQLException {
            int col = 1;
            UserSettings settings = new UserSettings(rs.getString(col++));
            settings.setLocale(StringUtil.parseLocale(rs.getString(col++)));
            settings.setThemeId(rs.getString(col++));
            settings.setFinalVersionNotificationEnabled(rs.getBoolean(col++));
            settings.setBetaVersionNotificationEnabled(rs.getBoolean(col++));

            settings.getMainVisibility().setCaptionCutoff(rs.getInt(col++));
            settings.getMainVisibility().setDiscNumberVisible(rs.getBoolean(col++));
            settings.getMainVisibility().setTrackNumberVisible(rs.getBoolean(col++));
            settings.getMainVisibility().setArtistVisible(rs.getBoolean(col++));
            settings.getMainVisibility().setAlbumVisible(rs.getBoolean(col++));
            settings.getMainVisibility().setGenreVisible(rs.getBoolean(col++));
            settings.getMainVisibility().setMoodVisible(rs.getBoolean(col++));
            settings.getMainVisibility().setYearVisible(rs.getBoolean(col++));
            settings.getMainVisibility().setBitRateVisible(rs.getBoolean(col++));
            settings.getMainVisibility().setDurationVisible(rs.getBoolean(col++));
            settings.getMainVisibility().setFormatVisible(rs.getBoolean(col++));
            settings.getMainVisibility().setFileSizeVisible(rs.getBoolean(col++));

            settings.getPlaylistVisibility().setCaptionCutoff(rs.getInt(col++));
            settings.getPlaylistVisibility().setDiscNumberVisible(rs.getBoolean(col++));
            settings.getPlaylistVisibility().setTrackNumberVisible(rs.getBoolean(col++));
            settings.getPlaylistVisibility().setArtistVisible(rs.getBoolean(col++));
            settings.getPlaylistVisibility().setAlbumVisible(rs.getBoolean(col++));
            settings.getPlaylistVisibility().setGenreVisible(rs.getBoolean(col++));
            settings.getPlaylistVisibility().setMoodVisible(rs.getBoolean(col++));
            settings.getPlaylistVisibility().setYearVisible(rs.getBoolean(col++));
            settings.getPlaylistVisibility().setBitRateVisible(rs.getBoolean(col++));
            settings.getPlaylistVisibility().setDurationVisible(rs.getBoolean(col++));
            settings.getPlaylistVisibility().setFormatVisible(rs.getBoolean(col++));
            settings.getPlaylistVisibility().setFileSizeVisible(rs.getBoolean(col++));
            
            settings.getButtonVisibility().setRankVisible(rs.getBoolean(col++));
            settings.getButtonVisibility().setStarredVisible(rs.getBoolean(col++));
            settings.getButtonVisibility().setPlayVisible(rs.getBoolean(col++));
            settings.getButtonVisibility().setPlayAddVisible(rs.getBoolean(col++));
            settings.getButtonVisibility().setPlayMoreVisible(rs.getBoolean(col++));
            settings.getButtonVisibility().setAddContextVisible(rs.getBoolean(col++));
            settings.getButtonVisibility().setAddNextVisible(rs.getBoolean(col++));
            settings.getButtonVisibility().setAddLastVisible(rs.getBoolean(col++));
            settings.getButtonVisibility().setDownloadVisible(rs.getBoolean(col++));
            settings.getButtonVisibility().setYoutubeVisible(rs.getBoolean(col++));
            
            settings.setLastFmEnabled(rs.getBoolean(col++));
            settings.setLastFmUsername(rs.getString(col++));
            settings.setLastFmPassword(decrypt(rs.getString(col++)));

            settings.setTranscodeScheme(TranscodeScheme.valueOf(rs.getString(col++)));
            settings.setShowNowPlayingEnabled(rs.getBoolean(col++));
            settings.setSelectedMusicFolderId(rs.getInt(col++));
            settings.setSelectedGenre(rs.getString(col++));
            settings.setPartyModeEnabled(rs.getBoolean(col++));
            settings.setNowPlayingAllowed(rs.getBoolean(col++));
            settings.setAvatarScheme(AvatarScheme.valueOf(rs.getString(col++)));
            settings.setSystemAvatarId((Integer) rs.getObject(col++));
            settings.setChanged(rs.getTimestamp(col++));
            settings.setShowChatEnabled(rs.getBoolean(col++));
            settings.setAutoHideChat(rs.getBoolean(col++));
            settings.setListType(rs.getString(col++));
            settings.setListRows(rs.getInt(col++));
            settings.setListColumns(rs.getInt(col++));
            settings.setPlayQueueResize(rs.getBoolean(col++));
            settings.setLeftFrameResize(rs.getBoolean(col++));
            settings.setCustomScrollbarEnabled(rs.getBoolean(col++));
            settings.setCustomAccordionEnabled(rs.getBoolean(col++));
            return settings;
        }
    }

}
