package org.mercury.im.common.core.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.mercury.im.common.core.constant.ErrorCodeEnum;
import org.mercury.im.common.core.objects.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice(value = {"org.mercury.im"})
public class MercuryExceptionHandler {
    private final static Logger logger = LoggerFactory.getLogger(MercuryExceptionHandler.class);

    /**
     * 通用异常处理方法。
     *
     * @param ex      异常对象。
     * @param request http请求。
     * @return 应答对象。
     */
    @ExceptionHandler(Exception.class)
    public ResponseResult<Void> handleException(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled exception from URL [" + request.getRequestURI() + "]", ex);
        return ResponseResult.create(ErrorCodeEnum.UNHANDLED_EXCEPTION);
    }


}
