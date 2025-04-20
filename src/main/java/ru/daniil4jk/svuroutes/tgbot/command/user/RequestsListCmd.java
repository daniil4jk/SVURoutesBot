package ru.daniil4jk.svuroutes.tgbot.command.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.command.assets.StaticCommand;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.RequestService;
import ru.daniil4jk.svuroutes.tgbot.keyboard.KeyboardConfig;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.RequestsListKeyboard;

import java.util.stream.Collectors;

@Component
public class RequestsListCmd extends StaticCommand {
    @Autowired
    private KeyboardConfig config;
    @Autowired
    private RequestService requestService;

    public RequestsListCmd() {
        super("requests", "show list of actual requests",
                CommandTag.REQUESTS, null);
    }

    @Override
    public void protectedExecute(AbsSender absSender, long chatId, String[] strings) {
        executeWithKeyboard(absSender, chatId, new RequestsListKeyboard(
                requestService.getByUserId(chatId).stream()
                .filter(r -> r.getEvent().getDate().getTime() > System.currentTimeMillis())
                .collect(Collectors.toSet()), config
        ));
    }
}
