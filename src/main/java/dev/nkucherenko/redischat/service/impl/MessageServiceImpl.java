package dev.nkucherenko.redischat.service.impl;

import static dev.nkucherenko.redischat.util.Utils.checkUUIDValidity;
import static dev.nkucherenko.redischat.util.Utils.resolveChannelTopic;

import dev.nkucherenko.redischat.dto.MessageDto;
import dev.nkucherenko.redischat.entity.Message;
import dev.nkucherenko.redischat.entity.Room;
import dev.nkucherenko.redischat.entity.UserDetails;
import dev.nkucherenko.redischat.exception.UserNotFoundException;
import dev.nkucherenko.redischat.mapper.MessageMapper;
import dev.nkucherenko.redischat.repository.MessageRepository;
import dev.nkucherenko.redischat.repository.RoomRepository;
import dev.nkucherenko.redischat.repository.UserRepository;
import dev.nkucherenko.redischat.service.ChatGptService;
import dev.nkucherenko.redischat.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {
    public static final String DELIMITER_CONSTANT = "%s:%s";
    public static final String GENERAL_CHAT_ID = "general";

    private final MessageMapper messageMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final GenericApplicationContext applicationContext;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final Comparator<Message> messageComparator = Comparator.comparing(Message::getTime);

    private final ChatGptService chatGptService;

    @Override
    public void postMessage(Message message, String userId) {
        message.setUserId(UUID.fromString(userId));
        String chatId = resolveChatId(message.getChatId(), userId);
        if (message.getChatId()!= null && !message.getChatId().equals(userId) && checkUUIDValidity(message.getChatId())) {
            updateUserRooms(message.getChatId(), userId, chatId);
        }
        message.setChatId(chatId);


        messageRepository.save(message);
        String channelTopic = resolveChannelTopic(chatId, applicationContext).getTopic();
        redisTemplate.convertAndSend(channelTopic, message);

        if (message.getContent().startsWith(chatGptService.getBotName())) {
            sendGptBotMessage(message, chatId, channelTopic);
        }
    }

    private void sendGptBotMessage(Message message, String chatId, String channelTopic) {
        ExecutorService executorsService = Executors.newSingleThreadExecutor();
        executorsService.submit(() -> {
            try {
                MessageDto messageDto = chatGptService.sendAndReceiveMessage(chatId, message.getContent());
                messageDto.setChatId(chatId);
                messageDto.setTime(LocalDateTime.now());

                Message msgChatGpt = messageRepository.save(messageMapper.messageDtoToMessage(messageDto));
                redisTemplate.convertAndSend(channelTopic, msgChatGpt);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public List<MessageDto> getAllMessagesByChatId(String chatId, String userId) {
        log.info(String.format("User %s was connected to %s chat", userId, chatId));

        TreeSet<Message> messageTreeSet = new TreeSet<>(messageComparator);
        Set<Message> messagesSet = messageRepository.findByChatId(chatId).stream()
                .filter(msg -> msg != null && msg.getTime() != null).collect(Collectors.toSet());
        messageTreeSet.addAll(messagesSet);
        return messageMapper.messageListToDto(messageTreeSet);
    }

    public String resolveChatId(String chatId, String userId) {
        String resolvedChatId;
        if (!StringUtils.isEmpty(chatId) && userExistsByChatId(chatId)) {
            resolvedChatId = buildChatId(chatId, userId);
        } else {
            resolvedChatId = GENERAL_CHAT_ID;
        }
        return resolvedChatId;
    }

    private boolean userExistsByChatId(String chatId) {
        try {
            UUID userId = UUID.fromString(chatId);
            return userRepository.existsById(userId);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private String buildChatId(String chatId, String userId) {
        String resolvedChatId;
        if (chatId.compareTo(userId) > 0) {
            resolvedChatId = String.format(DELIMITER_CONSTANT, chatId, userId);
        } else {
            resolvedChatId = String.format(DELIMITER_CONSTANT, userId, chatId);
        }
        return resolvedChatId;
    }

    private void updateUserRooms(String chatId, String userId, String resolvedChatId) {
        Optional<Room> roomOptional = roomRepository.findByChatId(resolvedChatId);
        UserDetails userChat = userRepository.findById(UUID.fromString(chatId))
                .orElseThrow(UserNotFoundException::new);
        UserDetails userInitiator = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(UserNotFoundException::new);

        if (roomOptional.isEmpty()) {
            String userNameChat = userChat.getUserName();

            String userNameInitiator = userInitiator.getUserName();

            Room savedRoom = roomRepository.save(
                    new Room().setChatId(resolvedChatId)
                            .setName(String.format(DELIMITER_CONSTANT, userNameChat, userNameInitiator))
                            .setDirectMessages(true));

            updateUserRooms(chatId, savedRoom);
            updateUserRooms(userId, savedRoom);

            redisTemplate.convertAndSend(resolveChannelTopic(resolvedChatId, applicationContext).getTopic(),
                    new Message()
                            .setPfpUrl(userChat.getPfpUrl())
                            .setRoom(savedRoom));
        }

    }

    private void updateUserRooms(String userId, Room room) {
        userRepository.findById(UUID.fromString(userId))
                .ifPresent(user -> {
                    user.getRoomIds().add(room.getId().toString());
                    userRepository.save(user);
                });
    }
}
