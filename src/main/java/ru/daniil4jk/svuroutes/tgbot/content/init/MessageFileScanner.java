package ru.daniil4jk.svuroutes.tgbot.content.init;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.content.ContentStorageConfig;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.MessageEntry;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

@Slf4j
@Component
public class MessageFileScanner {
    @Autowired
    private ContentStorageConfig config;
    @Autowired
    private ObjectMapper mapper;

    @Bean
    public Map<CommandTag, MessageEntry> messageMap() {
        try {
            Map<String, MessageEntry> stringMap = mapper
                    .readValue(new File(config.getMessages()), new TypeReference<>() {
                    });
            Map<CommandTag, MessageEntry> result = new EnumMap<>(CommandTag.class);
            for (Map.Entry<String, MessageEntry> entry : stringMap.entrySet()) {
                result.put(CommandTag.normalValueOf(entry.getKey()), entry.getValue());
            }
            for (CommandTag key : CommandTag.values()) {
                if (!result.containsKey(key)) {
                    result.put(key, new MessageEntry(
                            String.format("Добавьте сообщение \"%s\" в %s",
                                    key.toString(), config.getMessages()),
                            null, null, null));
                }
            }
            return result;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return Map.of();
    }
}
