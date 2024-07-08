package org.mercury.im.domain.room.core.rpc.conversation;

import lombok.Data;

@Data
public class BlockConverseVo {

    /**
     * 当前用户是否拉黑对方 0 未拉黑 1 拉黑
     */
    private Byte block;
}
