package org.mercury.im.room.service.impl;

import lombok.Data;
import org.mercury.im.common.json.JsonUtils;
import org.mercury.im.domain.room.model.ConversationBo;
import org.mercury.im.domain.room.model.param.SingleConversationExtra;
import org.mercury.im.room.core.util.AESUtil;
import org.mercury.im.room.service.MessageTokenService;
import org.springframework.stereotype.Service;

@Service
public class MessageTokenServiceImpl implements MessageTokenService {

    /**
     * 必须是 16 字节 24 字节 32 字节等
     */
    public final String AES_KEY = "b20oAitX3EQmgxAo";

    /**
     * token的作用
     * 证明converse是否存在、
     * 是否可以直接发消息 拉黑
     * firstMessageId 用于消息的分页
     */

    @Override
    public String token(String firstMessageId, ConversationBo conversationBo) {
        SingleConversationExtra extra = (SingleConversationExtra) conversationBo.getExtra();
        TokenBo tokenBo = new TokenBo();
        tokenBo.setFirstMessageId(firstMessageId);
        tokenBo.setConverseId(conversationBo.getConverseId());
        tokenBo.setBlock(extra.getBlock());
        String s = JsonUtils.toJsonString(tokenBo);
        try {
            return AESUtil.encrypt(s, AES_KEY);
        } catch (Exception ignore) {
        }
        return null;
    }

    @Override
    public TokenBo parseToken(String token) {
        try {
            String s = AESUtil.decrypt(token, AES_KEY);
            return JsonUtils.parse(s, TokenBo.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Data
    public static class TokenBo {
        private String firstMessageId;
        private String converseId;
        private Byte block;
    }
}
