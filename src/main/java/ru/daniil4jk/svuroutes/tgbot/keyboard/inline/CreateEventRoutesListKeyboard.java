package ru.daniil4jk.svuroutes.tgbot.keyboard.inline;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.Route;
import ru.daniil4jk.svuroutes.tgbot.keyboard.KeyboardConfig;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.assets.TListKeyboard;

import java.util.Collection;

@Slf4j
@Component
public class CreateEventRoutesListKeyboard extends TListKeyboard<Route> {
    public CreateEventRoutesListKeyboard(Collection<Route> routes, KeyboardConfig config) {
        super(routes, CommandData.ADMIN_CREATE_EVENT, config);
    }

    @Override
    protected String getName(Route route) {
        return route.getName();
    }

    @Override
    protected Long getId(Route route) {
        return route.getId();
    }
}
