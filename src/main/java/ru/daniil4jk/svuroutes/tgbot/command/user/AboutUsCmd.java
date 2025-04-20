package ru.daniil4jk.svuroutes.tgbot.command.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.command.assets.StaticCommand;

@Slf4j
@Component
public class AboutUsCmd extends StaticCommand {
    public AboutUsCmd() {
        super("peoples", "description about us",
                CommandTag.ABOUT_US, null);
    }
}
