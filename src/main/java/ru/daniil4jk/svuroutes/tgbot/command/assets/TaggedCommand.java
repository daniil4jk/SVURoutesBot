package ru.daniil4jk.svuroutes.tgbot.command.assets;

import lombok.Getter;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;

//todo transform it to interface and add startCommand
public class TaggedCommand extends SimpleBotCommand {
    @Getter
    private final CommandTag tag;

    public TaggedCommand(String commandIdentifier, String description, CommandTag tag) {
        super(commandIdentifier, description);
        this.tag = tag;
    }

    @Override
    public void execute(AbsSender absSender, long chatId, String[] strings) {

    }
}
