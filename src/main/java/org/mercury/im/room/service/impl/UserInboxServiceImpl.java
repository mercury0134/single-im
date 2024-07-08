package org.mercury.im.room.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.Resource;
import org.mercury.im.common.lock.annonation.Lock;
import org.mercury.im.domain.room.model.MessageItem;
import org.mercury.im.domain.room.repository.ConverseRangeRepository;
import org.mercury.im.room.core.config.RocksDBConfig;
import org.mercury.im.room.service.ConverseDbService;
import org.mercury.im.room.service.MessageService;
import org.mercury.im.room.service.UserInboxService;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.StringAppendOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserInboxServiceImpl implements UserInboxService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RocksDB rocksDB;
    private final Cache<Long, MessageQueue> inbox;

    @Resource
    private ConverseDbService converseDbService;

    @Resource
    private ConverseRangeRepository converseRangeRepository;

    @Resource
    private MessageService messageService;

    // 降序
    public static Comparator<MessageItem> ageComparator = Comparator.comparing(MessageItem::getId).reversed();

    public UserInboxServiceImpl(RocksDBConfig rocksDBConfig) {
        try (final Options options = new Options().setCreateIfMissing(true)) {
            rocksDB = RocksDB.open(options, rocksDBConfig.getUserBoxDb());
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }

        inbox = Caffeine.newBuilder()
                .maximumSize(100000)
                .expireAfterAccess(1, TimeUnit.HOURS)
                .build();
    }

    @Override
    public MessageQueue user(Long userId) {
        return inbox.get(userId, k -> loadUserInbox(userId));
    }

    @Lock(keys = {"toUserId"}, prefix = "inbox_", expire = 20, timeUnit = TimeUnit.SECONDS)
    @Override
    public boolean addMessage(Long toUserId, MessageItem message) {
        // TODO 提供降级方案 用户收信箱用不了

        MessageQueue queue = user(toUserId);
        return queue.add(message);
    }

    @Lock(keys = {"userId"}, prefix = "inbox_", expire = 20, timeUnit = TimeUnit.SECONDS)
    @Override
    public List<MessageItem> userNoAlreadyMessage(Long userId) {
        String s = alreadyRead(userId); // 获取already message id
        s = StrUtil.isBlank(s) ? "" : s;
        MessageQueue messageQueue = user(userId);
        return messageQueue.getRange(s);
    }

    @Override
    public boolean alreadyRead(Long toUserId, String messageId) {
        byte[] key = ("already_" + toUserId).getBytes();
        try {
            byte[] value = rocksDB.get(key);
            if (value == null || messageId.compareTo(new String(value)) > 0) {
                rocksDB.put(key, messageId.getBytes());
            }
            return true;
        } catch (Exception ignore) {
        }
        return false;
    }

    @Override
    public String alreadyRead(Long userId) {
        byte[] key = ("already_" + userId).getBytes();
        try {
            byte[] value = rocksDB.get(key);
            if (value == null) {
                return null;
            }
            return new String(value);
        } catch (Exception ignore) {
        }
        return null;
    }

    /**
     * 加载用户的收信箱
     */
    public MessageQueue loadUserInbox(Long userId) {
        // 获取用户的会话记录
        List<String> converseIds = converseDbService.userConverse(userId);

        // 获取用户已读消息id
        String messageId = alreadyRead(userId);
        final String alreadyMessageId = StrUtil.isBlank(messageId) ? "" : messageId;

        // 这里使用虚拟线程池优化 懒得!!! TODO Executors.newVirtualThreadPerTaskExecutor();
        try {
            List<MessageItem> list = converseIds.stream()
                    .map(converseId -> converseRangeRepository.getNoAlreadyMessage(converseId, alreadyMessageId))
                    .toList().stream()
                    .flatMap(Collection::stream)
                    .map(range -> messageService.list(range.getFirstMessageId()))
                    .flatMap(Collection::stream).sorted(ageComparator).toList();
            return new MessageQueue(list);
        } catch (RuntimeException e) {
            // TODO 立马告警
            logger.error("load user inbox:{} exception!", userId);
            throw e;
        }
    }

    public static class MessageQueue {
        private final PriorityQueue<MessageItem> queue = new PriorityQueue<>(ageComparator);

        public MessageQueue() {
        }

        public MessageQueue(Collection<MessageItem> messageItems) {
            boolean b = addAll(messageItems);
        }

        public synchronized boolean add(MessageItem messageItem) {
            return queue.add(messageItem);
        }

        public synchronized boolean addAll(Collection<MessageItem> messageItems) {
            return queue.addAll(messageItems);
        }

        public synchronized List<MessageItem> getRange(String messageId) {
            if (StrUtil.isBlank(messageId)) {
                return new ArrayList<>(queue);
            }
            List<MessageItem> result = new ArrayList<>();
            for (MessageItem next : queue) {
                if (next.getId().compareTo(messageId) >= 0) {
                    break;
                }
                result.add(next);
            }
            return result;
        }
    }
}
