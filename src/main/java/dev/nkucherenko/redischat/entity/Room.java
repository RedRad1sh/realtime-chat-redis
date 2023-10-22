package dev.nkucherenko.redischat.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@RedisHash("Room")
@Accessors(chain = true)
@Data
public class Room {
    private UUID id;
    @Indexed
    private String chatId;
    private String name;
    private boolean isDirectMessages = false;
}
