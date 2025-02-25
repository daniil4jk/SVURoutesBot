package ru.daniil4jk.svuroutes.tgbot.bot.assets;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

@Setter
@Getter
@Slf4j
@Component
public abstract class UserExceptionReporter {
    private boolean userLogging = false;

    public void reportExceptionToUsers(Update update, Throwable e) {
        if (userLogging) {
            Long chatId = null;
            if (update.hasMessage()) chatId = update.getMessage().getChatId();
            else if (update.hasCallbackQuery()) chatId = update.getCallbackQuery().getMessage().getChatId();

            if (chatId != null) {
                String[] messages = splitForLimit(getStackStaceInString(e));
                try {
                    sendMessage(SendMessage.builder()
                            .text("Ошибка: " + e.getLocalizedMessage() +
                                    ".\n\nПожалуйста, сообщите админу о проблеме. Тех. админ в данный момент: @daniil4jk" +
                                    "\n\nИнформация для разработчиков:\n" +
                                    messages[0])
                            .chatId(chatId)
                            .build());
                    for (int i = 1; i < messages.length; i++) {
                        sendMessage(SendMessage.builder()
                                .text(messages[i])
                                .chatId(chatId)
                                .build());
                    }
                } catch (TelegramApiException ex) {
                    log.error(e.getLocalizedMessage(), e);
                    log.error(ex.getLocalizedMessage(), ex);
                }
            }
        }
    }

    private String getStackStaceInString(Throwable e) {
        var stringWriter = new StringWriter();
        var printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    private static final int limit = 4000;

    private String[] splitForLimit(String input) {
        String[] strings = input.split("\n");
        String[] results = new String[Math.toIntExact(Math.floorDiv(input.length(), limit)) + 1];
        StringBuilder sb = new StringBuilder();
        int resultI = 0;
        for (String string : strings) {
            if (sb.length() + string.length() >= limit) {
                results[resultI] = sb.toString();
                sb = new StringBuilder();
                resultI++;
            }
            sb.append(string);
        }
        results[resultI] = sb.toString();
        return results;
    }

    protected abstract void sendMessage(SendMessage message) throws TelegramApiException;
}
