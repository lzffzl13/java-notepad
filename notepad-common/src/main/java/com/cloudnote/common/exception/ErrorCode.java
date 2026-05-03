package com.cloudnote.common.exception;

public enum ErrorCode {
    NOTE_NOT_FOUND(1001, "笔记不存在"),
    TITLE_EMPTY(1002, "标题不能为空"),
    TAG_NOT_FOUND(1003, "标签不存在"),
    TAG_DUPLICATE(1004, "标签已存在"),
    IMAGE_NOT_FOUND(1005, "图片不存在"),
    IMAGE_SAVE_FAILED(1006, "图片保存失败"),
    DB_ERROR(2001, "数据库异常"),
    DB_CONNECTION_FAILED(2002, "数据库连接失败"),
    CONFIG_ERROR(2003, "配置加载失败"),
    EXPORT_FAILED(3001, "导出失败"),
    IO_ERROR(4001, "文件IO异常");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
}
