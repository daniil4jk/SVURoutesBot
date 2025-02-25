package ru.daniil4jk.svuroutes.tgbot.keyboard.reply;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class CancelKeyboard extends ReplyKeyboardMarkup {
    private final KeyboardButton cancel = new KeyboardButton();
    private final List<KeyboardRow> keyboard = List.of(new KeyboardRow(List.of(cancel)));
    @Getter
    private final String text;

    {
        setSelective(true);
        setResizeKeyboard(true);
        setOneTimeKeyboard(false);
        setKeyboard(keyboard);
    }

    public CancelKeyboard(String text) {
        cancel.setText(text);
        this.text = text;
    }
}
