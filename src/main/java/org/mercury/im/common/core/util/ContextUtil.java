package org.mercury.im.common.core.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * 获取Servlet HttpRequest和HttpResponse的工具类
 */
public class ContextUtil {

    /**
     * 判断当前是否处于HttpServletRequest上下文环境。
     *
     * @return 是返回true，否则false。
     */
    public static boolean hasRequestContext() {
        return RequestContextHolder.getRequestAttributes() != null;
    }


    /**
     * 获取Servlet请求上下文的HttpRequest对象。
     *
     * @return 请求上下文中的HttpRequest对象。
     */
    public static HttpServletRequest getHttpRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取Servlet请求上下文的HttpResponse对象。
     *
     * @return 请求上下文中的HttpResponse对象。
     */
    public static HttpServletResponse getHttpResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private ContextUtil() {
    }

}
