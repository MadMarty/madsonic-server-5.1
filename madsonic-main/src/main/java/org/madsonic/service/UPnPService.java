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

import org.madsonic.Logger;
import org.madsonic.domain.Version;
import org.madsonic.service.upnp.ApacheUpnpServiceConfiguration;
import org.madsonic.service.upnp.FolderBasedContentDirectory;
import org.madsonic.service.upnp.MSMediaReceiverRegistrarService;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.types.DLNADoc;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.support.connectionmanager.ConnectionManagerService;
import org.fourthline.cling.support.model.ProtocolInfos;
import org.fourthline.cling.support.model.dlna.DLNAProfiles;
import org.fourthline.cling.support.model.dlna.DLNAProtocolInfo;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public class UPnPService {

    private static final Logger LOG = Logger.getLogger(UPnPService.class);

    private SettingsService settingsService;
    private VersionService versionService;
    private UpnpService upnpService;
    private FolderBasedContentDirectory folderBasedContentDirectory;

    public void init() {
        startService();
    }

    public void startService() {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    LOG.info("Checking UPnP service...");
                    createService();
                    LOG.info("Checking UPnP service - Done!");
                } catch (Throwable x) {
                    LOG.error("Failed to check UPnP service: " + x, x);
                }
            }
        };
        new Thread(runnable).start();
    }

    private synchronized void createService() throws Exception {
        upnpService = new UpnpServiceImpl(new ApacheUpnpServiceConfiguration());

        // Asynch search for other devices (most importantly UPnP-enabled routers for port-mapping)
        upnpService.getControlPoint().search();

        // Start DLNA media server?
        setMediaServerEnabled(settingsService.isDlnaEnabled());

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.err.println("Shutting down UPnP service...");
                upnpService.shutdown();
                System.err.println("Shutting down UPnP service - Done!");
            }
        });
    }

    public void setMediaServerEnabled(boolean enabled) {
        if (enabled) {
            try {
                upnpService.getRegistry().addDevice(createMediaServerDevice());
                LOG.info("Enabling UPnP/DLNA media server");
            } catch (Exception x) {
                LOG.error("Failed to start UPnP/DLNA media server: " + x, x);
            }
        } else {
            upnpService.getRegistry().removeAllLocalDevices();
            LOG.info("Disabling UPnP/DLNA media server");
        }
    }

    private LocalDevice createMediaServerDevice() throws Exception {

        String serverName = settingsService.getDlnaServerName();
        DeviceIdentity identity = new DeviceIdentity(UDN.uniqueSystemIdentifier(serverName));
        DeviceType type = new UDADeviceType("MediaServer", 1);

        // TODO: DLNACaps
        Version version = versionService.getLocalVersion();
        String versionString = version == null ? null : version.toString();
        String licenseEmail = "madsonic@localhost"; //settingsService.getLicenseEmail();
        String licenseString = licenseEmail == null ? "Unlicensed" : ("Licensed to " + licenseEmail);

        DeviceDetails details = new DeviceDetails("Madsonic Media Streamer", new ManufacturerDetails(serverName),
                new ModelDetails(serverName, licenseString, versionString),
                new DLNADoc[]{new DLNADoc("DMS", DLNADoc.Version.V1_5)}, null);

        Icon icon = new Icon("image/png", 512, 512, 32, getClass().getResource("madsonic-512.png"));

        @SuppressWarnings("unchecked")
		LocalService<FolderBasedContentDirectory> contentDirectoryservice = new AnnotationLocalServiceBinder().read(FolderBasedContentDirectory.class);
        contentDirectoryservice.setManager(new DefaultServiceManager<FolderBasedContentDirectory>(contentDirectoryservice) {

            @Override
            protected FolderBasedContentDirectory createServiceInstance() throws Exception {
                return folderBasedContentDirectory;
            }
        });

        final ProtocolInfos protocols = new ProtocolInfos();
        for (DLNAProfiles dlnaProfile : DLNAProfiles.values()) {
            if (dlnaProfile == DLNAProfiles.NONE) {
                continue;
            }
            try {
                protocols.add(new DLNAProtocolInfo(dlnaProfile));
            } catch (Exception e) {
                // Silently ignored.
            }
        }

        @SuppressWarnings("unchecked")
		LocalService<ConnectionManagerService> connetionManagerService = new AnnotationLocalServiceBinder().read(ConnectionManagerService.class);
        connetionManagerService.setManager(new DefaultServiceManager<ConnectionManagerService>(connetionManagerService) {
            @Override
            protected ConnectionManagerService createServiceInstance() throws Exception {
                return new ConnectionManagerService(protocols, null);
            }
        });

        // For compatibility with Microsoft
        @SuppressWarnings("unchecked")
		LocalService<MSMediaReceiverRegistrarService> receiverService = new AnnotationLocalServiceBinder().read(MSMediaReceiverRegistrarService.class);
        receiverService.setManager(new DefaultServiceManager<MSMediaReceiverRegistrarService>(receiverService, MSMediaReceiverRegistrarService.class));

        return new LocalDevice(identity, type, details, new Icon[]{icon}, new LocalService[]{contentDirectoryservice, connetionManagerService, receiverService});
    }


    public UpnpService getUpnpService() {
        return upnpService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setVersionService(VersionService versionService) {
        this.versionService = versionService;
    }

    public void setFolderBasedContentDirectory(FolderBasedContentDirectory folderBasedContentDirectory) {
        this.folderBasedContentDirectory = folderBasedContentDirectory;
    }
}
