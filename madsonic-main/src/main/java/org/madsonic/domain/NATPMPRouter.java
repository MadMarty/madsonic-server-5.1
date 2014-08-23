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

import com.hoodcomputing.natpmp.MapRequestMessage;
import com.hoodcomputing.natpmp.NatPmpDevice;

/**
 * @author Sindre Mehus
 * @version $Id: NATPMPRouter.java 1712 2010-08-09 13:53:38Z sindre_mehus $
 */
public class NATPMPRouter implements Router {

    private final NatPmpDevice device;

    private NATPMPRouter(NatPmpDevice device) {
        this.device = device;
    }

    public static NATPMPRouter findRouter() {
        try {
            return new NATPMPRouter(new NatPmpDevice(false));
        } catch (Exception x) {
            return null;
        }
    }

    public void addPortMapping(int externalPort, int internalPort, int leaseDuration) throws Exception {

        // Use one week if lease duration is "forever".
        if (leaseDuration == 0) {
            leaseDuration = 7 * 24 * 3600;
        }

        MapRequestMessage map = new MapRequestMessage(true, internalPort, externalPort, leaseDuration, null);
        device.enqueueMessage(map);
        device.waitUntilQueueEmpty();
    }

    public void deletePortMapping(int externalPort, int internalPort) throws Exception {
        MapRequestMessage map = new MapRequestMessage(true, internalPort, externalPort, 0, null);
        device.enqueueMessage(map);
        device.waitUntilQueueEmpty();
    }
}
