package ru.daniil4jk.svuroutes.tgbot.command.assets;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.daniil4jk.svuroutes.tgbot.bot.Bot;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.UserService;

@Setter
public abstract class ProtectedBotCommand extends TaggedCommand {
    @Autowired
    private UserService service;

    private boolean onlyAdminAccess;
    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public ProtectedBotCommand(String commandIdentifier, String description, CommandTag tag) {
        super(commandIdentifier, description, tag);
    }

    @Override
    public final void execute(AbsSender absSender, long chatId, String[] strings) {
        if (!onlyAdminAccess || isAdmin(chatId)) protectedExecute(absSender, chatId, strings);
        else if (absSender instanceof Bot)
            ((Bot) absSender).weDontKnowWhatThisIs(chatId);
    }

    abstract protected void protectedExecute(AbsSender absSender, long chatId, String[] strings);

    public boolean isAdmin(long chatId) {
        try {
            return service.get(chatId).isAdmin();
        } catch (Exception e) {
            return false;
        }
    }
}
