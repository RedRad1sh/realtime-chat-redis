package dev.nkucherenko.redischat.repository;

import dev.nkucherenko.redischat.entity.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface MessageRepository extends CrudRepository<Message, UUID> {
    Set<Message> findByChatId(String chatId);
}
