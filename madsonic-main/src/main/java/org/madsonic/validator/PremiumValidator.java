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
package org.madsonic.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import org.madsonic.command.PremiumCommand;
import org.madsonic.controller.PremiumController;
import org.madsonic.service.SettingsService;

/**
 * Validator for {@link PremiumController}.
 *
 * @author Sindre Mehus
 */
public class PremiumValidator implements Validator {
    private SettingsService settingsService;

    public boolean supports(Class clazz) {
        return clazz.equals(PremiumCommand.class);
    }

    public void validate(Object obj, Errors errors) {
        PremiumCommand command = (PremiumCommand) obj;

        if (!settingsService.isLicenseValid(command.getLicenseInfo().getLicenseEmail(), command.getLicenseCode())) {
            command.setSubmissionError(true);
            errors.rejectValue("licenseCode", "premium.invalidlicense");
        }
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
