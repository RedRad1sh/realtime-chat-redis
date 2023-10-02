package dev.nkucherenko.redischat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nkucherenko.redischat.entity.Message;
import dev.nkucherenko.redischat.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.UUID;

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
        log.info(message.toString());
        message.setId(UUID.randomUUID());
        messageService.postMessage(message);
    }

    @GetMapping(value = "/chatAlive", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Message> subscribe() {
        Flux<Message> stringFlux = Flux.just(new ArrayList<>(messageService.getAllMessages())
                .toArray(new Message[]{}));

        return stringFlux.concatWith(container.receive(channelTopic).map(a -> {
            try {
                return objectMapper.readValue(a.getMessage(), Message.class);
            } catch (JsonProcessingException jsonProcessingException) {
                return new Message();
            }
        }));
    }
}
