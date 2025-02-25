package ru.daniil4jk.svuroutes.tgbot.command.assets;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import ru.daniil4jk.svuroutes.tgbot.bot.BotConfig;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.Dot;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.MessageEntry;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.Route;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.EventService;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.RequestService;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.SuggestionService;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.UserService;
import ru.daniil4jk.svuroutes.tgbot.expected.services.ExpectedCallbackQueryService;
import ru.daniil4jk.svuroutes.tgbot.expected.services.ExpectedMessageService;

import java.util.Map;

@Slf4j
@Getter
public abstract class ServiceIntegratedBotCommand extends SimpleBotCommand {
    @Autowired @Lazy private ExpectedMessageService messageService;
    @Autowired @Lazy private ExpectedCallbackQueryService queryService;
    @Autowired @Lazy private BotConfig botConfig;
    @Autowired @Lazy private Map<CommandData, MessageEntry> messageMap;
    @Autowired @Lazy private Map<Long, Route> routeMap;
    @Autowired @Lazy private Map<Long, Dot> dotMap;
    @Autowired @Lazy private EventService eventService;
    @Autowired @Lazy private RequestService requestService;
    @Autowired @Lazy private UserService userService;
    @Autowired @Lazy private SuggestionService suggestionService;

    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public ServiceIntegratedBotCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }
}

