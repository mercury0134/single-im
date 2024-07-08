package org.mercury.im.room.service.impl;

import jakarta.annotation.Resource;
import org.mercury.im.common.lock.annonation.Lock;
import org.mercury.im.room.core.config.RocksDBConfig;
import org.mercury.im.room.core.util.RocksdbUtil;
import org.mercury.im.room.service.ConverseDbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ConverseDbServiceImpl implements ConverseDbService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RocksDB rocksDB;

    @Resource
    @Lazy
    public ConverseDbServiceImpl converseDbService;

    public ConverseDbServiceImpl(RocksDBConfig rocksDBConfig) {
        BlockBasedTableConfig tableConfig = new BlockBasedTableConfig();
        tableConfig.setBlockSize(64 * 1024);  // 设置缓存大小为 64K TODO 具体调参看系统负载
        try (final Options options = new Options().setCreateIfMissing(true);) {
            options.setMergeOperator(new StringAppendOperator("\\v"));
            options.setTableFormatConfig(tableConfig);
            rocksDB = RocksDB.open(options, rocksDBConfig.getConversationDb());
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean addSingle(ConversationBo bo) {
        String key = bo.getConverseId();
        String value = JsonUtils.toJsonString(bo.toModel());
        try {
            rocksDB.put(key.getBytes(), value.getBytes());
        } catch (Exception ignore) {
            return false;
        }
        return true;
    }

    @Override
    public ConversationBo getById(String converseId) {
        try {
            byte[] bytes = rocksDB.get(converseId.getBytes());
            if (bytes == null) {
                return null;
            }
            return ConversationBo.fromModel(JsonUtils.parse(new String(bytes), ConversationPo.class));
        } catch (Exception ignore) {
            return null;
        }
    }

    @Lock(keys = {"userId"}, prefix = "CONVERSE-", expire = 10, timeUnit = TimeUnit.SECONDS)
    @Override
    public boolean toUser(Long userId, ConversationBo bo) {
        byte[] keyBytes = ("converse-" + userId).getBytes();
        byte[] valueBytes = bo.getConverseId().getBytes();
        try {
            rocksDB.merge(keyBytes, valueBytes);
        } catch (RocksDBException ignore) {
            return false;
        }
        return true;
    }

    public List<String> userConverse(Long userId) {
        byte[] keyBytes = ("converse-" + userId).getBytes();
        try {
            byte[] valueBytes = rocksDB.get(keyBytes);
            if (valueBytes == null) {
                return List.of();
            }
            return Stream.of(new String(valueBytes).split("\\\\v")).collect(Collectors.toList());
        } catch (Exception ignore) {
        }
        return List.of();
    }

    @Lock(keys = {"userId"}, prefix = "CONVERSE-", expire = 10, timeUnit = TimeUnit.SECONDS)
    @Override
    public boolean clearUser(Long userId, String value) {
        String[] converseIds = value.split("\\\\v");

        String result = Stream.of(converseIds).filter(converseId -> {
            ConversationBo conversationBo = getById(converseId);
            if (conversationBo == null) {
                return false;
            }
            if (conversationBo.getExtra() == null || conversationBo.getExtra().getCreateTime() == null) {
                return false;
            }

            return new Date().getTime() - conversationBo.getExtra().getCreateTime() < 1000L * 60 * 60 * 24; // 24小时
        }).map(converseId -> converseId + "\\v").collect(Collectors.joining());

        try {
            rocksDB.put(("converse-" + userId).getBytes(), result.getBytes());
        } catch (Exception ignore) {
            return false;
        }

        return true;
        // TODO 是否需要使用虚拟线程线程池 有待商议
    }


    @Lock(keys = {"flag"}, prefix = "userConverseTask_", expire = 1, timeUnit = TimeUnit.HOURS)
    @Override
    public void userConverseTask(String flag) {
        byte[] prefix = "converse-".getBytes();

        try (RocksIterator iterator = rocksDB.newIterator()) {
            // seek 定位到对应的索引
            for (iterator.seek(prefix); iterator.isValid(); iterator.next()) {
                byte[] key = iterator.key();
                if (!RocksdbUtil.startsWith(key, prefix)) {
                    continue;
                }
                byte[] value = iterator.value();
                Thread.startVirtualThread(() -> converseDbService.clearUser(Long.valueOf(new String(key).replaceAll("converse-", "")), new String(value)));
            }
        }
    }

}
