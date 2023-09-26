package dev.nkucherenko.redischat.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;

import java.util.Arrays;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class MessageReceiver implements Function<Message, String> {

    @Override
    public String apply(Message message) {
        log.info("Received <" + Arrays.toString(message.getBody()) + ">");
        return Arrays.toString(message.getBody());
    }
}
