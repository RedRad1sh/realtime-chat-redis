package dev.nkucherenko.redischat.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.redis.listener.ChannelTopic;

import java.util.Map;
import java.util.UUID;

@UtilityClass
@Slf4j
public class Utils {
    public static ChannelTopic resolveChannelTopic(String resolvedChatId, GenericApplicationContext applicationContext) {
        Map<String, ChannelTopic> beans = applicationContext.getBeansOfType(ChannelTopic.class);
        log.debug(beans.toString());
        return beans.values().stream()
                .filter(channelTopic -> channelTopic.getTopic().equals(resolvedChatId))
                .findFirst().orElseGet(() -> {
                    ChannelTopic newChannelTopic = new ChannelTopic(resolvedChatId);
                    applicationContext.registerBean("topic:" + resolvedChatId, ChannelTopic.class, () -> newChannelTopic);
                    log.debug("New bean topic:" + resolvedChatId + " registered");
                    return new ChannelTopic(resolvedChatId);
                });
    }

    public static boolean checkUUIDValidity(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
