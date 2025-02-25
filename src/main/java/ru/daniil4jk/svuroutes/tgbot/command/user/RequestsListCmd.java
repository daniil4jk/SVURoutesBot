package ru.daniil4jk.svuroutes.tgbot.command.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.command.assets.StaticCommand;
import ru.daniil4jk.svuroutes.tgbot.keyboard.KeyboardConfig;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.RequestsListKeyboard;

import java.util.stream.Collectors;

@Component
public class RequestsListCmd extends StaticCommand {
    @Autowired
    private KeyboardConfig config;

    public RequestsListCmd() {
        super("requests", "show list of actual requests",
                CommandData.REQUESTS, null);
    }
    //TODO добавить "Мои заявки"
    //TODO добавить оповещение пользователя, о том что его заявка рассмотрена


    @Override
    public void protectedExecute(AbsSender absSender, long chatId, String[] strings) {
        executeWithKeyboard(absSender, chatId, new RequestsListKeyboard(
                getRequestService().getByUserId(chatId).stream()
                .filter(r -> r.getEvent().getDate().getTime() > System.currentTimeMillis())
                .collect(Collectors.toSet()), config
        ));
    }
}
