package util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Properties;

public class ApplicationProperties extends Properties {

    private static final String APPLICATION_PROPERTIES_FILE = "application.properties";
    private static final long serialVersionUID = 2994373857836726368L;
    private static String basePath;
    public static final String CONFIG_PATH = "/config/";


    public ApplicationProperties() {
        super();
        try {
            this.load(getResourceAsStream(APPLICATION_PROPERTIES_FILE));
        } catch (IOException e) {
            System.err.println(String.format("Error loading properties file: %s", APPLICATION_PROPERTIES_FILE));
            System.exit(1);
        }
        CodeSource src = ApplicationProperties.class.getProtectionDomain().getCodeSource();
        URL jar = src.getLocation();
        File parent = null;
        try {
            parent = new File(jar.toURI());
            basePath = parent.getParent();
        } catch (URISyntaxException e) {
            System.err.println(String.format("Error while setting base path: %s", jar.getPath()));
            System.exit(1);
        }
    }

    public File getFileFromConfig(String filename) {
        return new File(basePath + CONFIG_PATH + filename);
    }

    public InputStream getResourceAsStream(String filename) {
        return this.getClass().getResourceAsStream("/" + filename);
    }
}
