package ru.daniil4jk.svuroutes.tgbot.command.admin;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter.SimpleExecuter;
import ru.daniil4jk.svuroutes.tgbot.command.assets.ProtectedBotCommand;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.Route;
import ru.daniil4jk.svuroutes.tgbot.db.entity.EventEntity;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.EventService;
import ru.daniil4jk.svuroutes.tgbot.expected.ExpectedEvent;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.BooleanInlineKeyboard;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.EventsKeyboard;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.RoutesListKeyboard;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Slf4j
@Component
public class CreateEventCmd extends ProtectedBotCommand {
    @Autowired
    private RoutesListKeyboard routesKeyboard;
    @Autowired
    private EventsKeyboard eventsKeyboard;

    public CreateEventCmd() {
        super("createbynum", "create new event by route id");
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

    @Override
    public void protectedExecute(AbsSender absSender, long chatId, String[] strings) {
        try {
            Route route = Objects.requireNonNull(getRouteMap().get(Long.parseLong(strings[0])));
            Calendar c = GregorianCalendar.getInstance();
            AtomicLong aChatId = new AtomicLong(chatId);
            getMessageService().addExpectedEvent(aChatId.get(),
                    new GetMonthInput(aChatId, c, "Введите название месяца, " +
                            "в котором будет проходить событие",
            () -> getMessageService().addExpectedEvent(aChatId.get(),
                    new GetNumberEvent(aChatId, Calendar.DAY_OF_MONTH, c, 31,
                            "Введите день, в который будет проходить событие",
            () -> getMessageService().addExpectedEvent(aChatId.get(),
                    new GetNumberEvent(aChatId, Calendar.HOUR_OF_DAY, c, 24,
                            "Введите, в котором часу будет проходить событие, в 24-часовом формате",
            () -> getMessageService().addExpectedEvent(aChatId.get(),
                    new GetNumberEvent(aChatId, Calendar.MINUTE, c, 60,
                            "Введите, на какой минуте часа событие начнется",
            () -> getQueryService().addExpectedEvent(aChatId.get(),
                    new ConfirmEvent(aChatId,(SimpleExecuter) absSender
                            , getEventService(), route, c, eventsKeyboard))))))))));
        } catch (NumberFormatException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }


    private static class GetSomethingEvent extends ExpectedEvent<Message> {

        public GetSomethingEvent(AtomicLong chatId, Consumer<String> setter,
                                 String notification, Runnable addNext) {
            super(m -> {
                if (m.getChatId() != chatId.get()) chatId.set(m.getChatId());
                setter.accept(m.getText());
                addNext.run();
            });
            var getDateMessage = SendMessage.builder().text(notification).chatId(chatId.get()).build();
            firstNotification(getDateMessage);
            notification(getDateMessage);
            onException(e -> SendMessage.builder()
                    .text(e.getLocalizedMessage()).chatId(chatId.get()).build());
            removeOnException(false);
        }
    }

    private static class GetNumberEvent extends GetSomethingEvent {
        public GetNumberEvent(AtomicLong chatId, int calendarConst, Calendar c,
                              int maxValue, String notification, Runnable addNext) {
            super(chatId, t -> {
                try {
                    int value = Integer.parseInt(t);
                    if (value < 0) throw new IllegalArgumentException(
                            "Значение этого параметра не может быть меньше 0");
                    if (value > maxValue) throw new IllegalArgumentException(
                            "Значение этого параметра не должно быть больше " + maxValue);
                    c.set(calendarConst, value);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Введите только число, без лишних букв и символов");
                }
            }, notification, addNext);
        }
    }

    private static class GetMonthInput extends GetSomethingEvent {
        private static final String[] monthNames =
                new String[]{"январь", "февраль", "март",
                        "апрель", "май", "июнь",
                        "июль", "август", "сентябрь",
                        "октябрь", "ноябрь", "декабрь"};

        public GetMonthInput(AtomicLong chatId, Calendar c,
                             String notification, Runnable addNext) {
            super(chatId, s -> {
                int monthNumber = ArrayUtils.indexOf(monthNames, s.toLowerCase());
                if (monthNumber == -1) throw new IllegalArgumentException(
                        "Введите название месяца, например \"октябрь\"");
                c.set(Calendar.MONTH, monthNumber);
            }, notification, addNext);
        }
    }

    private static class ConfirmEvent extends ExpectedEvent<CallbackQuery> {
        private static final String[] monthNames =
                new String[]{"январе", "феврале", "марте",
                        "апреле", "мае", "июне",
                        "июле", "августе", "сентябре",
                        "октябре", "ноябре", "декабре"};

        public ConfirmEvent(AtomicLong chatId, SimpleExecuter executer,
                            EventService service, Route route, Calendar calendar,
                            EventsKeyboard keyboardToUpdate) {

            super(q -> {
                if (String.valueOf(true).equals(q.getData())) {

                    EventEntity newEntity = service.createNew(route.getId(), calendar.getTime(),
                            null, 1);
                    executer.sendSimpleTextMessage(
                            "Событие успешно добавлено", chatId.get());
                    keyboardToUpdate.add(newEntity);

                } else if (String.valueOf(false).equals(q.getData())) {
                    executer.sendSimpleTextMessage(
                            "Добавление события успешно отменено", chatId.get());
                } else {
                    throw new IllegalArgumentException("Выберите \"Все так\" или \"отменить\"");
                }
            });

            var notification = SendMessage.builder()
                    .text(String.format("""
                                    Вы хотите создать событие с:
                                    Маршрутом - %s
                                    В %s
                                    %d числа
                                    В %d часов, %d минут
                                    """, route.getName(),
                            monthNames[calendar.get(Calendar.MONTH)],
                            calendar.get(Calendar.DAY_OF_MONTH),
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE)))
                    .chatId(chatId.get())
                    .replyMarkup(new BooleanInlineKeyboard("Все так", "Отменить"))
                    .build();
            firstNotification(notification);
            notification(notification);
            onException(e -> SendMessage.builder()
                    .text(e.getLocalizedMessage()).chatId(chatId.get()).build());
            removeOnException(false);
        }
    }
}