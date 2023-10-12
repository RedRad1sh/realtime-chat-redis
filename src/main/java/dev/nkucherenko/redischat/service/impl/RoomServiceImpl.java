package dev.nkucherenko.redischat.service.impl;

import dev.nkucherenko.redischat.dto.RoomDto;
import dev.nkucherenko.redischat.exception.UnknownRoomException;
import dev.nkucherenko.redischat.mapper.RoomMapper;
import dev.nkucherenko.redischat.repository.RoomRepository;
import dev.nkucherenko.redischat.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    @Override
    public Iterable<RoomDto> getAllRoomsByIds(Set<String> roomIds) {
        return roomMapper.roomToDto(roomRepository.findAllById(
                roomIds.stream().map(UUID::fromString)
                        .toList())
        );
    }

    @Override
    public RoomDto findById(UUID roomId) {
        return roomMapper.roomToDto(roomRepository.findById(roomId).orElseThrow(() -> {
            throw new UnknownRoomException("Room wasn't found");
        }));
    }
}