package ru.daniil4jk.svuroutes.tgbot.keyboard.reply;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.command.MessageConfig;

import java.util.List;

@Component
public class AdminKeyboard extends DefaultKeyboard {
    private final KeyboardButton adminPanel = new KeyboardButton(CommandData.ADMIN_PANEL.toString());

    {
        addRow(new KeyboardRow(List.of(adminPanel)));
    }

    public AdminKeyboard(MessageConfig config) {
        super(config);
    }
}
