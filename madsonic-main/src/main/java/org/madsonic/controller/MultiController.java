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
import org.madsonic.domain.Playlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.madsonic.domain.User;
import org.madsonic.domain.UserSettings;
import org.madsonic.service.PlaylistService;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;
import org.madsonic.util.StringUtil;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.view.RedirectView;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;
import net.tanesha.recaptcha.ReCaptchaResponse;

/**
 * Multi-controller used for simple pages.
 *
 * @author Sindre Mehus
 */
public class MultiController extends MultiActionController {

    private static final Logger LOG = Logger.getLogger(MultiController.class);

    private SecurityService securityService;
    private SettingsService settingsService;
    private PlaylistService playlistService;

    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws Exception {

//        User defaultUser = securityService.getUserByName(User.USERNAME_DEFAULT);
    	
    	// Auto-login if "user" and "password" parameters are given.
        String username = request.getParameter("user");
        String password = request.getParameter("password");
        
        if (username != null && password != null) {
        	
            username = StringUtil.urlEncode(username);
            password = StringUtil.urlEncode(password);
            
//          password = StringUtil.urlEncode(decrypt(password));
            
            // LOCKOUT default user for logon
            if (username.equalsIgnoreCase("default")  ) {
                Map<String, Object> map = new HashMap<String, Object>();
            	map.put("username", "");
            	map.put("password", "");
                return new ModelAndView("login", "model", map);
            }
            
            return new ModelAndView(new RedirectView("j_spring_security_check?j_username=" + username + 
					     "&j_password=" + password + "&_spring_security_remember_me=checked"));
        }
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("logout", request.getParameter("logout") != null);
        map.put("error", request.getParameter("error") != null);
        map.put("brand", settingsService.getBrand());
        map.put("loginMessage", settingsService.getLoginMessage());

        User admin = securityService.getUserByName(User.USERNAME_ADMIN);
        
        if (admin != null) {
	        if ("a40546cc4fd6a12572828bb803380888ad1bfdab".equals(admin.getPassword())) {
	            map.put("insecure", true);
	        }
        }
       return new ModelAndView("login", "model", map);
    }

    public ModelAndView recover(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        String usernameOrEmail = StringUtils.trimToNull(request.getParameter("usernameOrEmail"));

        ReCaptcha captcha;

        if (settingsService.getHttpsPort() != 0) {
            captcha = ReCaptchaFactory.newSecureReCaptcha(
            		"6LcZ3OMSAAAAANkKMdFdaNopWu9iS03V-nLOuoiH", 
            		"6LcZ3OMSAAAAAPaFg89mEzs-Ft0fIu7wxfKtkwmQ", false);
        } else {
            captcha = ReCaptchaFactory.newReCaptcha(
            		"6LcZ3OMSAAAAANkKMdFdaNopWu9iS03V-nLOuoiH", 
            		"6LcZ3OMSAAAAAPaFg89mEzs-Ft0fIu7wxfKtkwmQ", false);
        }
        
        boolean showCaptcha = true;

        if (usernameOrEmail != null) {

            map.put("usernameOrEmail", usernameOrEmail);
            User user = getUserByUsernameOrEmail(usernameOrEmail);
            String challenge = request.getParameter("recaptcha_challenge_field");
            String uresponse = request.getParameter("recaptcha_response_field");
            ReCaptchaResponse captchaResponse = captcha.checkAnswer(request.getRemoteAddr(), challenge, uresponse);

            if (!captchaResponse.isValid()) {
                map.put("error", "recover.error.invalidcaptcha");
            } else if (user == null) {
                map.put("error", "recover.error.usernotfound");
            } else if (user.getEmail() == null) {
                map.put("error", "recover.error.noemail");
            } else {
                String password = RandomStringUtils.randomAlphanumeric(8);
                if (emailPassword(password, user.getUsername(), user.getEmail())) {
                    map.put("sentTo", user.getEmail());
                    user.setLdapAuthenticated(false);
                    user.setPassword(password);
                    securityService.updateUser(user);
                    showCaptcha = false;
                } else {
                    map.put("error", "recover.error.sendfailed");
                }
            }
        }

        if (showCaptcha) {
            map.put("captcha", captcha.createRecaptchaHtml(null, null));
        }

        return new ModelAndView("recover", "model", map);
    }

    private boolean emailPassword(String password, String username, String email) {
        HttpClient client = new DefaultHttpClient();
        try {
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 10000);
            HttpPost method = new HttpPost("http://subsonic.org/backend/sendMail.view");

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("from", "noreply@subsonic.org"));
            params.add(new BasicNameValuePair("to", email));
            params.add(new BasicNameValuePair("subject", "Subsonic Password"));
            params.add(new BasicNameValuePair("text",
                    "Hi there!\n\n" +
                            "You have requested to reset your Subsonic password.  Please find your new login details below.\n\n" +
                            "Username: " + username + "\n" +
                            "Password: " + password + "\n\n" +
                            "--\n" +
                            "The Subsonic Team\n" +
                            "subsonic.org"));
            method.setEntity(new UrlEncodedFormEntity(params, StringUtil.ENCODING_UTF8));
            client.execute(method);
            return true;
        } catch (Exception x) {
            LOG.warn("Failed to send email.", x);
            return false;
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    private User getUserByUsernameOrEmail(String usernameOrEmail) {
        if (usernameOrEmail != null) {
            User user = securityService.getUserByName(usernameOrEmail);
            if (user != null) {
                return user;
            }
            return securityService.getUserByEmail(usernameOrEmail);
        }
        return null;
    }

    public ModelAndView accessDenied(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("accessDenied");
    }

    public ModelAndView notFound(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("notFound");
    }

    public ModelAndView gettingStarted(HttpServletRequest request, HttpServletResponse response) {
        updatePortAndContextPath(request);

        if (request.getParameter("hide") != null) {
            settingsService.setGettingStartedEnabled(false);
            settingsService.save();
            return new ModelAndView(new RedirectView("home.view"));
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("runningAsRoot", "root".equals(System.getProperty("user.name")));
        return new ModelAndView("gettingStarted", "model", map);
    }

    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        updatePortAndContextPath(request);
        UserSettings userSettings = settingsService.getUserSettings(securityService.getCurrentUsername(request));

        Map<String, Object> map = new HashMap<String, Object>();
        
        map.put("showRight", userSettings.isShowNowPlayingEnabled() || userSettings.isShowChatEnabled());
        map.put("autohideChat", userSettings.isAutoHideChat());
        
        map.put("pageTitle", settingsService.getPageTitle());

        map.put("PlayQueueResizeable", userSettings.isPlayQueueResizeEnabled()); 
        map.put("LeftFrameResizeable", userSettings.isLeftFrameResizeEnabled()); 

        map.put("leftframeSize", settingsService.getLeftframeSize()); 
        map.put("playQueueSize", settingsService.getPlayqueueSize()); 

        map.put("customScrollbar", userSettings.isCustomScrollbarEnabled()); 
        map.put("brand", settingsService.getBrand());
        map.put("listType", userSettings.getListType());
        map.put("listRows", userSettings.getListRows());
        map.put("listColumns", userSettings.getListColumns());
        
        return new ModelAndView("index", "model", map);
    }

    public ModelAndView exportPlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception {

        int id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        Playlist playlist = playlistService.getPlaylist(id);
        if (!playlistService.isReadAllowed(playlist, securityService.getCurrentUsername(request))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;

        }
        response.setContentType("application/x-download");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + StringUtil.fileSystemSafe(playlist.getName()) + ".m3u8\"");

        playlistService.exportPlaylist(id, response.getOutputStream());
        return null;
    }

    private void updatePortAndContextPath(HttpServletRequest request) {

        int port = Integer.parseInt(System.getProperty("madsonic.port", String.valueOf(request.getLocalPort())));
        int httpsPort = Integer.parseInt(System.getProperty("madsonic.httpsPort", "0"));

        String contextPath = request.getContextPath().replace("/", "");

        if (settingsService.getPort() != port) {
            settingsService.setPort(port);
            settingsService.save();
        }
        if (settingsService.getHttpsPort() != httpsPort) {
            settingsService.setHttpsPort(httpsPort);
            settingsService.save();
        }
        if (!ObjectUtils.equals(settingsService.getUrlRedirectContextPath(), contextPath)) {
            settingsService.setUrlRedirectContextPath(contextPath);
            settingsService.save();
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
    
    public ModelAndView test(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("test");
    }
	
    public ModelAndView chat(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("chat");
    }

    public ModelAndView loginfb(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("loginfb");
    }    
    
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }
}