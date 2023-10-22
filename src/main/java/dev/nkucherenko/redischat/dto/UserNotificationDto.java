package dev.nkucherenko.redischat.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@ToString
public class UserNotificationDto {
    NotificationTypeEnum type;
    MessageDto message;
}
