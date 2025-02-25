package ru.daniil4jk.svuroutes.tgbot.keyboard.processing.assets;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface KeyboardDataHandler {
    boolean canAccept(Message message);
    boolean canAccept(CallbackQuery callbackQuery);
    void accept(Message message);
    void accept(CallbackQuery callbackQuery);
}
