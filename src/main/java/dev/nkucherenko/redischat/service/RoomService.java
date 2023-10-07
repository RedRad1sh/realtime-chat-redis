package dev.nkucherenko.redischat.service;

import dev.nkucherenko.redischat.dto.RoomDto;
import dev.nkucherenko.redischat.entity.Room;

import java.util.Set;

public interface RoomService {
    Iterable<RoomDto> getAllRoomsByIds(Set<String> roomIds);
}
