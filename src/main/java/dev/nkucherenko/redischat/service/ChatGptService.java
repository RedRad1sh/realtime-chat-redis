package dev.nkucherenko.redischat.service;

import dev.nkucherenko.redischat.dto.MessageDto;

import java.io.IOException;

public interface ChatGptService {
    MessageDto sendAndReceiveMessage(String userName, String content) throws IOException;
    String getBotName();
}
