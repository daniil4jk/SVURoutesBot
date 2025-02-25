package ru.daniil4jk.svuroutes.tgbot.expected.handlers.assets;

import java.util.function.Consumer;

public interface ExpectedHandler<T> extends Consumer<T> {
    boolean hasEvent(T t);
    boolean hasEvent(long chatId);
}
