package org.madsonic.booter.agent;

import org.madsonic.booter.deployer.DeploymentStatus;

/**
 * Callback interface implemented by GUI classes that wants to be notified when
 * the state of the Madsonic deployment changes.
 *
 * @author Sindre Mehus
 */
public interface MadsonicListener {

    /**
     * Invoked when new information about the Madsonic deployment is available.
     *
     * @param deploymentStatus The new deployment status, or <code>null</code> if an
     *                       error occurred while retrieving the status.
     */
    void notifyDeploymentStatus(DeploymentStatus deploymentStatus);

    /**
     * Invoked when new information about the Madsonic Windows service is available.
     *
     * @param serviceStatus The new service status, or <code>null</code> if an
     *                       error occurred while retrieving the status.
     */
    void notifyServiceStatus(String serviceStatus);
}
