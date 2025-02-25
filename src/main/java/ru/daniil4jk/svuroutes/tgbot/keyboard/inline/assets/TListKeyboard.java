package ru.daniil4jk.svuroutes.tgbot.keyboard.inline.assets;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.keyboard.KeyboardConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Slf4j
public abstract class TListKeyboard<T> extends InlineKeyboardMarkup {
    public final byte maxButtonsInRow;
    private final Collection<T> ts;
    private final CommandData data;

    public TListKeyboard(Collection<T> ts, CommandData data, KeyboardConfig config) {
        maxButtonsInRow = config.getMaxButtonsInRow();
        this.ts = ts;
        this.data = data;
        fill();
    }

    private void fill() {
        InlineKeyboardButton[] buttons = new InlineKeyboardButton[ts.size()];
        int i = 0;
        for (T t : ts) {
            buttons[i] = new InlineKeyboardButton(getName(t));
            buttons[i].setCallbackData(data.getId() + "_" + getId(t));
            i++;
        }
        setKeyboard(structure(buttons));
    }

    private List<List<InlineKeyboardButton>> structure(InlineKeyboardButton[] buttons) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(new ArrayList<>());
        byte buttonsInRow = 0;
        for (InlineKeyboardButton button : buttons) {
            keyboard.get(keyboard.size() - 1).add(button);
            buttonsInRow++;
            if (buttonsInRow == maxButtonsInRow) {
                buttonsInRow = 0;
                keyboard.add(new ArrayList<>());
            }
        }
        return keyboard;
    }

    public void add(T t) {
        ts.add(t);
        fill();
    }

    protected abstract String getName(T t);
    protected abstract Long getId(T t);
}
