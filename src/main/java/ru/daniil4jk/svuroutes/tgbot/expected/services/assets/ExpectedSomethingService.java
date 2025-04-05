package ru.daniil4jk.svuroutes.tgbot.expected.services.assets;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.daniil4jk.svuroutes.tgbot.bot.Bot;
import ru.daniil4jk.svuroutes.tgbot.expected.ExpectedEvent;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
public abstract class ExpectedSomethingService<T> {
    private final Map<Long, Deque<ExpectedEvent<T>>> map = new ConcurrentHashMap<>();
    @Autowired
    private Bot bot;

    public void addExpectedEvent(long id, @NotNull ExpectedEvent<T> event) {
        var stack = map.computeIfAbsent(id, key -> new LinkedBlockingDeque<>());
        sendFirstNotification(event);
        stack.addLast(event);
    }

    private void sendFirstNotification(@NotNull ExpectedEvent<T> event) {
        if (event.getFirstNotification() != null) {
            try {
                bot.execute(event.getNotification());
            } catch (TelegramApiException e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    public ExpectedEvent<T> getLastExpectedEvent(long id) {
        var deque = map.get(id);
        return deque.peekLast();
    }

    public boolean hasExpectedEvent(long id) {
        return map.containsKey(id) && !map.get(id).isEmpty();
    }

    public synchronized void removeEvent(long id, @NotNull ExpectedEvent<T> toRemove) {
        var stack = map.get(id);
        if (stack != null) {
            stack.removeIf(toRemove::equals);
            if (stack.isEmpty()) map.remove(id);
        }
    }

    public void removeEvents(long id) {
        var stack = map.get(id);
        stack.clear();
        map.remove(id);
    }
}
