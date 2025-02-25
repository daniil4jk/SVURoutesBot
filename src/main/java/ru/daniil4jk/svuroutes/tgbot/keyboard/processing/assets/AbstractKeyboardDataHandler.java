package ru.daniil4jk.svuroutes.tgbot.keyboard.processing.assets;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

public abstract class AbstractKeyboardDataHandler implements KeyboardDataHandler{
    @Override
    public boolean canAccept(Message message) {
        return canAccept(message.getText(), true);
    }

    @Override
    public boolean canAccept(CallbackQuery callbackQuery) {
        return canAccept(callbackQuery.getData(), false);
    }

    public abstract boolean canAccept(String text, boolean onlyText);

    @Override
    public void accept(Message message) {
        accept(message.getText(), message.getChatId(), true);
    }

    @Override
    public void accept(CallbackQuery callbackQuery) {
        accept(callbackQuery.getData(), callbackQuery.getMessage().getChatId(), false);
    }

    public abstract void accept(String text, long chatId, boolean onlyText);
}
