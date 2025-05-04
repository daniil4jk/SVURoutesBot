package ru.daniil4jk.svuroutes.tgbot.keyboard.reply;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.command.MessageConfig;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
@Component
public class DefaultKeyboard extends ReplyKeyboardMarkup {
    private final KeyboardButton events = new KeyboardButton(String.valueOf(CommandTag.EVENTS));
    private final KeyboardButton myRequests = new KeyboardButton(String.valueOf(CommandTag.REQUESTS));
    private final KeyboardButton routes = new KeyboardButton(String.valueOf(CommandTag.ROUTES));
    private final KeyboardButton dots = new KeyboardButton(String.valueOf(CommandTag.DOTS));
    private final KeyboardButton aboutUs = new KeyboardButton(String.valueOf(CommandTag.ABOUT_US));
    private final KeyboardButton aboutProject = new KeyboardButton(String.valueOf(CommandTag.ABOUT_PROJECT));
    private final KeyboardButton addSuggestion = new KeyboardButton(String.valueOf(CommandTag.ADD_SUGGESTION));
    private final List<KeyboardRow> keyboard = new ArrayList<>(List.of(
            new KeyboardRow(List.of(events, myRequests)),
            new KeyboardRow(List.of(aboutUs, aboutProject)),
            new KeyboardRow(List.of(addSuggestion))
    ));

    {
        setSelective(true);
        setResizeKeyboard(true);
        setOneTimeKeyboard(false);
        setKeyboard(keyboard);
    }

    public DefaultKeyboard(MessageConfig config) {
        List<KeyboardButton> additionalButtons = new ArrayList<>();
        if (config.isShowDots()) {
            additionalButtons.add(dots);
        }
        if (config.isShowRoutes()) {
            additionalButtons.add(routes);
        }
        if (!additionalButtons.isEmpty()) {
            keyboard.add(1, new KeyboardRow(additionalButtons));
            setKeyboard(keyboard);
        }
    }

    public void addRow(KeyboardRow row) {
        keyboard.add(row);
        setKeyboard(keyboard);
    }
}
