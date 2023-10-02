package dev.nkucherenko.redischat.service;

import static dev.nkucherenko.redischat.util.Utils.resolveChannelTopic;

import dev.nkucherenko.redischat.entity.Message;
import dev.nkucherenko.redischat.repository.MessageRepository;
import dev.nkucherenko.redischat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final GenericApplicationContext applicationContext;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final Comparator<Message> messageComparator = Comparator.comparing(Message::getTime);

    @Override
    public String postMessage(Message message, String userId) {
        message.setTime(LocalDateTime.now());
        message.setUserId(UUID.fromString(userId));

        String chatId = resolveChatId(message.getChatId(), userId);
        message.setChatId(chatId);

        messageRepository.save(message);
        redisTemplate.convertAndSend(resolveChannelTopic(chatId, applicationContext).getTopic(), message);
        return "200";
    }

    @Override
    public TreeSet<Message> getAllMessagesByChatId(String chatId, String userId) {
        log.info(String.format("User %s was connected to %s chat", userId, chatId));

        TreeSet<Message> messageTreeSet = new TreeSet<>(messageComparator);
        Set<Message> messagesSet = messageRepository.findByChatId(chatId).stream()
                .filter(msg -> msg != null && msg.getTime() != null).collect(Collectors.toSet());
        messageTreeSet.addAll(messagesSet);
        return messageTreeSet;
    }

    private boolean userExistsByChatId(String chatId) {
        try {
            UUID userId = UUID.fromString(chatId);
            return userRepository.existsById(userId);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String resolveChatId(String chatId, String userId) {
        if (!StringUtils.isEmpty(chatId) && userExistsByChatId(chatId)) {
            if (chatId.compareTo(userId) > 0) {
                return String.format("%s:%s", chatId, userId);
            } else {
                return String.format("%s:%s", userId, chatId);
            }
        } else {
            return "general";
        }
    }
}
