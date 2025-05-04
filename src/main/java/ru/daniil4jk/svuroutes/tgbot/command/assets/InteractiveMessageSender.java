package ru.daniil4jk.svuroutes.tgbot.command.assets;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.MessageEntry;

@Slf4j
public class InteractiveMessageSender {
    private final MessageEntry message;
    private final ReplyKeyboard defaultKeyboard;

    public InteractiveMessageSender(MessageEntry message, ReplyKeyboard defaultKeyboard) {
        this.message = message;
        this.defaultKeyboard = defaultKeyboard;
    }

    public InteractiveMessageSender(MessageEntry message) {
        this.message = message;
        this.defaultKeyboard = null;
    }

    public void execute(AbsSender absSender, long chatId) {
        executeWithKeyboard(absSender, chatId, defaultKeyboard);
    }

    public void executeWithKeyboard(AbsSender absSender, long chatId, ReplyKeyboard keyboard) {
        if (keyboard == null) keyboard = defaultKeyboard;

        try {
            if (message.getMediaGroup().isPresent() &&
                    !message.getMediaGroup().get().isEmpty()) {
                absSender.execute(SendMediaGroup.builder()
                        .medias(message.getMediaGroup().get())
                        .chatId(chatId)
                        .build());
            }
            if (message.getImage().isPresent() && message.getVideo().isPresent()) {
                absSender.execute(SendVideo.builder()
                        .video(message.getVideo().get())
                        .chatId(chatId)
                        .build());
                absSender.execute(SendPhoto.builder()
                        .caption(message.getText())
                        .replyMarkup(keyboard)
                        .photo(message.getImage().get())
                        .chatId(chatId)
                        .parseMode("HTML")
                        .build());
            } else if (message.getImage().isPresent()) {
                absSender.execute(SendPhoto.builder()
                        .caption(message.getText())
                        .replyMarkup(keyboard)
                        .photo(message.getImage().get())
                        .chatId(chatId)
                        .parseMode("HTML")
                        .build());
            } else if (message.getVideo().isPresent()) {
                absSender.execute(SendVideo.builder()
                        .caption(message.getText())
                        .replyMarkup(keyboard)
                        .video(message.getVideo().get())
                        .chatId(chatId)
                        .parseMode("HTML")
                        .build());
            } else {
                absSender.execute(SendMessage.builder()
                        .text(message.getText())
                        .replyMarkup(keyboard)
                        .chatId(chatId)
                        .parseMode("HTML")
                        .build());
            }
        } catch (TelegramApiException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }
}
