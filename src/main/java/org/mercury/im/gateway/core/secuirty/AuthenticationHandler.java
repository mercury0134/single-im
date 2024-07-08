package org.mercury.im.gateway.core.secuirty;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mercury.im.common.core.constant.ErrorCodeEnum;
import org.mercury.im.common.core.objects.ResponseResult;
import org.mercury.im.common.core.util.ResultUtil;

import java.io.IOException;

public class AuthenticationHandler {

    public static void loginAuthenticationFailureHandler(HttpServletRequest request,
                                                         HttpServletResponse response,
                                                         Exception exception) throws IOException, ServletException {
        ResultUtil.responseJson(response, ResponseResult.create(ErrorCodeEnum.INVALID_USERNAME_PASSWORD, exception.getMessage()));
    }
}
