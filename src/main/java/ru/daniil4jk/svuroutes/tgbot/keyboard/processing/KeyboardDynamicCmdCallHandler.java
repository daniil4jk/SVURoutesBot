package ru.daniil4jk.svuroutes.tgbot.keyboard.processing;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.daniil4jk.svuroutes.tgbot.bot.Bot;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.command.CommandService;
import ru.daniil4jk.svuroutes.tgbot.command.admin.RemoveEventCmd;
import ru.daniil4jk.svuroutes.tgbot.command.user.*;
import ru.daniil4jk.svuroutes.tgbot.keyboard.processing.assets.AbstractKeyboardCmdCallHandler;

@Setter
@Component
public class KeyboardDynamicCmdCallHandler extends AbstractKeyboardCmdCallHandler {
    @Autowired
    private Bot bot;
    @Autowired
    private CommandService commands;

    public boolean canAccept(String text, boolean onlyText) {
        try {
            CommandTag prefix = getPrefix(text);
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
        CommandTag prefix = getPrefix(text);
        String prefixText = (!startsWithNumber(text) || onlyText) ?
                prefix.toString() : String.valueOf(prefix.getId());
        String argsInString = text.substring(prefixText.length()).trim();
        if (argsInString.startsWith("_")) {
            argsInString = argsInString.substring(1);
        }
        accept(prefix, chatId, argsInString.split("_"));
    }

    public void accept(CommandTag tag, long chatId, String[] args) {
        commands.getCommandByTag(tag).execute(bot, chatId, args);
    }

    private CommandTag getPrefix(String text) {
        for (int i = CommandTag.values().length - 1; i >= 0; i--) {
            CommandTag prefix = CommandTag.values()[i];
            String idInStr = String.valueOf(prefix.getId());
            if (text.startsWith(idInStr)) {
                return prefix;
            }
        }
        for (CommandTag prefix : CommandTag.values()) {
            if (text.startsWith(prefix.toString())) {
                return prefix;
            }
        }
        throw new IllegalArgumentException("Unknown prefix");
    }
}
