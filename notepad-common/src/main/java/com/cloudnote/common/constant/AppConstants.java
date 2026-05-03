package com.cloudnote.common.constant;

public final class AppConstants {
    private AppConstants() {}

    public static final String APP_NAME = "云记事本";
    public static final String IMAGE_DIR = System.getProperty("user.home") + "/cloudnote/images";
    public static final int IMAGE_THUMB_WIDTH = 100;
    public static final int IMAGE_THUMB_HEIGHT = 100;
    public static final int NOTE_CONTENT_PREVIEW_LEN = 50;
    public static final int DEFAULT_PAGE_SIZE = 50;
}
