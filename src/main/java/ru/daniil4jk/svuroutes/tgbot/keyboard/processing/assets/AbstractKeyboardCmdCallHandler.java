package ru.daniil4jk.svuroutes.tgbot.keyboard.processing.assets;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.daniil4jk.svuroutes.tgbot.bot.Bot;
import ru.daniil4jk.svuroutes.tgbot.command.CommandService;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.command.assets.SimpleBotCommand;
import ru.daniil4jk.svuroutes.tgbot.command.assets.TaggedCommand;

public abstract class AbstractKeyboardCmdCallHandler implements KeyboardCmdCallHandler {
    @Autowired
    private CommandService commands;
    @Autowired
    private Bot bot;

    @Override
    public boolean canAccept(Message message) {
        String text = message.getText().trim();
        return canAccept(text, true);
    }

    @Override
    public boolean canAccept(CallbackQuery callbackQuery) {
        String text = callbackQuery.getData().trim();
        return callbackQueryDataPreCheck(text) &&
                canAccept(callbackQueryDataPreProcessing(text), false);
    }

    private boolean callbackQueryDataPreCheck(String text) {
        return text.startsWith(KeyboardCmdCallHandler.commandPrefix);
    }

    public abstract boolean canAccept(String text, boolean onlyText);

    @Override
    public void accept(Message message) {
        accept(message.getText(), message.getChatId(), true);
    }

    @Override
    public void accept(CallbackQuery callbackQuery) {
        accept(callbackQueryDataPreProcessing(callbackQuery.getData()),
                callbackQuery.getMessage().getChatId(), false);
    }

    private String callbackQueryDataPreProcessing(String data) {
        return data.replace(commandPrefix, "");
    }

    public abstract void accept(String text, long chatId, boolean onlyText);

    public void execute(CommandTag tag, long chatId, String[] args) {
        TaggedCommand cmd =commands.getCommandByTag(tag);
        if (cmd instanceof SimpleBotCommand scmd) {
            scmd.execute(bot, chatId, args);
        } else {
            throw new IllegalArgumentException("Не реализовано"); //чтобы реализовать, надо конкретно переделать систему
        }
    }
}
