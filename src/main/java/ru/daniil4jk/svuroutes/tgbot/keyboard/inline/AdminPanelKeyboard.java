package ru.daniil4jk.svuroutes.tgbot.keyboard.inline;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;

import java.util.List;

@Component
public class AdminPanelKeyboard extends InlineKeyboardMarkup {
    private final InlineKeyboardButton showRequests = new InlineKeyboardButton(CommandData.ADMIN_REQUESTS.toString());
    private final InlineKeyboardButton createEvent = new InlineKeyboardButton(CommandData.ADMIN_CREATE_EVENTS.toString());

    {
        showRequests.setCallbackData(String.valueOf(CommandData.ADMIN_REQUESTS.getId()));
        createEvent.setCallbackData(String.valueOf(CommandData.ADMIN_CREATE_EVENTS.getId()));
        this.setKeyboard(List.of(List.of(showRequests),
                                List.of(createEvent)));
    }
}
