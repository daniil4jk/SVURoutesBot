package ru.daniil4jk.svuroutes.tgbot.keyboard.inline;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.Route;
import ru.daniil4jk.svuroutes.tgbot.keyboard.KeyboardConfig;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.assets.TListKeyboard;

import java.util.Collection;
import java.util.Comparator;

@Slf4j
@Component
public class RoutesListKeyboard extends TListKeyboard<Route> {
    public RoutesListKeyboard(Collection<Route> routes, KeyboardConfig config) {
        super(routes.stream().sorted(Comparator.comparingLong(Route::getId)).toList(),
                CommandData.ROUTE, config);
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
