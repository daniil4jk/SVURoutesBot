package ru.daniil4jk.svuroutes.tgbot.keyboard.inline;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.db.entity.EventEntity;

import java.util.List;

public class EventKeyboard extends InlineKeyboardMarkup {
    public EventKeyboard(EventEntity event) {
        var registerButton = new InlineKeyboardButton(CommandData.REGISTER.toString());
        registerButton.setCallbackData(CommandData.REGISTER.getId() + "_" + event.getId());
        this.setKeyboard(List.of(List.of(registerButton)));
    }

}
