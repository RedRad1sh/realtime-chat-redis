package dev.nkucherenko.redischat.service;

import dev.nkucherenko.redischat.dto.Message;

import java.util.TreeSet;

public interface MessageService {
    /**
     * Post message in chat
     *
     * @param message of the User
     * @return result code
     */
    String postMessage(Message message);

    /**
     * Get messages ordered by dateTime
     * @return messages
     */
    TreeSet<Message> getAllMessages();
}
