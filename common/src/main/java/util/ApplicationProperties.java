package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ApplicationProperties extends Properties {

    private static final String APPLICATION_PROPERTIES_FILE = "application.properties";
    private final String RESOURCE_ROOT_PATH;

    public ApplicationProperties() {
        super();
        RESOURCE_ROOT_PATH = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = RESOURCE_ROOT_PATH + APPLICATION_PROPERTIES_FILE;
        try {
            this.load(new FileInputStream(appConfigPath));
        } catch (IOException e) {
            System.err.println(String.format("Error loading properties file from path", appConfigPath));
            System.exit(1);
        }
    }

    public String getResourceRootPath() {
        return this.RESOURCE_ROOT_PATH;
    }

    public File getFileFromResourcePath(String filename) {
        return new File(RESOURCE_ROOT_PATH + filename);
    }
}
