package ru.daniil4jk.svuroutes.tgbot.expected.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.daniil4jk.svuroutes.tgbot.expected.handlers.assets.ExpectedHandlerAbstractImpl;

@Component
public class ExpectedCallbackQueryHandler extends ExpectedHandlerAbstractImpl<CallbackQuery> {

    @Override
    public long getChatId(CallbackQuery callbackQuery) {
        return callbackQuery.getMessage().getChatId();
    }

    @Override
    public String getContent(CallbackQuery callbackQuery) {
        return callbackQuery.getData();
    }
}
