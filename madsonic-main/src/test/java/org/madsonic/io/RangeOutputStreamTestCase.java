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

 Copyright 2014 (C) Sindre Mehus
 */
package org.madsonic.io;

import junit.framework.TestCase;
import org.madsonic.util.HttpRange;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public class RangeOutputStreamTestCase extends TestCase {

    public void testWrap() throws Exception {
        doTestWrap(0, 99, 100, 1);
        doTestWrap(0, 99, 100, 10);
        doTestWrap(0, 99, 100, 13);
        doTestWrap(0, 99, 100, 70);
        doTestWrap(0, 99, 100, 100);

        doTestWrap(10, 99, 100, 1);
        doTestWrap(10, 99, 100, 10);
        doTestWrap(10, 99, 100, 13);
        doTestWrap(10, 99, 100, 70);
        doTestWrap(10, 99, 100, 100);

        doTestWrap(66, 66, 100, 1);
        doTestWrap(66, 66, 100, 2);

        doTestWrap(10, 20, 100, 1);
        doTestWrap(10, 20, 100, 10);
        doTestWrap(10, 20, 100, 13);
        doTestWrap(10, 20, 100, 70);
        doTestWrap(10, 20, 100, 100);

        for (int start = 0; start < 10; start++) {
            for (int end = start; end < 10; end++) {
                for (int bufferSize = 1; bufferSize < 10; bufferSize++) {
                    doTestWrap(start, end, 10, bufferSize);
                    doTestWrap(start, null, 10, bufferSize);
                }
            }
        }
    }

    private void doTestWrap(int first, Integer last, int sourceSize, int bufferSize) throws Exception {
        byte[] source = createSource(sourceSize);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStream rangeOut = RangeOutputStream.wrap(out, new HttpRange((long) first, last == null ? null : last.longValue()));
        copy(source, rangeOut, bufferSize);
        verify(out.toByteArray(), first, last, sourceSize);
    }

    private void verify(byte[] bytes, int first, Integer last, int sourceSize) {
        if (last == null) {
            assertEquals(sourceSize - first, bytes.length);
        } else {
            assertEquals(last - first + 1, bytes.length);
        }
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(first + i, bytes[i]);
        }
    }

    private void copy(byte[] source, OutputStream out, int bufsz) throws IOException {
        InputStream in = new ByteArrayInputStream(source);
        byte[] buffer = new byte[bufsz];
        int n;
        while (-1 != (n = in.read(buffer))) {
            int split = n / 2;
            out.write(buffer, 0, split);
            out.write(buffer, split, n - split);
        }
    }

    private byte[] createSource(int size) {
        byte[] result = new byte[size];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) i;
        }
        return result;
    }
}
