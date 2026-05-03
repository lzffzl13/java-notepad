package com.cloudnote.common.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ImageUtils {
    private ImageUtils() {}

    public static Path getImageDir() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, "cloudnote", "images");
    }

    public static void ensureImageDir() {
        File dir = getImageDir().toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static String generateImageName(String originalName) {
        return System.currentTimeMillis() + "_" + originalName;
    }

    public static boolean isImageFile(String filename) {
        if (filename == null) return false;
        String lower = filename.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                || lower.endsWith(".png") || lower.endsWith(".gif");
    }
}
