package ru.daniil4jk.svuroutes.tgbot.command;

import lombok.Getter;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import ru.daniil4jk.svuroutes.tgbot.command.assets.TaggedCommand;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Service
@Getter
public class CommandService {
    private final IBotCommand[] commands;
    private final Map<Class<? extends IBotCommand>, IBotCommand> commandClassMap = new HashMap<>();
    private final Map<CommandTag, TaggedCommand> commandTagMap = new EnumMap<>(CommandTag.class);

    public CommandService(IBotCommand[] commands,
                          Collection<TaggedCommand> taggedCommands) {
        this.commands = commands;
        for (var cmd : commands) {
            commandClassMap.put(cmd.getClass(), cmd);
        }

        for (var cmd : taggedCommands) {
            if (cmd.getTag() != null) {
                commandTagMap.put(cmd.getTag(), cmd);
            }

        }
    }

    @Deprecated
    public <T extends IBotCommand> T getCommandByClass(Class<T> t) {
        return (T) commandClassMap.get(t);
    }

    public TaggedCommand getCommandByTag(CommandTag tag) {
        return commandTagMap.get(tag);
    }
}
