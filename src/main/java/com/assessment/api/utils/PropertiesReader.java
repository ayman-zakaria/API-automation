package com.assessment.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Generic, reusable properties file reader shared by config and test data lookups,
 * so no configuration or test data values are hard-coded in the code.
 */
public class PropertiesReader {

    private final Properties properties;

    public PropertiesReader(String classpathResourceName) {
        this.properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(classpathResourceName)) {
            if (input == null) {
                throw new IllegalStateException("Unable to find resource: " + classpathResourceName);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load resource: " + classpathResourceName, e);
        }
    }

    public String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing property key: " + key);
        }
        return value.trim();
    }

    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }
}
