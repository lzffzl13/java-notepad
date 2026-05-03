package com.cloudnote.service.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class AppConfig {
    private static final Properties props = new Properties();

    static {
        Path envPath = Path.of(".env");
        if (Files.exists(envPath)) {
            try (FileInputStream fis = new FileInputStream(envPath.toFile())) {
                props.load(fis);
            } catch (IOException e) {
                // ignore, use defaults
            }
        }
    }

    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
