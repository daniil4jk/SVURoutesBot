package ru.daniil4jk.svuroutes.tgbot.command.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter.SimpleExecuter;
import ru.daniil4jk.svuroutes.tgbot.command.assets.ProtectedBotCommand;
import ru.daniil4jk.svuroutes.tgbot.keyboard.reply.AdminKeyboard;

@Slf4j
@Component
public class SetAdminCmd extends ProtectedBotCommand {
    @Autowired
    private AdminKeyboard adminKeyboard;

    public SetAdminCmd() {
        super("setadmin", "set user as administrator");
        setOnlyAdminAccess(true);
    }

    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public SetAdminCmd(String commandIdentifier, String description) {
        super(commandIdentifier, description);
        setOnlyAdminAccess(true);
    }

    @Override
    public void protectedExecute(AbsSender absSender, long chatId, String[] strings) {
        var executer = (SimpleExecuter) absSender;
        String username = strings[0];
        username = username.replace("@", "");
        var user = getUserService().getByUsername(username);
        user.setAdmin(true);
        executer.sendSimpleTextMessage(
                String.format("Пользователь: %s теперь имеет права администратора!",
                        username), chatId);
        try {
            absSender.execute(SendMessage.builder()
                    .text("Вы теперь администратор")
                    .chatId(user.getId())
                    .replyMarkup(adminKeyboard)
                    .build());
        } catch (TelegramApiException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }
}
