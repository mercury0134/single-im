package org.mercury.im.link.core.infra.ws.util;

import org.mercury.im.common.core.objects.Tuple2;
import org.mercury.im.link.core.infra.ws.pojo.Session;

import java.net.InetSocketAddress;

public class SessionUtil {


    public static String getKey(Session session) {
        Tuple2<String, Integer> info = getInfo(session);
        return info.getFirst() + ":" + info.getSecond();
    }

    public static Tuple2<String, Integer> getInfo(Session session) {
        InetSocketAddress address = (InetSocketAddress) session.channel().remoteAddress();
        String ipAddress = address.getAddress().getHostAddress();
        int port = address.getPort();
        return new Tuple2<>(ipAddress, port);
    }
}
