/*
 This file is part of Madsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Madsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Madsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2014 (C) Madevil
 */
package org.madsonic.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import org.madsonic.service.SettingsService;
import org.madsonic.service.UPnPService;

/**
 * Controller for the page used to administrate the UPnP/DLNA server settings.
 *
 * @author Sindre Mehus
 */
public class apiSettingsController extends ParameterizableViewController {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();

        if (isFormSubmission(request)) {
            handleParameters(request);
            map.put("toast", true);
        }

        ModelAndView result = super.handleRequestInternal(request, response);
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

    private void handleParameters(HttpServletRequest request) {
    }

}