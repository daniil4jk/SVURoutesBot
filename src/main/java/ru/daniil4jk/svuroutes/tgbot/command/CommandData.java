package ru.daniil4jk.svuroutes.tgbot.command;

import lombok.AllArgsConstructor;
import org.glassfish.grizzly.utils.ArrayUtils;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public enum CommandData {
    START("Старт"),
    ROUTES("Маршруты"),
    ROUTE("Маршрут"),
    DOTS("Все точки"),
    DOT("Точка"),
    ABOUT_US("О нас"),
    ABOUT_PROJECT("О проекте"),
    EVENTS("Ближайшие экскурсии"),
    EVENT("Экскурсия"),
    ADMIN_CREATE_EVENT("Создать экскурсию"),
    ADMIN_CREATE_EVENTS("Создание экскурсии"),
    REGISTER("Подать заявку"),
    REQUESTS("Мои заявки"),
    REQUEST("Заявка"),
    ADMIN_REQUESTS("Смотреть заявки"),
    ADMIN_REQUEST("Репрезентация заявки"),
    ADMIN_PANEL("Панель администратора"),
    ADD_SUGGESTION("Написать предложение/пожелание"),
    HELP("Помощь");

    private static final Map<String, CommandData> LOOKUP_STRING_MAP = new HashMap<>();
    private static final Map<Integer, CommandData> LOOKUP_INTEGER_MAP = new HashMap<>();

    static {
        for (CommandData d : values()) {
            LOOKUP_STRING_MAP.put(d.toString(), d);
        }
        CommandData[] values = values();
        for (int i = 0; i < values.length; i++) {
            LOOKUP_INTEGER_MAP.put(i, values[i]);
        }
    }

    private final String stringValue;

    public String toString() {
        return stringValue;
    }

    public int getId() {
        return ArrayUtils.indexOf(values(), this);
    }

    public static boolean contains(String constantValue) {
        return LOOKUP_STRING_MAP.containsKey(constantValue);
    }

    public static boolean contains(Integer constantValue) {
        return LOOKUP_INTEGER_MAP.containsKey(constantValue);
    }

    public static CommandData normalValueOf(String constantValue) {
        return LOOKUP_STRING_MAP.get(constantValue);
    }

    public static CommandData normalValueOf(Integer num) {
        return LOOKUP_INTEGER_MAP.get(num);
    }
}
