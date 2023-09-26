package dev.nkucherenko.redischat.service;

import dev.nkucherenko.redischat.dto.Message;
import dev.nkucherenko.redischat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

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
        messageTreeSet.addAll(messageRepository.findAll());
        return messageTreeSet;
    }
}
