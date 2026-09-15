package com.assessment.api.utils;

/**
 * Central access point for environment configuration (config.properties).
 */
public final class ConfigManager {

    private static final PropertiesReader READER = new PropertiesReader("config.properties");

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
