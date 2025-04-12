package ru.daniil4jk.svuroutes.tgbot.keyboard.inline;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;

import java.util.List;

import static ru.daniil4jk.svuroutes.tgbot.keyboard.processing.assets.KeyboardCmdCallHandler.commandPrefix;

@Component
public class AdminPanelKeyboard extends InlineKeyboardMarkup {
    private final InlineKeyboardButton showRequests = new InlineKeyboardButton(CommandData.ADMIN_REQUESTS.toString());
    private final InlineKeyboardButton createEvent = new InlineKeyboardButton(CommandData.ADMIN_CREATE_EVENT.toString());
    private final InlineKeyboardButton giveAdmin = new InlineKeyboardButton(CommandData.GIVE_ADMIN.toString());


    {
        showRequests.setCallbackData(commandPrefix + CommandData.ADMIN_REQUESTS.getId());
        createEvent.setCallbackData(commandPrefix + CommandData.ADMIN_CREATE_EVENT.getId());
        giveAdmin.setCallbackData(commandPrefix + CommandData.GIVE_ADMIN.getId());
        this.setKeyboard(List.of(List.of(showRequests),
                                List.of(createEvent),
                                List.of(giveAdmin)));
    }
}
