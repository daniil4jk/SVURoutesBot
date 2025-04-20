package ru.daniil4jk.svuroutes.tgbot.keyboard.inline;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;

import java.util.List;

import static ru.daniil4jk.svuroutes.tgbot.keyboard.processing.assets.KeyboardCmdCallHandler.commandPrefix;

@Component
public class AdminPanelKeyboard extends InlineKeyboardMarkup {
    private final InlineKeyboardButton showRequests = new InlineKeyboardButton(CommandTag.ADMIN_REVIEW_REQUESTS.toString());
    private final InlineKeyboardButton createEvent = new InlineKeyboardButton(CommandTag.ADMIN_CREATE_EVENT.toString());
    private final InlineKeyboardButton giveAdmin = new InlineKeyboardButton(CommandTag.GIVE_ADMIN.toString());


    {
        showRequests.setCallbackData(commandPrefix + CommandTag.ADMIN_REVIEW_REQUESTS.getId());
        createEvent.setCallbackData(commandPrefix + CommandTag.ADMIN_CREATE_EVENT.getId());
        giveAdmin.setCallbackData(commandPrefix + CommandTag.GIVE_ADMIN.getId());
        this.setKeyboard(List.of(List.of(showRequests),
                                List.of(createEvent),
                                List.of(giveAdmin)));
    }
}
