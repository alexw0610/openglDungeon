package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class ApplicationProperties extends Properties {

    private static final String APPLICATION_PROPERTIES_FILE = "application.properties";


    public ApplicationProperties() {
        super();
        try {
            this.load(new FileInputStream(getFileFromResourcePath(APPLICATION_PROPERTIES_FILE)));
        } catch (IOException e) {
            System.err.println(String.format("Error loading properties file: %s", APPLICATION_PROPERTIES_FILE));
            System.exit(1);
        }
    }

    public File getFileFromResourcePath(String filename) {
        URL requestedURL = Thread.currentThread().getContextClassLoader().getResource(filename);
        if (requestedURL == null) {
            System.err.println(String.format("Error file not found: %s", filename));
            System.exit(1);
        }
        return new File(Thread.currentThread().getContextClassLoader().getResource(filename).getFile());
    }
}
