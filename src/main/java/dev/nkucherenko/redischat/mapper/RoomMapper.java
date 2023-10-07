package dev.nkucherenko.redischat.mapper;

import dev.nkucherenko.redischat.dto.RoomDto;
import dev.nkucherenko.redischat.entity.Room;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    RoomDto roomToDto(Room room);
    Iterable<RoomDto> roomToDto(Iterable<Room> room);
}
