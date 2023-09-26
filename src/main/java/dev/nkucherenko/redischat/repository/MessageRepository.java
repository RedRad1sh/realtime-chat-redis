package dev.nkucherenko.redischat.repository;

import dev.nkucherenko.redischat.dto.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface MessageRepository extends CrudRepository<Message, String> {
    Set<Message> findAll();
}
