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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.RandomStringUtils;

import org.madsonic.Logger;
import org.madsonic.dao.ShareDao;
import org.madsonic.domain.MediaFile;
import org.madsonic.domain.Share;
import org.madsonic.domain.User;

/**
 * Provides services for sharing media.
 *
 * @author Sindre Mehus
 * @see Share
 */
public class ShareService {

    private static final Logger LOG = Logger.getLogger(ShareService.class);

    private ShareDao shareDao;
    private SecurityService securityService;
    private SettingsService settingsService;
    private MediaFileService mediaFileService;

    public List<Share> getAllShares() {
        return shareDao.getAllShares();
    }

    public List<Share> getSharesForUser(User user) {
        List<Share> result = new ArrayList<Share>();
        for (Share share : getAllShares()) {
            if (user.isAdminRole() || ObjectUtils.equals(user.getUsername(), share.getUsername())) {
                result.add(share);
            }
        }
        return result;
    }

    public Share getShareById(int id) {
        return shareDao.getShareById(id);
    }

    public Share getShareByName(String name) {
    	return shareDao.getShareByName(name);
    }
    
    public List<MediaFile> getSharedFiles(int id) {
        List<MediaFile> result = new ArrayList<MediaFile>();
        for (String path : shareDao.getSharedFiles(id)) {
            try {
                MediaFile mediaFile = mediaFileService.getMediaFile(path);
                if (mediaFile != null) {
                    result.add(mediaFile);
                }
            } catch (Exception x) {
                // Ignored
            }
        }
        return result;
    }

    public Share createShare(HttpServletRequest request, List<MediaFile> files) throws Exception {

        Share share = new Share();
        share.setName(RandomStringUtils.random(5, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        share.setCreated(new Date());
        share.setUsername(securityService.getCurrentUsername(request));

        Calendar expires = Calendar.getInstance();
        expires.add(Calendar.YEAR, 1);
        share.setExpires(expires.getTime());

        shareDao.createShare(share);
        for (MediaFile file : files) {
            shareDao.createSharedFiles(share.getId(), file.getPath());
        }
        LOG.info("Created share '" + share.getName() + "' with " + files.size() + " file(s).");

        return share;
    }

    public void updateShare(Share share) {
        shareDao.updateShare(share);
    }

    public void deleteShare(int id) {
        shareDao.deleteShare(id);
    }

    public String getShareBaseUrl() {
    	
       if (settingsService.isUsePremiumServices()) {
    	    return "http://" + settingsService.getUrlRedirectFrom() + ".subsonic.org/share/";
       } 
       else {
    	    return settingsService.getMadsonicUrl() + "/share/";    	   
       }
    	   
    }

    public String getShareUrl(Share share) {
        return getShareBaseUrl() + share.getName();
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setShareDao(ShareDao shareDao) {
        this.shareDao = shareDao;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }
}