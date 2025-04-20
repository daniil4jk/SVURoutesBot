package ru.daniil4jk.svuroutes.tgbot.command.user;

import org.springframework.stereotype.Component;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.command.assets.StaticCommand;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.EventsKeyboard;

@Component
public class EventsCmd extends StaticCommand {
    public EventsCmd(EventsKeyboard keyboard) {
        super("events", "show actual events", CommandTag.EVENTS, keyboard);
    }
}
