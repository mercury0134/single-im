package org.mercury.im.domain.room.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.mercury.im.common.core.util.HMac;

@Data
public class MessageItem {

    /**
     * ms time + userId mod x
     */
    private String id;

    /**
     * 设备生成的id 用来处理消息重复发送 设备自己解决重复性问题
     */
    private String deviceId;

    /**
     * 会话id
     */
    private String converseId;

    /**
     * 发送者
     */
    private Long userId;

    /**
     * 发送时间戳 ms 应该为服务器当前时间戳
     */
    private Long time;

    /**
     * 内容
     */
    private String content;

    /**
     * 是否撤回 0 未撤回 1 撤回
     */
    private Byte recall = (byte) 0;

    /**
     * 引用内容 因为使用了range来管理消息 如果使用id来引用可能存在数据还没加载/已被清理的情况
     */
    private String ref;

    /**
     * 签名
     */
    private String secret;

    public static final Byte RECALL_NO = 0;
    public static final Byte RECALL_YES = 1;

    /**
     * 生成message ID 生成方式: ms时间戳 + 递增序列
     */
    public void getNewId() {
        this.setId(System.currentTimeMillis() + "" + this.getUserId() % 1000);
    }

    public boolean secretVal(String key) {
        if (StrUtil.isBlank(this.secret)) {
            return false;
        }
        try {
            String secret = HMac.hmac("HmacSHA1", dataSecret(), key);
            return this.secret.equals(secret);
        } catch (Exception e) {
            return false;
        }
    }

    public void secret(String key) {
        try {
            secret = HMac.hmac("HmacSHA1", dataSecret(), key);
        } catch (Exception ignore) {
        }
    }

    private String dataSecret() {
        return id + "_"
                + deviceId + "-"
                + userId + "_"
                + time;
    }
}
