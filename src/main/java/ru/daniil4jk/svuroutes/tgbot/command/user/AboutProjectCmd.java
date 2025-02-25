package ru.daniil4jk.svuroutes.tgbot.command.user;

import org.springframework.stereotype.Component;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.command.assets.StaticCommand;

@Component
public class AboutProjectCmd extends StaticCommand {
    public AboutProjectCmd() {
        super("about", "description about project",
                CommandData.ABOUT_PROJECT, null);
    }
}
