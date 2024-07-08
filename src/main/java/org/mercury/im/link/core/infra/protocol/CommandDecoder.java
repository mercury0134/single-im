package org.mercury.im.link.core.infra.protocol;

import org.mercury.im.link.core.infra.ws.pojo.Session;

import javax.annotation.Nonnull;

/**
 * command decoder
 */
public interface CommandDecoder {

    SocketCommand decode(@Nonnull Session session, String message) throws Exception;
}
