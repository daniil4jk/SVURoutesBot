package ru.daniil4jk.svuroutes.tgbot.keyboard.processing;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.daniil4jk.svuroutes.tgbot.bot.Bot;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.command.CommandService;
import ru.daniil4jk.svuroutes.tgbot.command.admin.CreateEventCmd;
import ru.daniil4jk.svuroutes.tgbot.command.admin.RemoveEventCmd;
import ru.daniil4jk.svuroutes.tgbot.command.user.*;
import ru.daniil4jk.svuroutes.tgbot.keyboard.processing.assets.AbstractKeyboardDataHandler;

@Setter
@Component
public class DynamicKeyboardDataHandler extends AbstractKeyboardDataHandler {
    @Autowired
    private Bot bot;
    @Autowired
    private CommandService commands;

    public boolean canAccept(String text, boolean onlyText) {
        text = text.trim();
        try {
            CommandData prefix = getPrefix(text);
            String prefixText = (!startsWithNumber(text) || onlyText) ?
                    prefix.toString() : String.valueOf(prefix.getId());
            return text.length() > prefixText.length();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean startsWithNumber(String s) {
        for (int i = 0; i <= 9; i++) {
            if (s.startsWith(String.valueOf(i))) return true;
        }
        return false;
    }

    @Override
    public void accept(String text, long chatId, boolean onlyText) {
        CommandData prefix = getPrefix(text);
        String prefixText = (!startsWithNumber(text) || onlyText) ?
                prefix.toString() : String.valueOf(prefix.getId());
        String argsInString = text.substring(prefixText.length()).trim();
        if (argsInString.startsWith("_")) {
            argsInString = argsInString.substring(1);
        }
        accept(prefix, chatId, argsInString.split("_"));
    }

    public void accept(CommandData data, long chatId, String[] args) {
        switch (data) {
            case DOT -> commands.getCommand(DotCmd.class).execute(bot, chatId, args);
            case ROUTE -> commands.getCommand(RouteCmd.class).execute(bot, chatId, args);
            case REGISTER -> commands.getCommand(RegisterCmd.class).execute(bot, chatId, args);
            case EVENT -> commands.getCommand(EventCmd.class).execute(bot, chatId, args);
            case ADMIN_CREATE_EVENT -> commands.getCommand(CreateEventCmd.class).execute(bot, chatId, args);
            case ADMIN_REMOVE_EVENT -> commands.getCommand(RemoveEventCmd.class).execute(bot, chatId, args);
            case REQUEST -> commands.getCommand(RequestCmd.class).execute(bot, chatId, args);
            default -> bot.weDontKnowWhatThisIs(chatId);
        }
    }

    private CommandData getPrefix(String text) {
        for (int i = CommandData.values().length - 1; i >= 0; i--) {
            CommandData prefix = CommandData.values()[i];
            String idInStr = String.valueOf(prefix.getId());
            if (text.startsWith(idInStr)) {
                return prefix;
            }
        }
        for (CommandData prefix : CommandData.values()) {
            if (text.startsWith(prefix.toString())) {
                return prefix;
            }
        }
        throw new IllegalArgumentException("Unknown prefix");
    }
}
