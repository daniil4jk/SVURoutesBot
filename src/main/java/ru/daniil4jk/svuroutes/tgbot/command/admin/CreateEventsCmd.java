package ru.daniil4jk.svuroutes.tgbot.command.admin;

import org.springframework.stereotype.Component;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.command.assets.StaticCommand;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.CreateEventRoutesListKeyboard;

@Component
public class CreateEventsCmd extends StaticCommand {
    public CreateEventsCmd(CreateEventRoutesListKeyboard keyboard) {
        super("create", "creates new event",
                CommandData.ADMIN_CREATE_EVENTS, keyboard);
        setOnlyAdminAccess(true);
    }
}
