package ru.daniil4jk.svuroutes.tgbot.command.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter.SimpleExecuter;
import ru.daniil4jk.svuroutes.tgbot.command.assets.ProtectedBotCommand;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.UserService;

@Slf4j
@Component
public class RebootCmd extends ProtectedBotCommand {
    @Autowired
    private ConfigurableApplicationContext context;
    @Autowired
    private UserService userService;

    public RebootCmd() {
        super("reboot", "reboot application", null);
    }

    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public RebootCmd(String commandIdentifier, String description) {
        super(commandIdentifier, description, null);
    }

    @Override
    protected void protectedExecute(AbsSender absSender, long chatId, String[] strings) {
        ((SimpleExecuter) absSender).nonExceptionExecute(SendMessage.builder()
                .text("Выполняю остановку бота...")
                .chatId(chatId)
                .build());
        try {
            log.info("Остановка бота вызвана пользователем {}", userService.get(chatId).getUsername());
        } catch (Exception e) {
            log.info("Остановка бота вызвана пользователем с chatId: {}", chatId);
        }
        SpringApplication.exit(context);
    }
}
