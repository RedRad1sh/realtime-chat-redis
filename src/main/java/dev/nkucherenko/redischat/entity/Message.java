package dev.nkucherenko.redischat.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@ToString
@RedisHash("Message")
public class Message {
    private UUID id;
    private UUID userId;
    @Size(min = 1, max = 2000)
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime time;
    private String name;
    private String pfpUrl;
}