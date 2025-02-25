package ru.daniil4jk.svuroutes.tgbot.keyboard.inline;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class BooleanKeyboard extends InlineKeyboardMarkup {
    public BooleanKeyboard(String trueText, String falseText) {
        var trueButton = new InlineKeyboardButton(trueText);
        var falseButton = new InlineKeyboardButton(falseText);
        trueButton.setCallbackData(String.valueOf(true));
        falseButton.setCallbackData(String.valueOf(false));
        this.setKeyboard(List.of(List.of(trueButton, falseButton)));
    }

    public BooleanKeyboard(String trueText, String falseText, String exitText) {
        var trueButton = new InlineKeyboardButton(trueText);
        var falseButton = new InlineKeyboardButton(falseText);
        var exitButton = new InlineKeyboardButton(exitText);
        trueButton.setCallbackData(String.valueOf(true));
        falseButton.setCallbackData(String.valueOf(false));
        exitButton.setCallbackData("exit");
        this.setKeyboard(List.of(List.of(trueButton, falseButton),
                List.of(exitButton)));
    }
}
