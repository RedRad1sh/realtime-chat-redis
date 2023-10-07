package dev.nkucherenko.redischat.controller;

import dev.nkucherenko.redischat.dto.NotificationTypeEnum;
import dev.nkucherenko.redischat.dto.UserNotificationDto;
import dev.nkucherenko.redischat.entity.Message;
import dev.nkucherenko.redischat.service.MessageService;
import dev.nkucherenko.redischat.service.RedisListenerClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final ReactiveRedisConnectionFactory redisConnectionFactory;
    private final MessageService messageService;
    private final RedisListenerClient redisListenerClient;

    @PostMapping(value = "/post")
    public void postMessage(@RequestBody Message message, Authentication authentication) {
        log.info(message.toString());
        String userId = authentication.getDetails().toString();
        message.setUserId(UUID.fromString(userId));
        messageService.postMessage(message, userId);
    }

    @GetMapping(value = "/chatAlive", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<UserNotificationDto> subscribe(@RequestParam @Nullable String chatId, Authentication authentication) {
        String userId = authentication.getDetails().toString();
        String resolvedChatId = messageService.resolveChatId(chatId, userId);
        Flux<UserNotificationDto> savedMessagesFlux = getSavedMessages(userId, resolvedChatId);

        Set<ChannelTopic> channelTopicSet = redisListenerClient.getChannelTopics(userId, resolvedChatId);
        ReactiveRedisMessageListenerContainer listenerContainer = new ReactiveRedisMessageListenerContainer(redisConnectionFactory);

        return concatSavedAndReceivedMessages(savedMessagesFlux, resolvedChatId, channelTopicSet, listenerContainer);
    }

    @NotNull
    private Flux<UserNotificationDto> concatSavedAndReceivedMessages(Flux<UserNotificationDto> savedMessagesFlux,
                                                                     String resolvedChatId, Set<ChannelTopic> channelTopicSet,
                                                                     ReactiveRedisMessageListenerContainer listenerContainer) {
        return savedMessagesFlux.concatWith(listenerContainer
                .receive(channelTopicSet.toArray(new ChannelTopic[]{}))
                .map(a -> redisListenerClient.receiveNotification(resolvedChatId, a))
        );

    }

    @NotNull
    private Flux<UserNotificationDto> getSavedMessages(String userId, String resolvedChatId) {
        return Flux.just(new ArrayList<>(messageService.getAllMessagesByChatId(resolvedChatId, userId))
                .stream().map(msg -> new UserNotificationDto()
                        .setMessage(msg)
                        .setType(NotificationTypeEnum.CHAT_MESSAGE)).toArray(UserNotificationDto[]::new));
    }


}
