/*
 * This file is part of Subsonic.
 *
 *  Subsonic is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Subsonic is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2014 (C) Sindre Mehus
 */

package org.madsonic.domain;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.TestCase;

public class SortableArtistTestCase extends TestCase {

    public void testGetName() throws Exception {

        TestSortableArtist a = new TestSortableArtist("ABBA");
        TestSortableArtist b = new TestSortableArtist("Abba");
        TestSortableArtist c = new TestSortableArtist("abba");
        TestSortableArtist d = new TestSortableArtist("ACDC");
        TestSortableArtist e = new TestSortableArtist("acdc");
        TestSortableArtist f = new TestSortableArtist("ACDC");
        TestSortableArtist g = new TestSortableArtist("abc");
        TestSortableArtist h = new TestSortableArtist("ABC");

        assertFalse(a.equals(b));
        assertFalse(b.equals(c));
        assertTrue(d.equals(f));

        assertFalse(a.hashCode() == b.hashCode());
        assertFalse(b.hashCode() == c.hashCode());
        assertTrue(d.hashCode() == f.hashCode());

        SortedSet<TestSortableArtist> artists = new TreeSet<TestSortableArtist>();
        artists.addAll(Arrays.asList(a, b, c, d, e, f, g, h));

        assertEquals("[ABBA, Abba, abba, ABC, abc, ACDC, acdc]", artists.toString());
    }

    public static class TestSortableArtist extends MusicIndex.SortableArtist {

        public TestSortableArtist(String sortableName) {
            super(sortableName, sortableName);
        }

        @Override
        public String toString() {
            return getSortableName();
        }
    }
}