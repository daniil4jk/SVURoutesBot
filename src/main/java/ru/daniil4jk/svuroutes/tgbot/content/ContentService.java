package ru.daniil4jk.svuroutes.tgbot.content;

import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;

public interface ContentService<K, V> {
    V get(K k);
}
