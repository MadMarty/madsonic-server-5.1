package org.madsonic.booter.deployer;

/**
 * RMI interface implemented by the Madsonic deployer and used by the agent.
 *
 * @author Sindre Mehus
 */
public interface MadsonicDeployerService {

    /**
     * Returns information about the Madsonic deployment, such
     * as URL, memory consumption, start time etc.
     *
     * @return Deployment information.
     */
    DeploymentStatus getDeploymentInfo();
}
