package dev.nkucherenko.redischat;


import dev.nkucherenko.redischat.service.ChatGptService;
import dev.nkucherenko.redischat.service.impl.ChatGptServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class ChatGptServiceTest {
    ChatGptService chatGptService = new ChatGptServiceImpl();

    @Test
    void testRequest() throws IOException {
        System.out.println(chatGptService.sendAndReceiveMessage("Кот", "Привет, как дела?"));
    }
}