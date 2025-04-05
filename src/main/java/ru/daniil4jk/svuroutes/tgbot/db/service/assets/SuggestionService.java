package ru.daniil4jk.svuroutes.tgbot.db.service.assets;

import ru.daniil4jk.svuroutes.tgbot.db.entity.EventEntity;
import ru.daniil4jk.svuroutes.tgbot.db.entity.SuggestionEntity;

import java.util.Date;

public interface SuggestionService extends TService<SuggestionEntity> {
    SuggestionEntity createNew(long chatId, String text);
}
