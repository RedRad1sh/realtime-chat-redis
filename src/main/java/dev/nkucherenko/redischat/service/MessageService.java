package dev.nkucherenko.redischat.service;

import dev.nkucherenko.redischat.dto.MessageDto;
import dev.nkucherenko.redischat.entity.Message;

import java.util.List;

public interface MessageService {
    /**
     * Post message in chat
     *
     * @param message of the User
     */
    void postMessage(Message message, String userName);

    /**
     * Get messages ordered by dateTime
     *
     * @return messages
     */
    List<MessageDto> getAllMessagesByChatId(String chatId, String userName);

    String resolveChatId(String chatId, String userId);
}
