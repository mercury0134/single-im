package org.mercury.im.room.core.util;

import org.mercury.im.common.core.objects.Tuple2;

public class BusinessIdUtil {


    /**
     * 获取单聊id
     */
    public static String converseId(Long userId, Long toUserId) {
        // 创建规则 c-用户id-对方用户id 其中userId比较小放在前面 禁止重复
        if (userId == null || toUserId == null) {
            throw new IllegalArgumentException("userId or toUserId is null");
        }
        if (Long.compare(userId, toUserId) == 0) {
            throw new IllegalArgumentException("userId and toUserId is same");
        }

        return "c-" + Math.min(userId, toUserId) + "-" + Math.max(userId, toUserId);
    }

    /**
     * converseId 反序列成 userId
     */
    public static Tuple2<Long, Long> parseConverseId(String converseId) {
        String[] split = converseId.split("-");
        if (split.length != 3) {
            throw new IllegalArgumentException("converseId: " + converseId + " is illegal");
        }
        Tuple2<Long, Long> tuple2 = new Tuple2<>(Long.parseLong(split[1]), Long.parseLong(split[2]));
        // 判断两个user id 是否一样
        if (Long.compare(tuple2.getFirst(), tuple2.getSecond()) == 0) {
            throw new IllegalArgumentException("converseId: " + converseId + " user id is same");
        }
        // 判断是否是正序
        if (Long.compare(tuple2.getFirst(), tuple2.getSecond()) > 0) {
            throw new IllegalArgumentException("converseId: " + converseId + " is not positive sequence");
        }
        return tuple2;
    }

}
