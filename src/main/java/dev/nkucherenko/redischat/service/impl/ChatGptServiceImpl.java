package dev.nkucherenko.redischat.service.impl;

import dev.nkucherenko.redischat.dto.MessageDto;
import dev.nkucherenko.redischat.service.ChatGptService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class ChatGptServiceImpl implements ChatGptService {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json");

    @Value("${gpt.constants.apiUrl}")
    private String apiUrl;
    @Value("${gpt.constants.jsonTemplate}")
    private String jsonTemplate;
    @Value("${gpt.constants.botBehaviour}")
    private String botBehaviour;
    @Value("${gpt.constants.botPfp}")
    private String botPfp;
    @Value("${gpt.constants.botName}")
    private String botName;
    @Value("${gpt.constants.errorMessage}")
    private String errorMessage;

    @Override
    public String getBotName() {
        return botName;
    }

    @Override
    public MessageDto sendAndReceiveMessage(String userName, String content) throws IOException {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(
                String.format(jsonTemplate, UUID.randomUUID(), botBehaviour, content),
                MEDIA_TYPE
        );

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        MessageDto messageDto = new MessageDto().setName(botName)
                .setPfpUrl(botPfp)
                .setContent(String.format(">%1.60s %n%n", content) + responseBody);
        if (responseBody.contains("\"error\":")) {
            messageDto.setContent(String.format(">%1.60s %n%n", content) + errorMessage);
        }
        return messageDto;
    }
}
