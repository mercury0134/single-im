package org.mercury.im.room.service;

import org.mercury.im.domain.room.model.ConversationBo;
import org.mercury.im.room.service.impl.MessageTokenServiceImpl;

public interface MessageTokenService {


    String token(String firstMessageId, ConversationBo conversationBo);


    MessageTokenServiceImpl.TokenBo parseToken(String token);
}
