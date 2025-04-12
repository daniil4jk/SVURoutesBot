package ru.daniil4jk.svuroutes.tgbot.keyboard.inline;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.db.entity.EventEntity;
import ru.daniil4jk.svuroutes.tgbot.db.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

import static ru.daniil4jk.svuroutes.tgbot.keyboard.processing.assets.KeyboardCmdCallHandler.commandPrefix;

public class EventKeyboard extends InlineKeyboardMarkup {
    public EventKeyboard(EventEntity event, UserEntity user) {
        var registerButton = new InlineKeyboardButton(CommandData.REGISTER.toString());
        registerButton.setCallbackData(commandPrefix + CommandData.REGISTER.getId() + "_" + event.getId());
        List<InlineKeyboardButton> buttons = new ArrayList<>(List.of(registerButton));
        if (user.isAdmin()) {
            var removeButton = new InlineKeyboardButton(CommandData.ADMIN_REMOVE_EVENT.toString());
            removeButton.setCallbackData(commandPrefix + CommandData.ADMIN_REMOVE_EVENT.getId() + "_" + event.getId());
            buttons.add(removeButton);
        }
        this.setKeyboard(List.of(buttons));
    }

}
