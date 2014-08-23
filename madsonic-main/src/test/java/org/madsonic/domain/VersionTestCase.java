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
package org.madsonic.domain;

/**
 * Unit test of {@link Version}.
 * @author Sindre Mehus
 */

import junit.framework.*;

public class VersionTestCase extends TestCase {

    /**
     * Tests that equals(), hashCode(), toString() and compareTo() works.
     */
    public void testVersion()  {
        doTestVersion("1.5.1", "2.3.0");
        doTestVersion("2.3.0", "2.34.0");

        doTestVersion("1.5.0", "1.5.1");
        doTestVersion("1.5.1", "1.5.2");
        doTestVersion("1.5.2", "1.5.11");

        doTestVersion("1.4.0", "1.5.1340.beta1");
        doTestVersion("1.4.1", "1.5.0.beta1");
        doTestVersion("1.5.0.beta1", "1.5.0");
        doTestVersion("1.5.0.beta1", "1.5.1");
        doTestVersion("1.5.0.beta1", "1.6.0");
        doTestVersion("4.7.0.beta1", "4.7.3180");
        doTestVersion("4.7.0.beta2", "4.8.3300.beta1");
    }

    /**
     * Tests that equals(), hashCode(), toString() and compareTo() works.
     * @param v1 A lower version.
     * @param v2 A higher version.
     */
    private void doTestVersion(String v1, String v2) {
        Version ver1 = new Version(v1);
        Version ver2 = new Version(v2);

        assertEquals("Error in toString().", v1, ver1.toString());
        assertEquals("Error in toString().", v2, ver2.toString());

        assertEquals("Error in equals().", ver1, ver1);

        assertEquals("Error in compareTo().", 0, ver1.compareTo(ver1));
        assertEquals("Error in compareTo().", 0, ver2.compareTo(ver2));
        assertTrue("Error in compareTo().", ver1.compareTo(ver2) < 0);
        assertTrue("Error in compareTo().", ver2.compareTo(ver1) > 0);
    }
}