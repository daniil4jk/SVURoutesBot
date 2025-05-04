package ru.daniil4jk.svuroutes.tgbot.command.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter.SimpleExecuter;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.command.assets.SimpleBotCommand;
import ru.daniil4jk.svuroutes.tgbot.content.CommandMessageService;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.EventService;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.UserService;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.EventKeyboard;

import java.util.NoSuchElementException;

@Slf4j
@Component
public class EventCmd extends SimpleBotCommand {
    @Autowired
    private CommandMessageService commandMessageService;
    @Autowired
    private UserService userService;
    @Autowired
    private EventService eventService;

    public EventCmd() {
        super("event", "show information by current event", CommandTag.EVENT);
    }

    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public EventCmd(String commandIdentifier, String description) {
        super(commandIdentifier, description, CommandTag.EVENT);
    }

    @Override
    public void execute(AbsSender absSender, long chatId, String[] strings) {
        try {
            var event = eventService.get(Long.parseLong(strings[0]));
            var timeToEvent = event.getTimeBefore();
            var user = userService.get(chatId);
            absSender.execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(String.format(commandMessageService.get(CommandTag.EVENT)
                            .getText(), event.getName()) +
                            String.format("""
                            До экскурсии осталось
                            %d дней
                            %d часов
                            %d минут
                            """, timeToEvent.getDays(), timeToEvent.getHours(), timeToEvent.getMins()))
                    .replyMarkup(new EventKeyboard(event, user))
                    .build());
        } catch (TelegramApiException e) {
            log.error(e.getLocalizedMessage(), e);
        } catch (NoSuchElementException e) {
            SimpleExecuter executer = (SimpleExecuter) absSender;
            executer.sendSimpleTextMessage("Экскурсии с таким id не существует", chatId);
        }
    }
}
