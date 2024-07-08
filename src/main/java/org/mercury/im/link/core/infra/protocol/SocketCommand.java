package org.mercury.im.link.core.infra.protocol;

import lombok.Data;
import org.mercury.im.link.core.infra.ws.pojo.Session;

@Data
public class SocketCommand {

    private Byte protocol;

    private String id;

    private String event;

    private Session session;

    private SocketRequestHeader header;

    private Object content;

    public String getSignData() {
        return protocol + "_"
                + id + "_"
                + event + "_"
                + header.getKey() + "_"
                + header.getTimeout();
    }
}
