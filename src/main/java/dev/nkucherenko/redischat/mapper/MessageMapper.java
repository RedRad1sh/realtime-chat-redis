package dev.nkucherenko.redischat.mapper;

import dev.nkucherenko.redischat.dto.MessageDto;
import dev.nkucherenko.redischat.entity.Message;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    Message messageDtoToMessage(MessageDto messageDto);

    MessageDto messageToDto(Message message);

    List<MessageDto> messageListToDto(Iterable<Message> messages);
}
