package dev.nkucherenko.redischat.repository;


import dev.nkucherenko.redischat.entity.Room;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends CrudRepository<Room, UUID> {
    Optional<Room> findByChatId(String chatId);
}
