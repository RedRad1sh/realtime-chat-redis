package dev.nkucherenko.redischat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nkucherenko.redischat.component.MessageReceiver;
import dev.nkucherenko.redischat.dto.Message;
import dev.nkucherenko.redischat.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final ReactiveRedisMessageListenerContainer container;
    private final ChannelTopic channelTopic;
    private final MessageService messageService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/post")
    public void postMessage(@RequestBody Message message) {
        message.setId(UUID.randomUUID());
        messageService.postMessage(message);
    }

    @GetMapping(value = "/chatAlive", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> subscribe() {
        final Sinks.Many<String> sink = Sinks.many().multicast().directBestEffort();
        Flux<String> stringFlux = Flux.just(messageService.getAllMessages()
                .stream().map(Message::toString)
                .collect(Collectors.toList())
                .toArray(new String[]{}));

        return stringFlux.concatWith(container.receive(channelTopic).map(ReactiveSubscription.Message::getMessage));
    }
}
