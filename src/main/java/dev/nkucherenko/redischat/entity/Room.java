package dev.nkucherenko.redischat.entity;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@RedisHash("Room")
@Data
public class Room {
    private UUID id;
    private String name;
}
