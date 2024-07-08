package org.mercury.im.link.ws;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.timeout.IdleStateEvent;
import jakarta.annotation.Resource;
import org.mercury.im.link.core.infra.ws.annotation.*;
import org.mercury.im.link.core.infra.ws.pojo.Session;
import org.mercury.im.link.core.listener.dto.MessageEvent;
import org.mercury.im.link.core.listener.dto.RegisterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

@ServerEndpoint(host = "0.0.0.0", port = "1980")
public class ImSocketController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ApplicationEventPublisher publisher;

    /**
     * 握手
     */
    @BeforeHandshake
    public void handshake(Session session, HttpHeaders headers,
                          @RequestParam(name = "req") String req,
                          @RequestParam(name = "reqMap") MultiValueMap reqMap,
                          @PathVariable(name = "arg") String arg,
                          @PathVariable(name = "pathMap") Map pathMap) {
        session.setSubprotocols("json");

        InetSocketAddress address = (InetSocketAddress) session.channel().remoteAddress();
        String ipAddress = address.getAddress().getHostAddress();
        int port = address.getPort();
        logger.info("handshake new socket: {}:{}", ipAddress, port);

        RegisterEvent event = new RegisterEvent(new Object(), session);
        publisher.publishEvent(event);
    }

    @OnOpen
    public void onOpen(Session session, HttpHeaders headers,
                       @RequestParam(name = "req") String req,
                       @RequestParam(name = "reqMap") MultiValueMap reqMap,
                       @PathVariable(name = "arg") String arg,
                       @PathVariable(name = "pathMap") Map pathMap) {
        InetSocketAddress address = (InetSocketAddress) session.channel().remoteAddress();
        String ipAddress = address.getAddress().getHostAddress();
        int port = address.getPort();
        logger.info("new socket opened: {}:{}", ipAddress, port);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        logger.info("one connection closed");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        MessageEvent event = new MessageEvent(new Object(), message, session);
        publisher.publishEvent(event);

        session.sendText(message);
    }

    @OnBinary
    public void onBinary(Session session, byte[] bytes) {
    }

    @OnEvent
    public void onEvent(Session session, Object evt) {
        if (evt instanceof IdleStateEvent idleStateEvent) {
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    System.out.println("read idle");
                    break;
                case WRITER_IDLE:
                    System.out.println("write idle");
                    break;
                case ALL_IDLE:
                    System.out.println("all idle");
                    break;
                default:
                    break;
            }
        }
    }

}
