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

import org.madsonic.domain.Group;
import org.madsonic.service.SecurityService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the page used to administrate the set of internet radio/tv stations.
 *
 * @author Sindre Mehus
 */
public class GroupSettingsController extends ParameterizableViewController {

    private SecurityService securityService;
    
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        if (isFormSubmission(request)) {
            String error = handleParameters(request);
            map.put("error", error);
            if (error == null) {
                map.put("reload", true);
				map.put("toast", true);
            }
        }
        ModelAndView result = super.handleRequestInternal(request, response);
        map.put("groups", securityService.GetAllGroups());
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
    	
    	List<Group> groups = securityService.GetAllGroups();
        
        for (Group group: groups) {
        	
            Integer id = group.getId();
            String name = getParameter(request, "name", id);
            boolean delete = getParameter(request, "delete", id) != null;

//          Integer audioDefaultBitrate = Integer.parseInt(getParameter(request, "audioDefaultBitrate", id)) ;
//          Integer audioMaxBitrate = Integer.parseInt(getParameter(request, "audioMaxBitrate", id)) ;

            Integer videoDefaultBitrate = Integer.parseInt(getParameter(request, "videoDefaultBitrate", id)) ;
            Integer videoMaxBitrate = Integer.parseInt(getParameter(request, "videoMaxBitrate", id)) ;
			
            if (delete) {
                securityService.deleteGroup(group);
            } else {
//              securityService.updateGroup(new Group(id, name, audioDefaultBitrate, audioMaxBitrate, videoDefaultBitrate, videoMaxBitrate));
                securityService.updateGroup(new Group(id, name, 0, 0, videoDefaultBitrate, videoMaxBitrate));
            }             
        }
        
        String name = StringUtils.trimToNull(request.getParameter("name"));

        if (name != null) {
        	securityService.createGroup(new Group(name));
        }
        return null;
    }

    private String getParameter(HttpServletRequest request, String name, Integer id) {
        return StringUtils.trimToNull(request.getParameter(name + "[" + id + "]"));
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }    
 
}
