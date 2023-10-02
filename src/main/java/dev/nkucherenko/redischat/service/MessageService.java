package dev.nkucherenko.redischat.service;

import dev.nkucherenko.redischat.entity.Message;

import java.util.TreeSet;

public interface MessageService {
    /**
     * Post message in chat
     *
     * @param message of the User
     * @return result code
     */
    String postMessage(Message message, String userName);

    /**
     * Get messages ordered by dateTime
     *
     * @return messages
     */
    TreeSet<Message> getAllMessagesByChatId(String chatId, String userName);

    String resolveChatId(String chatId, String userId);
}
