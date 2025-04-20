package ru.daniil4jk.svuroutes.tgbot.command.admin;

import org.springframework.stereotype.Component;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.command.assets.StaticCommand;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.AdminPanelKeyboard;

@Component
public class AdminPanelCmd extends StaticCommand {

    public AdminPanelCmd(AdminPanelKeyboard keyboard) {
        super("adminpanel", "admin commands", CommandTag.ADMIN_PANEL, keyboard);
        setOnlyAdminAccess(true);
    }
}
