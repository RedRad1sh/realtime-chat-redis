package dev.nkucherenko.redischat.service.impl;

import static dev.nkucherenko.redischat.util.Utils.resolveChannelTopic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nkucherenko.redischat.dto.MessageDto;
import dev.nkucherenko.redischat.dto.NotificationTypeEnum;
import dev.nkucherenko.redischat.dto.RoomDto;
import dev.nkucherenko.redischat.dto.UserNotificationDto;
import dev.nkucherenko.redischat.entity.Message;
import dev.nkucherenko.redischat.entity.Room;
import dev.nkucherenko.redischat.entity.UserDetails;
import dev.nkucherenko.redischat.mapper.MessageMapper;
import dev.nkucherenko.redischat.repository.UserRepository;
import dev.nkucherenko.redischat.service.RedisListenerClient;
import dev.nkucherenko.redischat.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisListenerClientImpl implements RedisListenerClient {
    private final GenericApplicationContext applicationContext;
    private final RoomService roomService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final MessageMapper messageMapper;

    @Override
    public Set<ChannelTopic> getChannelTopics(String userId, String resolvedChatId) {
        log.info(String.format("getChannelTopics -> %s, %s", userId, resolvedChatId));
        ChannelTopic channelTopic = resolveChannelTopic(resolvedChatId, applicationContext);
        UserDetails userDetails = userRepository.findById(UUID.fromString(userId)).orElseThrow();
        Set<ChannelTopic> channelTopicSet = userDetails.getRoomIds().stream().map(roomId -> {
            RoomDto room = roomService.findById(UUID.fromString(roomId));
            return resolveChannelTopic(room.getChatId(), applicationContext);
        }).collect(Collectors.toSet());
        channelTopicSet.add(channelTopic);
        return channelTopicSet;
    }

    public UserNotificationDto receiveNotification(String resolvedChatId, ReactiveSubscription.Message<String, String> a) {
        log.info(String.format("receiveNotification -> %s", resolvedChatId));
        try {
            Message message = objectMapper.readValue(a.getMessage(), Message.class);
            MessageDto messageDto = messageMapper.messageToDto(message);
            UserNotificationDto userNotificationDto = new UserNotificationDto()
                    .setMessage(messageDto);
            if (message.getId() == null && message.getRoom() != null) {
                return userNotificationDto.setType(NotificationTypeEnum.NEW_ROOM);
            }
            if (resolvedChatId.equals(message.getChatId())) {
                return userNotificationDto.setType(NotificationTypeEnum.CHAT_MESSAGE);
            }
            return userNotificationDto.setType(NotificationTypeEnum.NOTIFICATION_MESSAGE);
        } catch (JsonProcessingException jsonProcessingException) {
            return new UserNotificationDto();
        }

    }
}
