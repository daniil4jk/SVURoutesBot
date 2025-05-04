package ru.daniil4jk.svuroutes.tgbot.command.assets;

import lombok.Getter;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;

public abstract class SimpleBotCommand implements IBotCommand, TaggedCommand {
    @Getter
    private final CommandTag tag;
    private final String commandIdentifier;
    private final String description;

    public SimpleBotCommand(String commandIdentifier, String description, CommandTag tag) {
        this.commandIdentifier = commandIdentifier;
        this.description = description;
        this.tag = tag;
    }

    @Override
    public String getCommandIdentifier() {
        return commandIdentifier;
    };

    @Override
    public String getDescription() {
        return description;
    };

    @Override
    public final void processMessage(AbsSender absSender, Message message, String[] strings) {
        execute(absSender, message.getChatId(), strings);
    }

    abstract public void execute(AbsSender absSender, long chatId, String[] strings);
}
