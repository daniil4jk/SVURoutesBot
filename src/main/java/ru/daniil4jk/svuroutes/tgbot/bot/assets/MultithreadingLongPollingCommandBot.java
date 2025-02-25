//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ru.daniil4jk.svuroutes.tgbot.bot.assets;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.CommandRegistry;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Used default TelegramLongPollingCommandBot class code, with one change:
 * the getName of the method "onUpdateReceived" has been changed to "asyncUpdateHandle"
 */
public abstract class MultithreadingLongPollingCommandBot extends MultithreadingLongPollingBot implements CommandBot, ICommandRegistry {
    private final CommandRegistry commandRegistry;

    /** @deprecated */
    @Deprecated
    public MultithreadingLongPollingCommandBot() {
        this(new DefaultBotOptions());
    }

    /** @deprecated */
    @Deprecated
    public MultithreadingLongPollingCommandBot(DefaultBotOptions options) {
        this(options, true);
    }

    /** @deprecated */
    @Deprecated
    public MultithreadingLongPollingCommandBot(DefaultBotOptions options, boolean allowCommandsWithUsername) {
        super(options);
        this.commandRegistry = new CommandRegistry(allowCommandsWithUsername, this::getBotUsername);
    }

    public MultithreadingLongPollingCommandBot(String botToken) {
        this(new DefaultBotOptions(), botToken);
    }

    public MultithreadingLongPollingCommandBot(DefaultBotOptions options, String botToken) {
        this(options, true, botToken);
    }

    public MultithreadingLongPollingCommandBot(DefaultBotOptions options, boolean allowCommandsWithUsername, String botToken) {
        super(options, botToken);
        this.commandRegistry = new CommandRegistry(allowCommandsWithUsername, this::getBotUsername);
    }

    @Override
    public final void asyncUpdateHandle(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.isCommand() && !filter(message)) {
                if (!commandRegistry.executeCommand(this, message)) {
                    processInvalidCommandUpdate(update);
                }
                return;
            }
        }
        this.processNonCommandUpdate(update);
    }

    public final boolean register(IBotCommand botCommand) {
        return this.commandRegistry.register(botCommand);
    }

    public final Map<IBotCommand, Boolean> registerAll(IBotCommand... botCommands) {
        return this.commandRegistry.registerAll(botCommands);
    }

    public final boolean deregister(IBotCommand botCommand) {
        return this.commandRegistry.deregister(botCommand);
    }

    public final Map<IBotCommand, Boolean> deregisterAll(IBotCommand... botCommands) {
        return this.commandRegistry.deregisterAll(botCommands);
    }

    public final Collection<IBotCommand> getRegisteredCommands() {
        return this.commandRegistry.getRegisteredCommands();
    }

    public void registerDefaultAction(BiConsumer<AbsSender, Message> defaultConsumer) {
        this.commandRegistry.registerDefaultAction(defaultConsumer);
    }

    public final IBotCommand getRegisteredCommand(String commandIdentifier) {
        return this.commandRegistry.getRegisteredCommand(commandIdentifier);
    }

    public abstract String getBotUsername();
}
