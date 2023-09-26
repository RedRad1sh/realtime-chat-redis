package dev.nkucherenko.redischat.service;

import java.util.Optional;

public interface UsersService {
   String createUser(String name, String pfpUrl);
   String getUser(String id);
   String getUserMessages();
}
