package org.mercury.im.link.core.infra.ws.support;

import io.netty.channel.Channel;
import org.mercury.im.link.core.infra.ws.pojo.Session;
import org.springframework.core.MethodParameter;

import static org.mercury.im.link.core.infra.ws.pojo.PojoEndpointServer.SESSION_KEY;


public class SessionMethodArgumentResolver implements MethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Session.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {
        return channel.attr(SESSION_KEY).get();
    }
}
