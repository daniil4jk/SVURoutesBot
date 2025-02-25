package ru.daniil4jk.svuroutes.tgbot.command.assets;

import lombok.Setter;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.daniil4jk.svuroutes.tgbot.bot.Bot;

@Setter
public abstract class ProtectedBotCommand extends ServiceIntegratedBotCommand {
    private boolean onlyAdminAccess;
    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public ProtectedBotCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
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
            return getUserService().get(chatId).isAdmin();
        } catch (Exception e) {
            return false;
        }
    }
}
