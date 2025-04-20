package ru.daniil4jk.svuroutes.tgbot.command.user;

import org.springframework.stereotype.Component;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.command.assets.StaticCommand;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.RoutesListKeyboard;

@Component
public class RoutesCmd extends StaticCommand {
    public RoutesCmd(RoutesListKeyboard keyboard) {
        super("routes", "list of routes",
                CommandTag.ROUTES, keyboard);
    }
}
