package dev.nkucherenko.redischat.controller;

import static dev.nkucherenko.redischat.util.Utils.resolveChannelTopic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nkucherenko.redischat.entity.Message;
import dev.nkucherenko.redischat.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.GenericApplicationContext;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final ReactiveRedisMessageListenerContainer listenerContainer;
    private List<ChannelTopic> channelTopics;
    private final MessageService messageService;
    private final ObjectMapper objectMapper;
    private final GenericApplicationContext applicationContext;

    @PostMapping(value = "/post")
    public void postMessage(@RequestBody Message message, Authentication authentication) {
        log.info(message.toString());
        String userId = authentication.getDetails().toString();
        message.setUserId(UUID.randomUUID());
        messageService.postMessage(message, userId);
    }

    @GetMapping(value = "/chatAlive", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Message> subscribe(@RequestParam @Nullable String chatId, Authentication authentication) {
        String userId = authentication.getDetails().toString();
        String resolvedChatId = messageService.resolveChatId(chatId, userId);
        Flux<Message> stringFlux = Flux.just(new ArrayList<>(messageService.getAllMessagesByChatId(resolvedChatId, userId))
                .toArray(new Message[]{}));

        ChannelTopic channelTopic = resolveChannelTopic(resolvedChatId, applicationContext);

        return stringFlux.concatWith(listenerContainer.receive(channelTopic).map(a -> {
            try {
                return objectMapper.readValue(a.getMessage(), Message.class);
            } catch (JsonProcessingException jsonProcessingException) {
                return new Message();
            }
        }));
    }
}
