package org.madsonic.booter.deployer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.BindException;
import java.util.Date;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Responsible for deploying the Madsonic web app in
 * the embedded Jetty container.
 * <p/>
 * The following system properties may be used to customize the behaviour:
 * <ul>
 * <li><code>madsonic.contextPath</code> - The context path at which Madsonic is deployed.  Default "/".</li>
 * <li><code>madsonic.port</code> - The port Madsonic will listen to.  Default 4040.</li>
 * <li><code>madsonic.httpsPort</code> - The port Madsonic will listen to for HTTPS.  Default 0, which disables HTTPS.</li>
 * <li><code>madsonic.war</code> - Madsonic WAR file, or exploded directory.  Default "madsonic.war".</li>
 * <li><code>madsonic.createLinkFile</code> - If set to "true", a Madsonic.url file is created in the working directory.</li>
 * <li><code>madsonic.ssl.keystore</code> - Path to an alternate SSL keystore.</li>
 * <li><code>madsonic.ssl.password</code> - Password of the alternate SSL keystore.</li>
 * </ul>
 *
 * @author Sindre Mehus
 */
public class MadsonicDeployer implements MadsonicDeployerService {

    public static final String DEFAULT_HOST = "0.0.0.0";
    public static final int DEFAULT_PORT = 4040;
    public static final int DEFAULT_HTTPS_PORT = 0;
    public static final int DEFAULT_MEMORY_INIT = 192;
    public static final int DEFAULT_MEMORY_LIMIT = 384;
    public static final String DEFAULT_CONTEXT_PATH = "/";
    public static final String DEFAULT_WAR = "madsonic.war";
    private static final int MAX_IDLE_TIME_MILLIS = 7 * 24 * 60 * 60 * 1000; // One week.
    private static final int HEADER_BUFFER_SIZE = 64 * 1024;

    // Madsonic home directory.
    private static final File MADSONIC_HOME_WINDOWS = new File("c:/madsonic");
    private static final File MADSONIC_HOME_OTHER = new File("/var/madsonic");

    private Throwable exception;
    private File madsonicHome;
    private final Date startTime;

    public MadsonicDeployer() {

        // Enable shutdown hook for Ehcache.
        System.setProperty("net.sf.ehcache.enableShutdownHook", "true");

        startTime = new Date();
        createLinkFile();
        deployWebApp();
    }

    private void createLinkFile() {
        if ("true".equals(System.getProperty("madsonic.createLinkFile"))) {
            Writer writer = null;
            try {
                writer = new FileWriter("madsonic.url");
                writer.append("[InternetShortcut]");
                writer.append(System.getProperty("line.separator"));
                writer.append("URL=").append(getUrl());
                writer.flush();
            } catch (Throwable x) {
                System.err.println("Failed to create madsonic.url.");
                x.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException x) {
                        // Ignored
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
	private void deployWebApp() {
        try {
            Server server = new Server();
            SelectChannelConnector connector = new SelectChannelConnector();
            connector.setMaxIdleTime(MAX_IDLE_TIME_MILLIS);
            connector.setRequestHeaderSize(HEADER_BUFFER_SIZE);
            connector.setResponseHeaderSize(HEADER_BUFFER_SIZE);
            connector.setHost(getHost());
            connector.setPort(getPort());
            if (isHttpsEnabled()) {
                connector.setConfidentialPort(getHttpsPort());
            }
            server.addConnector(connector);

            if (isHttpsEnabled()) {
                SslSocketConnector sslConnector = new SslSocketConnector();
                sslConnector.setMaxIdleTime(MAX_IDLE_TIME_MILLIS);
                sslConnector.setRequestHeaderSize(HEADER_BUFFER_SIZE);
                sslConnector.setResponseHeaderSize(HEADER_BUFFER_SIZE);
                sslConnector.setHost(getHost());
                sslConnector.setPort(getHttpsPort());
                sslConnector.setKeystore(System.getProperty("subsonic.ssl.keystore", getClass().getResource("/subsonic.keystore").toExternalForm()));
                sslConnector.setPassword(System.getProperty("subsonic.ssl.password", "subsonic"));
                server.addConnector(sslConnector);
            }

            WebAppContext context = new WebAppContext();
            context.setTempDirectory(getJettyDirectory());
            context.setContextPath(getContextPath());
            context.setWar(getWar());
            context.setOverrideDescriptor("/web-jetty.xml");

            if (isHttpsEnabled()) {

                // Allow non-https for streaming and cover art (for Chromecast, UPnP etc)
                ((ConstraintSecurityHandler) context.getSecurityHandler()).setConstraintMappings(new ConstraintMapping[]{
                        createConstraintMapping("/stream", Constraint.DC_NONE),
                        createConstraintMapping("/coverArt.view", Constraint.DC_NONE),
                        createConstraintMapping("/", Constraint.DC_CONFIDENTIAL)
                });
            } 

            server.setHandler(context);
            server.start();

            System.err.println("Madsonic running on: " + getUrl());
            if (isHttpsEnabled()) {
            System.err.println("                and: " + getHttpsUrl());
            }

        } catch (Throwable x) {
            x.printStackTrace();
            exception = x;
        }
    }

    private ConstraintMapping createConstraintMapping(String pathSpec, int dataConstraint) {
        ConstraintMapping constraintMapping = new ConstraintMapping();
        Constraint constraint = new Constraint();
        constraint.setDataConstraint(dataConstraint);
        constraintMapping.setPathSpec(pathSpec);
        constraintMapping.setConstraint(constraint);
        return constraintMapping;
    }

    private File getJettyDirectory() {
        File dir = new File(getMadsonicHome(), "jetty");
        String buildNumber = getMadsonicBuildNumber();
        if (buildNumber != null) {
            dir = new File(dir, buildNumber);
        }
        System.err.println("Extracting webapp to " + dir);

        if (!dir.exists() && !dir.mkdirs()) {
            System.err.println("Failed to create directory " + dir);
        }

        return dir;
    }

    private String getMadsonicBuildNumber() {
        File war = new File(getWar());
        InputStream in = null;
        try {
            if (war.isFile()) {
                @SuppressWarnings("resource")
				JarFile jar = new JarFile(war);
                ZipEntry entry = jar.getEntry("WEB-INF\\classes\\build_number.txt");
                if (entry == null) {
                    entry = jar.getEntry("WEB-INF/classes/build_number.txt");
                }
                in = jar.getInputStream(entry);
            } else {
                in = new FileInputStream(war.getPath() + "/WEB-INF/classes/build_number.txt");
            }
            return IOUtils.toString(in);

        } catch (Exception x) {
            System.err.println("Failed to resolve build number from WAR: " + war);
            x.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private String getContextPath() {
        return System.getProperty("madsonic.contextPath", DEFAULT_CONTEXT_PATH);
    }


    private String getWar() {
        String war = System.getProperty("madsonic.war");
        if (war == null) {
            war = DEFAULT_WAR;
        }

        File file = new File(war);
        if (file.exists()) {
            System.err.println("Using WAR file: " + file.getAbsolutePath());
        } else {
            System.err.println("Error: WAR file not found: " + file.getAbsolutePath());
        }

        return war;
    }

    private String getHost() {
        return System.getProperty("madsonic.host", DEFAULT_HOST);
    }

    private int getPort() {
        int port = DEFAULT_PORT;

        String portString = System.getProperty("madsonic.port");
        if (portString != null) {
            port = Integer.parseInt(portString);
        }

        // Also set it so that the webapp can read it.
        System.setProperty("madsonic.port", String.valueOf(port));

        return port;
    }

    private int getHttpsPort() {
        int port = DEFAULT_HTTPS_PORT;

        String portString = System.getProperty("madsonic.httpsPort");
        if (portString != null) {
            port = Integer.parseInt(portString);
        }

        // Also set it so that the webapp can read it.
        System.setProperty("madsonic.httpsPort", String.valueOf(port));

        return port;
    }

    private boolean isHttpsEnabled() {
        return getHttpsPort() > 0;
    }

    public String getErrorMessage() {
        if (exception == null) {
            return null;
        }
        if (exception instanceof BindException) {
            return "Address already in use. Please change port number.";
        }

        return exception.toString();
    }

    public int getMemoryUsed() {
        long freeBytes = Runtime.getRuntime().freeMemory();
        long totalBytes = Runtime.getRuntime().totalMemory();
        long usedBytes = totalBytes - freeBytes;
        return (int) Math.round(usedBytes / 1024.0 / 1024.0);
    }

    private String getUrl() {
        String host = DEFAULT_HOST.equals(getHost()) ? "localhost" : getHost();
        StringBuilder url = new StringBuilder("http://").append(host);
        if (getPort() != 80) {
            url.append(":").append(getPort());
        }
        url.append(getContextPath());
        return url.toString();
    }

    private String getHttpsUrl() {
        if (!isHttpsEnabled()) {
            return null;
        }

        String host = DEFAULT_HOST.equals(getHost()) ? "localhost" : getHost();
        StringBuilder url = new StringBuilder("https://").append(host);
        if (getHttpsPort() != 443) {
            url.append(":").append(getHttpsPort());
        }
        url.append(getContextPath());
        return url.toString();
    }

    /**
     * Returns the Madsonic home directory.
     *
     * @return The Madsonic home directory, if it exists.
     * @throws RuntimeException If directory doesn't exist.
     */
    private File getMadsonicHome() {

        if (madsonicHome != null) {
            return madsonicHome;
        }

        File home;

        String overrideHome = System.getProperty("madsonic.home");
        if (overrideHome != null) {
            home = new File(overrideHome);
        } else {
            boolean isWindows = System.getProperty("os.name", "Windows").toLowerCase().startsWith("windows");
            home = isWindows ? MADSONIC_HOME_WINDOWS : MADSONIC_HOME_OTHER;
        }

        // Attempt to create home directory if it doesn't exist.
        if (!home.exists() || !home.isDirectory()) {
            boolean success = home.mkdirs();
            if (success) {
                madsonicHome = home;
            } else {
                String message = "The directory " + home + " does not exist. Please create it and make it writable. " +
                        "(You can override the directory location by specifying -Dmadsonic.home=... when " +
                        "starting the servlet container.)";
                System.err.println("ERROR: " + message);
            }
        } else {
            madsonicHome = home;
        }

        return home;
    }

    public DeploymentStatus getDeploymentInfo() {
        return new DeploymentStatus(startTime, getUrl(), getHttpsUrl(), getMemoryUsed(), getErrorMessage());
    }
}
