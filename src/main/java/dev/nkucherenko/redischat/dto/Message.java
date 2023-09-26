package dev.nkucherenko.redischat.dto;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@ToString
@RedisHash("Message")
public class Message {
    private UUID id;
    private UUID userId;
    private String content;
    private LocalDateTime time;
}
