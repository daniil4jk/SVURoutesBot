package ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

public interface SimpleExecuter {
    <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws TelegramApiException;
    <T extends Serializable, Method extends BotApiMethod<T>> T nonExceptionExecute(Method method);
    <T extends Serializable> T sendSimpleTextMessage(String messageText, long chatId);
}
