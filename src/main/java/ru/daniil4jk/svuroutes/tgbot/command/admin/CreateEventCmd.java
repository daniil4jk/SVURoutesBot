package ru.daniil4jk.svuroutes.tgbot.command.admin;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.weaver.ast.Call;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter.SimpleExecuter;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.command.assets.ProtectedBotCommand;
import ru.daniil4jk.svuroutes.tgbot.command.assets.StaticCommand;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.Dot;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.Route;
import ru.daniil4jk.svuroutes.tgbot.db.entity.EventEntity;
import ru.daniil4jk.svuroutes.tgbot.expected.DefaultExpectedEvent;
import ru.daniil4jk.svuroutes.tgbot.expected.ExpectedEvent;
import ru.daniil4jk.svuroutes.tgbot.keyboard.KeyboardConfig;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.BooleanInlineKeyboard;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.DotsListKeyboard;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.EventsKeyboard;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.RoutesListKeyboard;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.assets.TListKeyboard;

import java.util.*;
import java.util.function.Supplier;

@Slf4j
@Component
public class CreateEventCmd extends StaticCommand {
    @Autowired
    private RoutesListKeyboardWithoutCmdIndex routesKeyboard;
    @Autowired
    private EventsKeyboard eventsKeyboard;
    @Autowired
    private Map<Route, DotsListKeyboardWithoutCmdIndex> routeDotsListKeyboard;

    public CreateEventCmd() {
        super("createevent", "create new event by route id",
                CommandData.ADMIN_CREATE_EVENT, null);
        setOnlyAdminAccess(true);
    }

    private static final String CANCEL_TRIGGER = "Отменить";
    private static final String CANCEL_MESSAGE = "Создание ивента успешно отменено";

    @Override
    public void protectedExecute(AbsSender absSender, long chatId, String[] strings) {
        EventEntity event = new EventEntity();
        Calendar c = GregorianCalendar.getInstance();
        getQueryService().addExpectedEvent(chatId,
            getRoute(event, chatId,
                () -> getDot(event, chatId,
                    () -> getMonth(c, chatId,
                        () -> getDay(c, chatId,
                            () -> getHour(c, chatId,
                                () -> getMinute(c, chatId,
                                    () -> getGuideName(event, chatId,
                                        () -> getMaxUsers(event, chatId,
                                            () -> confirmEvent((SimpleExecuter) absSender, event, c, chatId))))))))));
    }

    private ExpectedEvent<CallbackQuery> getRoute(EventEntity event, long chatId,
                                                  Supplier<ExpectedEvent<CallbackQuery>> next) {
        SendMessage notification = SendMessage.builder()
                .text("Выберите маршрут")
                .replyMarkup(routesKeyboard)
                .chatId(chatId)
                .build();

        return new DefaultExpectedEvent<>(notification,
                q -> {
                    try {
                        event.setRouteId(
                                Objects.requireNonNull(
                                        getRouteMap().get(Long.parseLong(q.getData()))
                                ).getId()
                        );
                        getQueryService().addExpectedEvent(chatId, next.get());
                    } catch (NumberFormatException | NullPointerException e) {
                        throw new NullPointerException("Выбран не существующий маршрут");
                    }
                },
                chatId);
    }

    private ExpectedEvent<CallbackQuery> getDot(EventEntity event, long chatId,
                                                Supplier<ExpectedEvent<Message>> next) {
        SendMessage notification = SendMessage.builder()
                .text("Выберите Точку")
                .replyMarkup(
                        Objects.requireNonNull(
                            routeDotsListKeyboard.get(
                                    getRouteMap().get(
                                            event.getRouteId()
                                    )
                            )
                        )
                )
                .chatId(chatId)
                .build();

        return new DefaultExpectedEvent<>(notification,
                q -> {
                    try {
                        event.setDotId(
                                Objects.requireNonNull(
                                        getDotMap().get(Long.parseLong(q.getData()))
                                ).getId()
                        );
                        getMessageService().addExpectedEvent(chatId, next.get());
                    } catch (NumberFormatException | NullPointerException e) {
                        throw new NullPointerException("Выбрана не существующая точка");
                    }
                },
                chatId);
    }

    private static final String[] monthNames =
            new String[]{"январь", "февраль", "март",
                    "апрель", "май", "июнь",
                    "июль", "август", "сентябрь",
                    "октябрь", "ноябрь", "декабрь"};

    private ExpectedEvent<Message> getMonth(Calendar c, long chatId,
                                            Supplier<ExpectedEvent<Message>> next) {
        String notification = "Введите название месяца, в котором будет проходить событие";
        return new DefaultExpectedEvent<>(notification,
                m -> {
                    int monthNumber = ArrayUtils.indexOf(monthNames, m.getText().toLowerCase());
                    if (monthNumber == -1) throw new IllegalArgumentException(
                            "Введите название месяца, например \"октябрь\"");
                    c.set(Calendar.MONTH, monthNumber);
                    getMessageService().addExpectedEvent(chatId, next.get());
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

    private ExpectedEvent<Message> getDay(Calendar c, long chatId,
                                          Supplier<ExpectedEvent<Message>> next) {
        String notification = "Введите день, в который будет проходить событие";
        return new GetCalendarNumberEvent(notification, c, chatId,
                Calendar.DAY_OF_MONTH, 31,
                () -> getMessageService().addExpectedEvent(chatId, next.get()));
    }

    private ExpectedEvent<Message> getHour(Calendar c, long chatId,
                                           Supplier<ExpectedEvent<Message>> next) {
        String notification = "Введите, в котором часу будет проходить событие, в 24-часовом формате";
        return new GetCalendarNumberEvent(notification, c, chatId,
                Calendar.HOUR_OF_DAY, 24,
                () -> getMessageService().addExpectedEvent(chatId, next.get()));
    }

    private ExpectedEvent<Message> getMinute(Calendar c, long chatId,
                                             Supplier<ExpectedEvent<Message>> next) {
        String notification = "Введите, на какой минуте часа событие начнется";
        return new GetCalendarNumberEvent(notification, c, chatId,
                Calendar.MINUTE, 60,
                () -> getMessageService().addExpectedEvent(chatId, next.get()));
    }

    private ExpectedEvent<Message> getGuideName(EventEntity event, long chatId,
                                                Supplier<ExpectedEvent<Message>> next) {
        String notification = "Введите ФИО экскурсовода";
        return new DefaultExpectedEvent<>(notification,
                m -> {
                    event.setGuideName(m.getText());
                    getMessageService().addExpectedEvent(chatId, next.get());
                },
                CANCEL_TRIGGER,
                CANCEL_MESSAGE,
                chatId);
    }

    private ExpectedEvent<Message> getMaxUsers(EventEntity event, long chatId,
                                               Supplier<ExpectedEvent<CallbackQuery>> next) {
        String notification = "Введите максимальное количество посетителей на экскурсии";
        return new DefaultExpectedEvent<>(notification,
                m -> {
                    try {
                        event.setMaxUsers(Integer.parseInt(m.getText().trim()));
                    } catch (NumberFormatException e) {
                        throw new NumberFormatException("Введено не число, либо по каким-то причинам мы " +
                                "не можем прочитать это число. Проверьте чтобы между цифрами не было пробелов");
                    }
                    getQueryService().addExpectedEvent(chatId, next.get());
                },
                CANCEL_TRIGGER,
                CANCEL_MESSAGE,
                chatId);
    }

    private static final String[] monthNames2 =
            new String[]{"января", "февраля", "марта",
                    "апреля", "мая", "июня",
                    "июля", "августа", "сентября",
                    "октября", "ноября", "декабря"};

    private ExpectedEvent<CallbackQuery> confirmEvent(SimpleExecuter executer, EventEntity event,
                                                      Calendar c, long chatId) {
         event.setDate(c.getTime());
         SendMessage notification = SendMessage.builder()
                 .text(String.format("""
                         Проверьте все ли верно, и подтвердите создание экскурсии
                         Экскурсия пройдет %d %s в %d:%d
                         Название маршрута: %s
                         Название точки: %s
                         Имя экскурсовода: %s
                         """,
                         c.get(Calendar.DAY_OF_MONTH),
                         monthNames2[c.get(Calendar.MONTH)],
                         c.get(Calendar.HOUR_OF_DAY),
                         c.get(Calendar.MINUTE),
                         getRouteMap().get(event.getRouteId()).getName(),
                         getDotMap().get(event.getDotId()).getName(),
                         event.getGuideName()
                         ))
                 .replyMarkup(new BooleanInlineKeyboard("Все верно", "Отменить"))
                 .chatId(chatId)
                 .build();

         return new ExpectedEvent<CallbackQuery>(q -> {
             if (!BooleanInlineKeyboard.Data.TRUE.equals(q.getData())) {
                 throw new IllegalArgumentException("Вы создаете событие. Выберите \"Все верно\" или \"отменить\"");
             }

             EventEntity newEntity = getEventService().save(event);
             executer.sendSimpleTextMessage(
                     "Событие успешно добавлено", chatId);
             eventsKeyboard.add(newEntity);
        })
        .notification(notification)
        .firstNotification(notification)
        .onException(e -> SendMessage.builder()
            .text(e.getLocalizedMessage())
            .chatId(chatId)
            .build())
        .removeOnException(false)
        .cancelTrigger(BooleanInlineKeyboard.Data.FALSE)
        .cancelText("Добавление события отменено");
    }
}

@Component
class RoutesListKeyboardWithoutCmdIndex extends TListKeyboard<Route> {
    public RoutesListKeyboardWithoutCmdIndex(Collection<Route> routes, KeyboardConfig config) {
        super(routes.stream().sorted(Comparator.comparingLong(Route::getId)).toList(), config);
    }

    @Override
    protected String getName(Route route) {
        return route.getName();
    }

    @Override
    protected Long getId(Route route) {
        return route.getId();
    }
}

@Component
class DotsListKeyboardWithoutCmdIndex extends TListKeyboard<Dot> {
    public DotsListKeyboardWithoutCmdIndex(Collection<Dot> dots, KeyboardConfig config) {
        super(dots, config);
    }

    @Override
    protected String getName(Dot dot) {
        return dot.getName();
    }

    @Override
    protected Long getId(Dot dot) {
        return dot.getId();
    }
}

@Configuration
class RouteDotsListKeyboardsWithoutCmdIndex {
    @Bean
    public Map<Route, DotsListKeyboardWithoutCmdIndex> routeDotsListKeyboardWithoutCmdIndex(Collection<Route> routes, KeyboardConfig config) {
        Map<Route, DotsListKeyboardWithoutCmdIndex> result = new HashMap<>();
        for (Route route : routes) {
            result.put(route, new DotsListKeyboardWithoutCmdIndex(route.getDots(), config));
        }
        return result;
    }
}
