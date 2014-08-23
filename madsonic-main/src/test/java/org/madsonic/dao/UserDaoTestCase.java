package org.madsonic.dao;

import java.util.Date;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import org.madsonic.domain.AvatarScheme;
import org.madsonic.domain.TranscodeScheme;
import org.madsonic.domain.User;
import org.madsonic.domain.UserSettings;

/**
 * Unit test of {@link UserDao}.
 *
 * @author Sindre Mehus
 */
public class UserDaoTestCase extends DaoTestCaseBase {

    @Override
    protected void setUp() throws Exception {
        JdbcTemplate template = getJdbcTemplate();
        
        template.execute("delete from user_role");
        template.execute("delete from user");
    }

    public void testCreateUser() {
        User user = new User("sindre", "secret", "sindre@activeobjects.no", false, 1000L, 2000L, 3000L, 0, false, "TEST COMMENT");
        user.setAdminRole(true);
        user.setCommentRole(true);
        user.setCoverArtRole(true);
        user.setDownloadRole(false);
        user.setPlaylistRole(true);
        user.setUploadRole(false);
        user.setPodcastRole(true);
        user.setStreamRole(true);
        user.setJukeboxRole(true);
        user.setSettingsRole(true);
        userDao.createUser(user);

        User newUser = userDao.getAllUsers().get(0);
        assertUserEquals(user, newUser);
    }

    public void testUpdateUser() {
        User user = new User("sindre", "secret", null);
        user.setAdminRole(true);
        user.setCommentRole(true);
        user.setCoverArtRole(true);
        user.setDownloadRole(false);
        user.setPlaylistRole(true);
        user.setUploadRole(false);
        user.setPodcastRole(true);
        user.setStreamRole(true);
        user.setJukeboxRole(true);
        user.setSettingsRole(true);
        userDao.createUser(user);

        user.setPassword("foo");
        user.setEmail("sindre@foo.bar");
        user.setLdapAuthenticated(true);
        user.setBytesStreamed(1);
        user.setBytesDownloaded(2);
        user.setBytesUploaded(3);
        user.setAdminRole(false);
        user.setCommentRole(false);
        user.setCoverArtRole(false);
        user.setDownloadRole(true);
        user.setPlaylistRole(false);
        user.setUploadRole(true);
        user.setPodcastRole(false);
        user.setStreamRole(false);
        user.setJukeboxRole(false);
        user.setSettingsRole(false);
        userDao.updateUser(user);

        User newUser = userDao.getAllUsers().get(0);
        assertUserEquals(user, newUser);
        assertEquals("Wrong bytes streamed.", 1, newUser.getBytesStreamed());
        assertEquals("Wrong bytes downloaded.", 2, newUser.getBytesDownloaded());
        assertEquals("Wrong bytes uploaded.", 3, newUser.getBytesUploaded());
    }

    public void testGetUserByName() {
        User user = new User("sindre", "secret", null);
        userDao.createUser(user);

        User newUser = userDao.getUserByName("sindre");
        assertNotNull("Error in getUserByName().", newUser);
        assertUserEquals(user, newUser);

        assertNull("Error in getUserByName().", userDao.getUserByName("sindre2"));
//        assertNull("Error in getUserByName().", userDao.getUserByName("sindre "));
        assertNull("Error in getUserByName().", userDao.getUserByName("bente"));
        assertNull("Error in getUserByName().", userDao.getUserByName(""));
        assertNull("Error in getUserByName().", userDao.getUserByName(null));
    }

    public void testDeleteUser() {
        assertEquals("Wrong number of users.", 0, userDao.getAllUsers().size());

        userDao.createUser(new User("sindre", "secret", null));
        assertEquals("Wrong number of users.", 1, userDao.getAllUsers().size());

        userDao.createUser(new User("bente", "secret", null));
        assertEquals("Wrong number of users.", 2, userDao.getAllUsers().size());

        userDao.deleteUser("sindre");
        assertEquals("Wrong number of users.", 1, userDao.getAllUsers().size());

        userDao.deleteUser("bente");
        assertEquals("Wrong number of users.", 0, userDao.getAllUsers().size());
    }

    public void testGetRolesForUser() {
        User user = new User("sindre", "secret", null);
        user.setAdminRole(true);
        user.setCommentRole(true);
        user.setPodcastRole(true);
        user.setStreamRole(true);
        user.setSettingsRole(true);
        userDao.createUser(user);

        String[] roles = userDao.getRolesForUser("sindre");
        assertEquals("Wrong number of roles.", 5, roles.length);
        assertEquals("Wrong role.", "admin", roles[0]);
        assertEquals("Wrong role.", "comment", roles[1]);
        assertEquals("Wrong role.", "podcast", roles[2]);
        assertEquals("Wrong role.", "stream", roles[3]);
        assertEquals("Wrong role.", "settings", roles[4]);
    }

    public void testUserSettings() {
        assertNull("Error in getUserSettings.", userDao.getUserSettings("sindre"));

        try {
            userDao.updateUserSettings(new UserSettings("sindre"));
            fail("Expected DataIntegrityViolationException.");
        } catch (DataIntegrityViolationException x) {
        }

        userDao.createUser(new User("sindre", "secret", null));
        assertNull("Error in getUserSettings.", userDao.getUserSettings("sindre"));

        userDao.updateUserSettings(new UserSettings("sindre"));
        UserSettings userSettings = userDao.getUserSettings("sindre");
        assertNotNull("Error in getUserSettings().", userSettings);
        assertNull("Error in getUserSettings().", userSettings.getLocale());
        assertNull("Error in getUserSettings().", userSettings.getThemeId());
        assertFalse("Error in getUserSettings().", userSettings.isFinalVersionNotificationEnabled());
        assertFalse("Error in getUserSettings().", userSettings.isBetaVersionNotificationEnabled());
        assertFalse("Error in getUserSettings().", userSettings.isLastFmEnabled());
        assertNull("Error in getUserSettings().", userSettings.getLastFmUsername());
        assertNull("Error in getUserSettings().", userSettings.getLastFmPassword());
        assertSame("Error in getUserSettings().", TranscodeScheme.OFF, userSettings.getTranscodeScheme());
        assertFalse("Error in getUserSettings().", userSettings.isShowNowPlayingEnabled());
        assertEquals("Error in getUserSettings().", -1, userSettings.getSelectedMusicFolderId());
        assertFalse("Error in getUserSettings().", userSettings.isPartyModeEnabled());
        assertFalse("Error in getUserSettings().", userSettings.isNowPlayingAllowed());
        assertSame("Error in getUserSettings().", AvatarScheme.NONE, userSettings.getAvatarScheme());
        assertNull("Error in getUserSettings().", userSettings.getSystemAvatarId());

        UserSettings settings = new UserSettings("sindre");
        settings.setLocale(Locale.SIMPLIFIED_CHINESE);
        settings.setThemeId("midnight");
        settings.setBetaVersionNotificationEnabled(true);
        settings.getMainVisibility().setCaptionCutoff(42);
        settings.getMainVisibility().setBitRateVisible(true);
        settings.getPlaylistVisibility().setCaptionCutoff(44);
        settings.getPlaylistVisibility().setYearVisible(true);
        settings.setLastFmEnabled(true);
        settings.setLastFmUsername("last_user");
        settings.setLastFmPassword("last_pass");
        settings.setTranscodeScheme(TranscodeScheme.MAX_192);
        settings.setShowNowPlayingEnabled(false);
        settings.setSelectedMusicFolderId(3);
        settings.setPartyModeEnabled(true);
        settings.setNowPlayingAllowed(true);
        settings.setAvatarScheme(AvatarScheme.SYSTEM);
        settings.setSystemAvatarId(1);
        settings.setChanged(new Date(9412L));

        userDao.updateUserSettings(settings);
        userSettings = userDao.getUserSettings("sindre");
        assertNotNull("Error in getUserSettings().", userSettings);
        assertEquals("Error in getUserSettings().", Locale.SIMPLIFIED_CHINESE, userSettings.getLocale());
        assertEquals("Error in getUserSettings().", false, userSettings.isFinalVersionNotificationEnabled());
        assertEquals("Error in getUserSettings().", true, userSettings.isBetaVersionNotificationEnabled());
        assertEquals("Error in getUserSettings().", "midnight", userSettings.getThemeId());
        assertEquals("Error in getUserSettings().", 42, userSettings.getMainVisibility().getCaptionCutoff());
        assertEquals("Error in getUserSettings().", true, userSettings.getMainVisibility().isBitRateVisible());
        assertEquals("Error in getUserSettings().", 44, userSettings.getPlaylistVisibility().getCaptionCutoff());
        assertEquals("Error in getUserSettings().", true, userSettings.getPlaylistVisibility().isYearVisible());
        assertEquals("Error in getUserSettings().", true, userSettings.isLastFmEnabled());
        assertEquals("Error in getUserSettings().", "last_user", userSettings.getLastFmUsername());
        assertEquals("Error in getUserSettings().", "last_pass", userSettings.getLastFmPassword());
        assertSame("Error in getUserSettings().", TranscodeScheme.MAX_192, userSettings.getTranscodeScheme());
        assertFalse("Error in getUserSettings().", userSettings.isShowNowPlayingEnabled());
        assertEquals("Error in getUserSettings().", 3, userSettings.getSelectedMusicFolderId());
        assertTrue("Error in getUserSettings().", userSettings.isPartyModeEnabled());
        assertTrue("Error in getUserSettings().", userSettings.isNowPlayingAllowed());
        assertSame("Error in getUserSettings().", AvatarScheme.SYSTEM, userSettings.getAvatarScheme());
        assertEquals("Error in getUserSettings().", 1, userSettings.getSystemAvatarId().intValue());
        assertEquals("Error in getUserSettings().", new Date(9412L), userSettings.getChanged());

        userDao.deleteUser("sindre");
        assertNull("Error in cascading delete.", userDao.getUserSettings("sindre"));
    }

    private void assertUserEquals(User expected, User actual) {
        assertEquals("Wrong name.", expected.getUsername(), actual.getUsername());
        assertEquals("Wrong password.", expected.getPassword(), actual.getPassword());
        assertEquals("Wrong email.", expected.getEmail(), actual.getEmail());
        assertEquals("Wrong LDAP auth.", expected.isLdapAuthenticated(), actual.isLdapAuthenticated());
        assertEquals("Wrong bytes streamed.", expected.getBytesStreamed(), actual.getBytesStreamed());
        assertEquals("Wrong bytes downloaded.", expected.getBytesDownloaded(), actual.getBytesDownloaded());
        assertEquals("Wrong bytes uploaded.", expected.getBytesUploaded(), actual.getBytesUploaded());
        assertEquals("Wrong admin role.", expected.isAdminRole(), actual.isAdminRole());
        assertEquals("Wrong comment role.", expected.isCommentRole(), actual.isCommentRole());
        assertEquals("Wrong cover art role.", expected.isCoverArtRole(), actual.isCoverArtRole());
        assertEquals("Wrong download role.", expected.isDownloadRole(), actual.isDownloadRole());
        assertEquals("Wrong playlist role.", expected.isPlaylistRole(), actual.isPlaylistRole());
        assertEquals("Wrong upload role.", expected.isUploadRole(), actual.isUploadRole());
        assertEquals("Wrong upload role.", expected.isUploadRole(), actual.isUploadRole());
        assertEquals("Wrong stream role.", expected.isStreamRole(), actual.isStreamRole());
        assertEquals("Wrong jukebox role.", expected.isJukeboxRole(), actual.isJukeboxRole());
        assertEquals("Wrong settings role.", expected.isSettingsRole(), actual.isSettingsRole());
    }
}