package ru.daniil4jk.svuroutes.tgbot.content;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.MessageEntry;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommandMessageService implements ContentService<CommandTag, MessageEntry> {
    private final Map<CommandTag, MessageEntry> map;

    @Override
    public MessageEntry get(CommandTag tag) {
        return map.get(tag);
    }
}
