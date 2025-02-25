package ru.daniil4jk.svuroutes.tgbot.expected.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.daniil4jk.svuroutes.tgbot.expected.handlers.assets.ExpectedHandlerAbstractImpl;

@Component
public class ExpectedMessageHandler extends ExpectedHandlerAbstractImpl<Message> {
    @Override
    public long getChatId(Message message) {
        return message.getChatId();
    }

    @Override
    public String getContent(Message message) {
        return message.getText();
    }
}

