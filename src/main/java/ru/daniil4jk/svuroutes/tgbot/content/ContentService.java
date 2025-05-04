package ru.daniil4jk.svuroutes.tgbot.content;

public interface ContentService<K, V> {
    V get(K k);
}
