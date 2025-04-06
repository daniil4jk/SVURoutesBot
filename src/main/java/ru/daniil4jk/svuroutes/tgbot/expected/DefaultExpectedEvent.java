package ru.daniil4jk.svuroutes.tgbot.expected;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.daniil4jk.svuroutes.tgbot.keyboard.reply.CancelReplyKeyboard;

import java.util.function.Consumer;

public class DefaultExpectedEvent<T> extends ExpectedEvent<T> {

    public DefaultExpectedEvent(String notification,
                                Consumer<T> setter,
                                long chatId) {
        super(setter);
        var message = SendMessage.builder()
                .text(notification)
                .chatId(chatId)
                .build();
        firstNotification(message);
        notification(message);
        onException(e -> SendMessage.builder()
                .text(e.getLocalizedMessage())
                .chatId(chatId)
                .build());
        removeOnException(false);
    }

    public DefaultExpectedEvent(String notification,
                                Consumer<T> setter,
                                String cancelTriggerText,
                                String cancelText,
                                long chatId) {
        super(setter);
        var message = SendMessage.builder()
                .text(notification)
                .chatId(chatId)
                .replyMarkup(new CancelReplyKeyboard(cancelTriggerText))
                .build();
        firstNotification(message);
        notification(message);
        onException(e -> SendMessage.builder()
                .text(e.getLocalizedMessage())
                .chatId(chatId)
                .build());
        removeOnException(false);
        cancelTrigger(cancelTriggerText);
        cancelText(cancelText);
    }
}
