package ru.daniil4jk.svuroutes.tgbot.keyboard.inline;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.db.entity.EventEntity;
import ru.daniil4jk.svuroutes.tgbot.db.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

import static ru.daniil4jk.svuroutes.tgbot.keyboard.processing.assets.KeyboardCmdCallHandler.commandPrefix;

public class EventKeyboard extends InlineKeyboardMarkup {
    public EventKeyboard(EventEntity event, UserEntity user) {
        var registerButton = new InlineKeyboardButton(CommandTag.REGISTER.toString());
        registerButton.setCallbackData(commandPrefix + CommandTag.REGISTER.getId() + "_" + event.getId());
        List<InlineKeyboardButton> buttons = new ArrayList<>(List.of(registerButton));
        if (user.isAdmin()) {
            var removeButton = new InlineKeyboardButton(CommandTag.ADMIN_REMOVE_EVENT.toString());
            removeButton.setCallbackData(commandPrefix + CommandTag.ADMIN_REMOVE_EVENT.getId() + "_" + event.getId());
            buttons.add(removeButton);
        }
        this.setKeyboard(List.of(buttons));
    }

}
