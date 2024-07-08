package org.mercury.im.link.core.listener.dto;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.mercury.im.link.core.infra.ws.pojo.Session;
import org.springframework.context.ApplicationEvent;

import javax.annotation.Nonnull;

@Getter
public class MessageEvent extends ApplicationEvent {

    private final String message;

    private final Session session;

    public MessageEvent(Object source, String message, @Nonnull Session session) {
        super(source);
        this.message = message;
        this.session = session;
    }

    @NotNull
    public Session getSession() {
        return session;
    }
}

