package ru.daniil4jk.svuroutes.tgbot.command;

import lombok.Getter;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;

import java.util.HashMap;
import java.util.Map;

@Service
@Getter
public class CommandService {
    private final IBotCommand[] commands;
    private final Map<Class<? extends IBotCommand>, IBotCommand> commandMap = new HashMap<>();

    public CommandService(IBotCommand[] commands) {
        this.commands = commands;
        for (IBotCommand cmd : commands) {
            commandMap.put(cmd.getClass(), cmd);
        }
    }

    public <T extends IBotCommand> T getCommand(Class<T> t) {
        return (T) commandMap.get(t);
    }
}
