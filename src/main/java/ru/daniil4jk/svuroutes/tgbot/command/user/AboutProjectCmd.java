package ru.daniil4jk.svuroutes.tgbot.command.user;

import org.springframework.stereotype.Component;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.command.assets.StaticCommand;

@Component
public class AboutProjectCmd extends StaticCommand {
    public AboutProjectCmd() {
        super("about", "description about project",
                CommandTag.ABOUT_PROJECT, null);
    }
}
