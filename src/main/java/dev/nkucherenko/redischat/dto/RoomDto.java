package dev.nkucherenko.redischat.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class RoomDto {
    private UUID id;
    private String chatId;
    private String name;
    private boolean isDirectMessages = false;
    private int unreadMessagesCount = 0;
}
