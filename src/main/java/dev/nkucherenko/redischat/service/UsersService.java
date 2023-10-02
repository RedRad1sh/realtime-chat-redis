package dev.nkucherenko.redischat.service;

public interface UsersService {
    String createUser(String name, String pfpUrl);

    String getUser(String id);

    String getUserMessages();
}
