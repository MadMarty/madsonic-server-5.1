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
package org.madsonic.service.upnp;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public interface Router {

    /**
     * Adds a NAT entry on the UPNP device.
     *
     * @param externalPort  The external port to open on the UPNP device an map on the internal client.
     * @param internalPort  The internal client port where data should be redirected.
     * @param leaseDuration Seconds the lease duration in seconds, or 0 for an infinite time.
     */
    void addPortMapping(int externalPort, int internalPort, int leaseDuration) throws Exception;

    /**
     * Deletes a NAT entry on the UPNP device.
     *
     * @param externalPort The external port of the NAT entry to delete.
     * @param internalPort The internal port of the NAT entry to delete.
     */
    void deletePortMapping(int externalPort, int internalPort) throws Exception;
}
