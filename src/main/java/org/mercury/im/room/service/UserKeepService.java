package org.mercury.im.room.service;

import org.mercury.im.link.core.infra.protocol.SocketCommand;
import org.mercury.im.link.core.infra.ws.pojo.Session;

public interface UserKeepService {

    /**
     * 注册session
     */
    boolean cache(Session session);

    /**
     * 鉴权
     */
    boolean authenticate(SocketCommand cmd);

    /**
     * 获取会话
     */
    Session get(Long key);
}
