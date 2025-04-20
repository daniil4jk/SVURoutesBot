package ru.daniil4jk.svuroutes.tgbot.command.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.command.assets.StaticCommand;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.DotsListKeyboard;

@Slf4j
@Component
public class DotsCmd extends StaticCommand {
    public DotsCmd(DotsListKeyboard keyboard) {
        super("dots", "dots list",
                CommandTag.DOTS, keyboard);
    }
}
