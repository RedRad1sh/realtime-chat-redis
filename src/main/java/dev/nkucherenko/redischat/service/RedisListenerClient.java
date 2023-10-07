package dev.nkucherenko.redischat.service;

import dev.nkucherenko.redischat.dto.UserNotificationDto;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.listener.ChannelTopic;

import java.util.Set;

public interface RedisListenerClient {
    UserNotificationDto receiveNotification(String resolvedChatId, ReactiveSubscription.Message<String, String> a);
    Set<ChannelTopic> getChannelTopics(String userId, String resolvedChatId);
}
