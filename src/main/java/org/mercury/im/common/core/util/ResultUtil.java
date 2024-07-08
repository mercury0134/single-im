package org.mercury.im.common.core.util;

import jakarta.servlet.http.HttpServletResponse;
import org.mercury.im.common.core.objects.ResponseResult;
import org.mercury.im.common.json.JsonUtils;

import java.io.IOException;
import java.io.PrintWriter;

public class ResultUtil {

    public static <T> void responseJson(
            HttpServletResponse response, ResponseResult<T> voidResponseResult) {

        response.setContentType("application/json;charset=utf-8");
        String jsonData = JsonUtils.toJsonString(voidResponseResult);
        try {
            PrintWriter out = response.getWriter();
            out.print(jsonData);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
