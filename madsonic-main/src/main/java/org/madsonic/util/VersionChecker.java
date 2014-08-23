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
package org.madsonic.util;

import org.springframework.core.SpringVersion;
import org.springframework.security.core.SpringSecurityCoreVersion;

public class VersionChecker
{
    @SuppressWarnings("static-access")
	public static String getSpringFrameworkVersion()
    {	
    	String version = null;
    	try { 
    	 SpringVersion springVersion = new SpringVersion();
    	 version = springVersion.getVersion();
    	} 
    	catch (Exception x) {}
       	return version == null ? "3.1.4" : version.replace(".RELEASE", "");
    }
    
    public static String getSpringSecurityVersion()
    {
    	String version = null;
    	try { 
    		version = SpringSecurityCoreVersion.getVersion(); 
    		}
    		catch (Exception x) {}
       	return version == null ? "3.1.4" : version.replace(".RELEASE", "");
    }
}