package org.netomi.tracker.orekit;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.orekit.data.DataProvidersManager;

/**
 * Utility class for configuring the orekit library in a web application context
 */
public class OrekitConfiguration {

    /**
     * This is a utility class so its constructor is private.
     */
    private OrekitConfiguration() {}

    /**
     * Configure the library.
     * <p>
     * Several configuration components are used here. They have been chosen in order to simplify running the tutorials
     * in either a user home or local environment or in the development environment.
     * <ul>
     * <li>get the "orekit-data.zip" resource from the classloader</li>
     * </ul>
     * </p>
     */
    public static void configureOrekit()
        throws URISyntaxException {
        // check if the path has been set from external
        String path = System.getProperty(DataProvidersManager.OREKIT_DATA_PATH);

        if (path == null || path.length() == 0) {
            StringBuffer pathBuffer = new StringBuffer();

            File orekitDir = new File("orekit");
            appendIfExists(pathBuffer, orekitDir);

            if (pathBuffer.length() == 0) {
                URL classpathUrl = OrekitConfiguration.class.getClassLoader().getResource("orekit");
                if (classpathUrl != null) {
                    appendIfExists(pathBuffer, new File(classpathUrl.toURI()));
                }
            }

            path = pathBuffer.toString();
            System.setProperty(DataProvidersManager.OREKIT_DATA_PATH, path);
        }
    }

    /**
     * Append a directory/zip archive to the path if it exists.
     * 
     * @param path placeholder where to put the directory/zip archive
     * @param file file to try
     */
    private static void appendIfExists(final StringBuffer path, final File file) {
        if (file.exists() && (file.isDirectory() || file.getName().endsWith(".zip"))) {
            if (path.length() > 0) {
                path.append(System.getProperty("path.separator"));
            }
            path.append(file.getAbsolutePath());
        }
    }
}
