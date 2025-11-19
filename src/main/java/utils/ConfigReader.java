package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);

    public static Properties properties;

    public static void loadProperties() {
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
            properties = new Properties();
            properties.load(fis);
            logger.info("Config properties loaded successfully.");
        } catch (Exception e) {
            logger.error("Failed to load config.properties file", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}