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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import org.madsonic.domain.MediaFile;
import org.madsonic.domain.Share;
import org.madsonic.domain.User;
import org.madsonic.domain.UserSettings;
import org.madsonic.service.MediaFileService;
import org.madsonic.service.SecurityService;
import org.madsonic.service.SettingsService;
import org.madsonic.service.ShareService;

/**
 * Controller for the page used to administrate the set of shared media.
 *
 * @author Sindre Mehus
 */
public class ShareSettingsController extends ParameterizableViewController {

    private ShareService shareService;
    private SecurityService securityService;
    private MediaFileService mediaFileService;
    private SettingsService settingsService;
    
    protected Map<String, Boolean> referenceData(HttpServletRequest request) throws Exception {
    Map<String, Boolean> model = new HashMap<String, Boolean>();
    
    User user = securityService.getCurrentUser(request);
    UserSettings userSettings = settingsService.getUserSettings(user.getUsername());

    model.put("customScrollbar", userSettings.isCustomScrollbarEnabled());     
    return model;
    }       
    
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();

        if (isFormSubmission(request)) {
            String error = handleParameters(request);
            map.put("error", error);
            map.put("toast", true);
        }

        ModelAndView result = super.handleRequestInternal(request, response);
        map.put("shareBaseUrl", shareService.getShareBaseUrl());
        map.put("shareInfos", getShareInfos(request));
        map.put("user", securityService.getCurrentUser(request));
        map.put("licenseInfo", settingsService.getLicenseInfo());

        result.addObject("model", map);
        return result;
    }

    /**
     * Determine if the given request represents a form submission.
     *
     * @param request current HTTP request
     * @return if the request represents a form submission
     */
    private boolean isFormSubmission(HttpServletRequest request) {
        return "POST".equals(request.getMethod());
    }

    private String handleParameters(HttpServletRequest request) {
        User user = securityService.getCurrentUser(request);
        for (Share share : shareService.getSharesForUser(user)) {
            int id = share.getId();

            String description = getParameter(request, "description", id);
            boolean delete = getParameter(request, "delete", id) != null;
            String expireIn = getParameter(request, "expireIn", id);

            if (delete) {
                shareService.deleteShare(id);
            } else {
                if (expireIn != null) {
                    share.setExpires(parseExpireIn(expireIn));
                }
                share.setDescription(description);
                shareService.updateShare(share);
            }
        }

        return null;
    }

    private List<ShareInfo> getShareInfos(HttpServletRequest request) {
        List<ShareInfo> result = new ArrayList<ShareInfo>();
        User user = securityService.getCurrentUser(request);
        for (Share share : shareService.getSharesForUser(user)) {
            List<MediaFile> files = shareService.getSharedFiles(share.getId());
            if (!files.isEmpty()) {
                MediaFile file = files.get(0);
                result.add(new ShareInfo(share, file.isDirectory() ? file : mediaFileService.getParentOf(file)));
            }
        }
        return result;
    }


    private String getParameter(HttpServletRequest request, String name, int id) {
        return StringUtils.trimToNull(request.getParameter(name + "[" + id + "]"));
    }

    private Date parseExpireIn(String expireIn) {
        int days = Integer.parseInt(expireIn);
        if (days == 0) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setShareService(ShareService shareService) {
        this.shareService = shareService;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
    
    public static class ShareInfo {
        private final Share share;
        private final MediaFile dir;

        public ShareInfo(Share share, MediaFile dir) {
            this.share = share;
            this.dir = dir;
        }

        public Share getShare() {
            return share;
        }

        public MediaFile getDir() {
            return dir;
        }
    }
}