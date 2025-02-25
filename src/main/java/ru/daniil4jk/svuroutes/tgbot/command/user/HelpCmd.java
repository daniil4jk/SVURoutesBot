package ru.daniil4jk.svuroutes.tgbot.command.user;

import org.springframework.stereotype.Component;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.command.assets.StaticCommand;

@Component
public class HelpCmd extends StaticCommand {
    public HelpCmd() {
        super("help", "help by commands",
                CommandData.HELP, null);
    }
}
