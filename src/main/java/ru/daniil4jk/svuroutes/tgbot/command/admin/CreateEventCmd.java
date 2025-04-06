package ru.daniil4jk.svuroutes.tgbot.command.admin;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter.SimpleExecuter;
import ru.daniil4jk.svuroutes.tgbot.command.assets.ProtectedBotCommand;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.Route;
import ru.daniil4jk.svuroutes.tgbot.db.entity.EventEntity;
import ru.daniil4jk.svuroutes.tgbot.expected.DefaultExpectedEvent;
import ru.daniil4jk.svuroutes.tgbot.expected.ExpectedEvent;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.BooleanInlineKeyboard;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.EventsKeyboard;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.RoutesListKeyboard;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

@Slf4j
@Component
public class CreateEventCmd extends ProtectedBotCommand {
    @Autowired
    private RoutesListKeyboard routesKeyboard;
    @Autowired
    private EventsKeyboard eventsKeyboard;

    public CreateEventCmd() {
        super("createevent", "create new event by route id");
    }

    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public CreateEventCmd(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    private static final String CANCEL_TRIGGER = "Отменить";
    private static final String CANCEL_MESSAGE = "Создание ивента успешно отменено";

    @Override
    protected void protectedExecute(AbsSender absSender, long chatId, String[] strings) {
        Route route = Objects.requireNonNull(getRouteMap().get(Long.parseLong(strings[0])));
        EventEntity event = new EventEntity();
        event.setRouteId(route.getId());
        Calendar c = GregorianCalendar.getInstance();
        getMessageService().addExpectedEvent(chatId,
            getMonth(c, chatId,
                getDay(c, chatId,
                    getHour(c, chatId,
                        getMinute(c, chatId,
                            getGuideName(event, chatId,
                                getMaxUsers(event, chatId,
                                        confirmEvent((SimpleExecuter) absSender, event, c, chatId))))))));
    }

    private ExpectedEvent<Message> getMonth(Calendar c, long chatId, ExpectedEvent<Message> next) {
        String[] monthNames =
                new String[]{"январь", "февраль", "март",
                        "апрель", "май", "июнь",
                        "июль", "август", "сентябрь",
                        "октябрь", "ноябрь", "декабрь"};

        String notification = "Введите название месяца, в котором будет проходить событие";
        return new DefaultExpectedEvent<>(notification,
                m -> {
                    int monthNumber = ArrayUtils.indexOf(monthNames, m.getText().toLowerCase());
                    if (monthNumber == -1) throw new IllegalArgumentException(
                            "Введите название месяца, например \"октябрь\"");
                    c.set(Calendar.MONTH, monthNumber);
                    getMessageService().addExpectedEvent(chatId, next);
                },
                CANCEL_TRIGGER,
                CANCEL_MESSAGE,
                chatId);
    }

    private static class GetCalendarNumberEvent extends DefaultExpectedEvent<Message>{
        public GetCalendarNumberEvent(String notification, Calendar c, long chatId,
                                      int calendarConst, int maxValue, Runnable addNext) {
            super(notification,
                    m -> {
                        try {
                            int value = Integer.parseInt(m.getText());
                            if (value < 0) throw new IllegalArgumentException(
                                    "Значение этого параметра не может быть меньше 0");
                            if (value > maxValue) throw new IllegalArgumentException(
                                    "Значение этого параметра не должно быть больше " + maxValue);
                            c.set(calendarConst, value);
                            addNext.run();
                        } catch (NumberFormatException e) {
                            throw new NumberFormatException("Введите только число, без лишних букв и символов");
                        }
                    },
            CANCEL_TRIGGER,
            CANCEL_MESSAGE,
            chatId);
        }
    }

    private ExpectedEvent<Message> getDay(Calendar c, long chatId, ExpectedEvent<Message> next) {
        String notification = "Введите день, в который будет проходить событие";
        return new GetCalendarNumberEvent(notification, c, chatId,
                Calendar.DAY_OF_MONTH, 31,
                () -> getMessageService().addExpectedEvent(chatId, next));
    }

    private ExpectedEvent<Message> getHour(Calendar c, long chatId, ExpectedEvent<Message> next) {
        String notification = "Введите, в котором часу будет проходить событие, в 24-часовом формате";
        return new GetCalendarNumberEvent(notification, c, chatId,
                Calendar.HOUR_OF_DAY, 24,
                () -> getMessageService().addExpectedEvent(chatId, next));
    }

    private ExpectedEvent<Message> getMinute(Calendar c, long chatId, ExpectedEvent<Message> next) {
        String notification = "Введите, на какой минуте часа событие начнется";
        return new GetCalendarNumberEvent(notification, c, chatId,
                Calendar.MINUTE, 60,
                () -> getMessageService().addExpectedEvent(chatId, next));
    }

    private ExpectedEvent<Message> getGuideName(EventEntity event, long chatId, ExpectedEvent<Message> next) {
        String notification = "Введите ФИО экскурсовода";
        return new DefaultExpectedEvent<>(notification,
                m -> {
                    event.setGuideName(m.getText());
                    getMessageService().addExpectedEvent(chatId, next);
                },
                CANCEL_TRIGGER,
                CANCEL_MESSAGE,
                chatId);
    }

    private ExpectedEvent<Message> getMaxUsers(EventEntity event, long chatId, ExpectedEvent<CallbackQuery> next) {
        String notification = "Введите максимальное количество посетителей на экскурсии";
        return new DefaultExpectedEvent<>(notification,
                m -> {
                    try {
                        event.setMaxUsers(Integer.parseInt(m.getText().trim()));
                    } catch (NumberFormatException e) {
                        throw new NumberFormatException("Введено не число, либо по каким-то причинам мы " +
                                "не можем прочитать это число. Проверьте чтобы между цифрами не было пробелов");
                    }
                    getQueryService().addExpectedEvent(chatId, next);
                },
                CANCEL_TRIGGER,
                CANCEL_MESSAGE,
                chatId);
    }

    private ExpectedEvent<CallbackQuery> confirmEvent(SimpleExecuter executer, EventEntity event,
                                                      Calendar c, long chatId) {
        event.setDate(c.getTime());

        String notification = "Введите максимальное количество посетителей на экскурсии";
        return new DefaultExpectedEvent<>(notification,
                q -> {
                    if (!BooleanInlineKeyboard.Data.TRUE.equals(q.getData())) {
                        throw new IllegalArgumentException("Вы создаете событие. Выберите \"Все так\" или \"отменить\"");
                    }

                    EventEntity newEntity = getEventService().save(event);
                    executer.sendSimpleTextMessage(
                            "Событие успешно добавлено", chatId);
                    eventsKeyboard.add(newEntity);
                },
                BooleanInlineKeyboard.Data.FALSE,
                "Добавление события успешно отменено",
                chatId);
    }
}