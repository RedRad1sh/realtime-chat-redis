package dev.nkucherenko.redischat.repository;

import dev.nkucherenko.redischat.entity.UserDetails;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<UserDetails, UUID> {
    Optional<UserDetails> findByUserName(String userName);
    Optional<UserDetails> findByUserNameAndPassword(String userName, String password);
}
