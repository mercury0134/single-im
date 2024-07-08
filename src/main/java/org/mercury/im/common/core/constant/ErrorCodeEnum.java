package org.mercury.im.common.core.constant;

/**
 * 返回应答中的错误代码和错误信息。
 */
public enum ErrorCodeEnum {

    /**
     * 没有错误
     */
    NO_ERROR(200, "没有错误"),
    /**
     * 未处理的异常！
     */
    UNHANDLED_EXCEPTION(500, "未处理的异常！"),


    /**
     * 鉴权、权限
     */
    UNAUTHORIZED_LOGIN(501, "当前用户尚未登录或登录已超时，请重新登录！"),
    UNAUTHORIZED_USER_PERMISSION(511, "权限验证失败，当前用户不能访问该接口，请核对！"),
    NO_ACCESS_PERMISSION(511, "当前用户没有访问权限，请核对！"),
    NO_OPERATION_PERMISSION(511, "当前用户没有操作权限，请核对！"),
    INVALID_USERNAME_PASSWORD(517, "用户名或密码错误，请重试！"),
    INVALID_ACCESS_TOKEN(517, "无效的用户访问令牌！"),


    // 下面的枚举值为特定枚举值，即开发者可以根据自己的项目需求定义更多的非通用枚举值
    ;

    /**
     * 构造函数。
     *
     * @param errorMessage 错误消息。
     */
    ErrorCodeEnum(int code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }

    /**
     * 错误信息。
     */
    private final int code;

    /**
     * 错误信息。
     */
    private final String errorMessage;

    /**
     * 获取错误信息。
     *
     * @return 错误信息。
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * 获取错误码
     * @return
     */
    public int getCode() {
        return code;
    }
}
