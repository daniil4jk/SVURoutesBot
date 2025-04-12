package ru.daniil4jk.svuroutes.tgbot.expected;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.daniil4jk.svuroutes.tgbot.keyboard.reply.CancelReplyKeyboard;

import java.util.function.Consumer;

public class DefaultExpectedEvent<T> extends ExpectedEvent<T> {

    public DefaultExpectedEvent(SendMessage notification,
                                Consumer<T> setter,
                                long chatId) {
        super(setter);
        firstNotification(notification);
        notification(notification);
        onException(e -> SendMessage.builder()
                .text(e.getLocalizedMessage())
                .chatId(chatId)
                .build());
        removeOnException(false);
    }

    public DefaultExpectedEvent(String notification,
                                Consumer<T> setter,
                                long chatId) {
        this(SendMessage.builder()
                    .text(notification)
                    .chatId(chatId)
                    .build(),
                setter, chatId);
    }

    public DefaultExpectedEvent(String notification,
                                Consumer<T> setter,
                                String cancelTriggerText,
                                String cancelText,
                                long chatId) {
        this(notification, setter, chatId);
        var message = SendMessage.builder()
                .text(notification)
                .chatId(chatId)
                .replyMarkup(new CancelReplyKeyboard(cancelTriggerText))
                .build();
        firstNotification(message);
        notification(message);
        cancelTrigger(cancelTriggerText);
        cancelText(cancelText);
    }
}
