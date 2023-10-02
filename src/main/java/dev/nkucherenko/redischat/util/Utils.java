package dev.nkucherenko.redischat.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.redis.listener.ChannelTopic;
import org.thymeleaf.util.StringUtils;

import java.util.Map;

@UtilityClass
@Slf4j
public class Utils {
    public static ChannelTopic resolveChannelTopic(String resolvedChatId, GenericApplicationContext applicationContext) {
        Map<String, ChannelTopic> beans = applicationContext.getBeansOfType(ChannelTopic.class);
        return beans.values().stream()
                .filter(channelTopic -> channelTopic.getTopic().equals(resolvedChatId))
                .findFirst().orElseGet(() -> {
                    ChannelTopic newChannelTopic = new ChannelTopic(resolvedChatId);
                    applicationContext.registerBean("topic:" + resolvedChatId, ChannelTopic.class, () -> newChannelTopic);
                    log.debug("New bean topic:" + resolvedChatId + " registered");
                    return new ChannelTopic(resolvedChatId);
                });
    }
}
