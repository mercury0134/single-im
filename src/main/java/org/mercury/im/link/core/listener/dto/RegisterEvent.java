package org.mercury.im.link.core.listener.dto;

import org.mercury.im.link.core.infra.ws.pojo.Session;
import org.springframework.context.ApplicationEvent;

public class RegisterEvent extends ApplicationEvent {

    private final Session session;

    public RegisterEvent(Object source,Session session) {
        super(source);
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}

