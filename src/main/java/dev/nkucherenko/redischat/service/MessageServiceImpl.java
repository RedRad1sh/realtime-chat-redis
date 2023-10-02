package dev.nkucherenko.redischat.service;

import dev.nkucherenko.redischat.entity.Message;
import dev.nkucherenko.redischat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MessageServiceImpl implements MessageService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;
    private final MessageRepository messageRepository;
    private Comparator<Message> messageComparator = Comparator.comparing(Message::getTime);

    @Override
    public String postMessage(Message message) {
        message.setTime(LocalDateTime.now());
        messageRepository.save(message);
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
        return "200";
    }

    @Override
    public TreeSet<Message> getAllMessages() {
        TreeSet<Message> messageTreeSet = new TreeSet<>(messageComparator);
        Set<Message> messagesSet = messageRepository.findAll().stream()
                .filter(msg -> msg != null && msg.getTime() != null).collect(Collectors.toSet());
        messageTreeSet.addAll(messagesSet);
        return messageTreeSet;
    }
}
