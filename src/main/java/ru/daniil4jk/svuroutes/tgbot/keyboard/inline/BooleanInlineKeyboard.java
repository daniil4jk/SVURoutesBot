package ru.daniil4jk.svuroutes.tgbot.keyboard.inline;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class BooleanInlineKeyboard extends InlineKeyboardMarkup {
    public static class Data {
        public static final String TRUE = String.valueOf(true);
        public static final String FALSE = String.valueOf(false);
        public static final String CANCEL = "exit";
    }

    public BooleanInlineKeyboard(String trueText, String falseText) {
        var trueButton = new InlineKeyboardButton(trueText);
        var falseButton = new InlineKeyboardButton(falseText);
        trueButton.setCallbackData(Data.TRUE);
        falseButton.setCallbackData(Data.FALSE);
        this.setKeyboard(new ArrayList<>(List.of(List.of(trueButton, falseButton))));
    }

    public BooleanInlineKeyboard(String trueText, String falseText, String cancelText) {
        this(trueText, falseText);
        var cancelButton = new InlineKeyboardButton(cancelText);
        cancelButton.setCallbackData(Data.CANCEL);
        getKeyboard().add(List.of(cancelButton));
    }
}
