package dev.nkucherenko.redischat.dto;

import dev.nkucherenko.redischat.entity.Room;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class MessageDto {
    private UUID id;
    private UUID userId;
    private String content;
    private LocalDateTime time;
    private String name;
    private String pfpUrl;
    private String chatId;
    private Room room;
}
