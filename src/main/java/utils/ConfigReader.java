package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    public static void loadProperties() {
        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) {
                logger.error("config.properties not found in classpath");
                throw new RuntimeException("config.properties not found in classpath");
            }
            properties.load(is);
            logger.info("Config properties loaded successfully.");
        } catch (IOException e) {
            logger.error("Failed to load config.properties", e);
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String get(String key) {
        String val = properties.getProperty(key);
        if (val == null) {
            logger.warn("Missing config key: {}", key);
        }
        return val;
    }
}