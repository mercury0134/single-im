package org.mercury.im.room.service.impl;

import org.mercury.im.common.json.JsonUtils;
import org.mercury.im.domain.room.model.MessageItem;
import org.mercury.im.room.core.config.RocksDBConfig;
import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@EnableConfigurationProperties({RocksDBConfig.class})
public class MessageServiceImpl implements MessageService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RocksDB rocksDB;

    public MessageServiceImpl(RocksDBConfig rocksDBConfig) {
        try (final Options options = new Options().setCreateIfMissing(true);) {
            options.setMergeOperator(new StringAppendOperator("\\v"));
            rocksDB = RocksDB.open(options, rocksDBConfig.getMessageDb());
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean sendMessageToDB(String messageId, MessageItem message) {
        String value = JsonUtils.toJsonString(message);
        byte[] keyBytes = messageId.getBytes();
        byte[] valueBytes = value.getBytes();
        try {
            rocksDB.merge(keyBytes, valueBytes);
        } catch (RocksDBException ignore) {
            return false;
        }
        return true;
    }

    public List<MessageItem> list(String firstMessageId) {
        byte[] bytes = firstMessageId.getBytes();
        byte[] valueBytes;
        try {
            valueBytes = rocksDB.get(bytes);
            if (valueBytes == null) {
                return new ArrayList<>();
            }
        } catch (RocksDBException ignore) {
            return new ArrayList<>();
        }

        String s = new String(valueBytes);
        String[] pos = s.split("\\\\v");

        try {
            List<MessageItem> messageItems = new ArrayList<>();
            for (String po : pos) {
                MessageItem messageItem = JsonUtils.parse(po, MessageItem.class);
                messageItems.add(messageItem);
            }
            return messageItems;
        } catch (Exception ignore) {
        }
        return new ArrayList<>();
    }
}
