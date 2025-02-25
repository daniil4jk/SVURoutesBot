package ru.daniil4jk.svuroutes.tgbot.keyboard.reply;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;

import java.util.ArrayList;
import java.util.List;

@Component
public class DefaultKeyboard extends ReplyKeyboardMarkup {
    private final KeyboardButton events = new KeyboardButton(String.valueOf(CommandData.EVENTS));
    private final KeyboardButton myRequests = new KeyboardButton(String.valueOf(CommandData.REQUESTS));
    private final KeyboardButton routes = new KeyboardButton(String.valueOf(CommandData.ROUTES));
    private final KeyboardButton dots = new KeyboardButton(String.valueOf(CommandData.DOTS));
    private final KeyboardButton aboutUs = new KeyboardButton(String.valueOf(CommandData.ABOUT_US));
    private final KeyboardButton aboutProject = new KeyboardButton(String.valueOf(CommandData.ABOUT_PROJECT));
    private final KeyboardButton addSuggestion = new KeyboardButton(String.valueOf(CommandData.ADD_SUGGESTION));
    private final List<KeyboardRow> keyboard = new ArrayList<>(List.of(
            new KeyboardRow(List.of(events, myRequests)),
            new KeyboardRow(List.of(routes, dots)),
            new KeyboardRow(List.of(aboutUs, aboutProject)),
            new KeyboardRow(List.of(addSuggestion))
    ));

    {
        setSelective(true);
        setResizeKeyboard(true);
        setOneTimeKeyboard(false);
        setKeyboard(keyboard);
    }

    public void addRow(KeyboardRow row) {
        keyboard.add(row);
        setKeyboard(keyboard);
    }
}
