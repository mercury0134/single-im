package org.mercury.im.room.core.util;

public class SingleErrorEnums {


    public static final ErrorCodeEnum CONVERSE_NOT_EXIST = new ErrorCodeEnum(1001, "会话不存在");

    public static final ErrorCodeEnum CONVERSE_USER_ERROR = new ErrorCodeEnum(1021, "会话用户错误");

    public record ErrorCodeEnum(int code, String message) {
    }
}
