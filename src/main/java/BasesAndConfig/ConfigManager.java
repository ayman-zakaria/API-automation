package BasesAndConfig;

// Small wrapper so the rest of the code doesn't deal with raw property strings/keys.
// Add a getter here whenever a new value gets added to config.properties.
public final class ConfigManager {

    private static final ConfigReader READER = new ConfigReader("config.properties");

    private ConfigManager() {
    }

    public static String baseUri() {
        return READER.get("api.base.uri");
    }

    public static String booksPath() {
        return READER.get("api.books.path");
    }

    public static int requestTimeoutSeconds() {
        return READER.getInt("request.timeout.seconds");
    }
}
