package org.mercury.im.link.core.infra.ws.support;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.mercury.im.link.core.infra.ws.annotation.OnMessage;
import org.springframework.core.MethodParameter;

public class TextMethodArgumentResolver implements MethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getMethod().isAnnotationPresent(OnMessage.class) && String.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {
        TextWebSocketFrame textFrame = (TextWebSocketFrame) object;
        return textFrame.text();
    }
}
