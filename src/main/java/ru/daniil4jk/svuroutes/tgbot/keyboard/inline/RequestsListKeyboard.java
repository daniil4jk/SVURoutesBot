package ru.daniil4jk.svuroutes.tgbot.keyboard.inline;

import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.db.entity.RequestEntity;
import ru.daniil4jk.svuroutes.tgbot.keyboard.KeyboardConfig;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.assets.TListKeyboard;

import java.util.Collection;

public class RequestsListKeyboard extends TListKeyboard<RequestEntity> {
    public RequestsListKeyboard(Collection<RequestEntity> requests, KeyboardConfig config) {
        super(requests, CommandTag.REQUEST, config);
    }

    @Override
    protected String getName(RequestEntity request) {
        var event = request.getEvent();
        return "#" + request.getId() + " - " + event.getName() + "#" + event.getId();
    }

    @Override
    protected Long getId(RequestEntity request) {
        return request.getId();
    }
}
