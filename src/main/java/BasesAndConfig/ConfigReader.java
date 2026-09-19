package BasesAndConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// Reads any .properties file off the classpath. Used for both the app config
// and the test data file - didn't see the point in writing two loaders.
public class ConfigReader {

    private final Properties properties;

    public ConfigReader(String classpathResourceName) {
        this.properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(classpathResourceName)) {
            if (input == null) {
                throw new IllegalStateException("Unable to find resource on classpath: " + classpathResourceName);
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
