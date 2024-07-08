package org.mercury.im.room.service.impl;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.Data;
import org.mercury.im.room.service.SyncMessageService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class SyncMessageServiceImpl implements SyncMessageService {

    private final Disruptor<Message> disruptor = new Disruptor<>(
            new MessageFactory(),
            512,
            new ThreadPoolTaskExecutor(),
            ProducerType.SINGLE,
            new BlockingWaitStrategy());

    public SyncMessageServiceImpl() {
        disruptor.start();
    }

    public boolean sendMessage(Long userId) {
        return disruptor.getRingBuffer().tryPublishEvent((event, sequence) -> event.setUserId(userId));
    }

    public void subscribe(Consumer<Message> consumer) {
        // 订阅消息
        disruptor.handleEventsWith((event, sequence, endOfBatch) -> {
            // 处理消息
            System.out.println("userId: " + event.getUserId());
        });
    }

    public static class MessageEventHandler implements EventHandler<Message> {
        @Override
        public void onEvent(Message event, long sequence, boolean endOfBatch) {
            // 处理消息
            System.out.println("userId: " + event.getUserId());
        }
    }


    // 消息工厂
    public static class MessageFactory implements EventFactory<Message> {
        @Override
        public Message newInstance() {
            return new Message();
        }
    }

    // 消息类
    @Data
    public static class Message {
        private Long userId;
    }
}
