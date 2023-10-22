package dev.nkucherenko.redischat.service;

import dev.nkucherenko.redischat.dto.RoomDto;
import java.util.Set;
import java.util.UUID;

public interface RoomService {
    Iterable<RoomDto> getAllRoomsByIds(Set<String> roomIds);
    RoomDto findById(UUID roomId);
}
