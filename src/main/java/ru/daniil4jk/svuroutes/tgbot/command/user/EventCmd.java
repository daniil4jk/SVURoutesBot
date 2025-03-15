package ru.daniil4jk.svuroutes.tgbot.command.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter.SimpleExecuter;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.command.assets.ServiceIntegratedBotCommand;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.EventKeyboard;

import java.util.Date;
import java.util.NoSuchElementException;

@Slf4j
@Component
public class EventCmd extends ServiceIntegratedBotCommand {
    public EventCmd() {
        super("event", "show information by current event");
    }

    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public EventCmd(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    @Override
    public void execute(AbsSender absSender, long chatId, String[] strings) {
        try {
            var event = getEventService().get(Long.parseLong(strings[0]));
            var timeToEvent = getTime(event.getDate());
            var user = getUserService().get(chatId);
            absSender.execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(String.format(getMessageMap().get(CommandData.EVENT)
                            .getText(), event.getName()) +
                            String.format("""
                            До экскурсии осталось
                            %d дней
                            %d часов
                            %d минут
                            """, timeToEvent[0], timeToEvent[1], timeToEvent[2]))
                    .replyMarkup(new EventKeyboard(event, user))
                    .build());
        } catch (TelegramApiException e) {
            log.error(e.getLocalizedMessage(), e);
        } catch (NoSuchElementException e) {
            SimpleExecuter executer = (SimpleExecuter) absSender;
            executer.sendSimpleTextMessage("Экскурсии с таким id не существует", chatId);
        }
    }

    private static final long millsInDay = 1000 * 60 * 60 * 24;
    private static final long millsInHour = 1000 * 60 * 60;
    private static final long millsInMin = 1000 * 60;

    private int[] getTime (Date date) {
        long eventTime = date.getTime();
        long currentTime = System.currentTimeMillis();
        long difference = eventTime - currentTime;

        int days = Math.toIntExact(Math.floorDiv(difference, millsInDay));
        difference -= days * millsInDay;
        int hours = Math.toIntExact(Math.floorDiv(difference, millsInHour));
        difference -= hours * millsInHour;
        int mins = Math.toIntExact(Math.floorDiv(difference, millsInMin));

        return new int[]{days, hours, mins};
    }
}
