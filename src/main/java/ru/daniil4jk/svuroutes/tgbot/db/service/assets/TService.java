package ru.daniil4jk.svuroutes.tgbot.db.service.assets;

import java.util.function.Consumer;

public interface TService<T> {
    T save(T t);
    T get(long id);
    T get(T t);
    boolean contains(long id);
    boolean contains(T t);
    void update(long id, Consumer<T> updates);
    void update(T t, Consumer<T> updates);
    void remove(long id);
    void remove(T t);
}
