package ru.daniil4jk.svuroutes.tgbot.command.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.command.assets.StaticCommand;

@Slf4j
@Component
public class AboutUsCmd extends StaticCommand {
    public AboutUsCmd() {
        super("peoples", "description about us",
                CommandData.ABOUT_US, null);
    }
}
