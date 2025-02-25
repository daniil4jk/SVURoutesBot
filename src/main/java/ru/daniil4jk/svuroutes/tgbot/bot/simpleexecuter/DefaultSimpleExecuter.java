package ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

@Slf4j
public class DefaultSimpleExecuter implements SimpleExecuter {
    private final AbsSender absSender;

    public DefaultSimpleExecuter(AbsSender absSender) {
        this.absSender = absSender;
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws TelegramApiException {
        return absSender.execute(method);
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T nonExceptionExecute(Method method) {
        try {
            return absSender.execute(method);
        } catch (TelegramApiException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public <T extends Serializable> T sendSimpleTextMessage(String messageText, long chatId) {
        return (T) nonExceptionExecute(SendMessage.builder()
                .chatId(chatId)
                .text(messageText)
                .build());
    }
}
