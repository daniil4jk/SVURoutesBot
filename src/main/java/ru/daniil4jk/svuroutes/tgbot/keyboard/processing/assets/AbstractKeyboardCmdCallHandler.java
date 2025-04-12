package ru.daniil4jk.svuroutes.tgbot.keyboard.processing.assets;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

public abstract class AbstractKeyboardCmdCallHandler implements KeyboardCmdCallHandler {
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
}
