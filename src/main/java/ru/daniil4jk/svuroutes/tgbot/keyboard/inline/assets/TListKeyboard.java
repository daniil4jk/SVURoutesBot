package ru.daniil4jk.svuroutes.tgbot.keyboard.inline.assets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.keyboard.KeyboardConfig;
import ru.daniil4jk.svuroutes.tgbot.keyboard.processing.assets.KeyboardCmdCallHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
public abstract class TListKeyboard<T> extends InlineKeyboardMarkup {
    @JsonIgnore
    private final KeyboardConfig config;
    @JsonIgnore
    private final Integer commandId;
    @JsonIgnore
    private final Collection<T> ts;

    public TListKeyboard(Collection<T> ts, KeyboardConfig config) {
        this.config = config;
        this.ts = ts;
        commandId = null;
        fill();
    }

    public TListKeyboard(Collection<T> ts, CommandTag data, KeyboardConfig config) {
        this.config = config;
        this.ts = ts;
        commandId = data.getId();
        fill();
    }

    public TListKeyboard(Collection<T> ts, int commandId, KeyboardConfig config) {
        this.config = config;
        this.ts = ts;
        this.commandId = commandId;
        fill();
    }

    private void fill() {
        String prefix;
        if (commandId == null) {
            prefix = "";
        } else {
            prefix = KeyboardCmdCallHandler.commandPrefix + commandId + "_";
        }

        InlineKeyboardButton[] buttons = new InlineKeyboardButton[ts.size()];
        int i = 0;
        for (T t : ts) {
            buttons[i] = new InlineKeyboardButton(getName(t));
            buttons[i].setCallbackData(prefix + getId(t));
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
            if (buttonsInRow == config.getMaxButtonsInRow()) {
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

    public void remove(T t) {
        ts.remove(t);
        fill();
    }

    protected abstract String getName(T t);
    protected abstract Long getId(T t);
}
