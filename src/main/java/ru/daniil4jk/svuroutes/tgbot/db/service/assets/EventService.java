package ru.daniil4jk.svuroutes.tgbot.db.service.assets;

import ru.daniil4jk.svuroutes.tgbot.db.entity.EventEntity;

import java.util.Date;
import java.util.Set;

public interface EventService extends TService<EventEntity> {
    EventEntity createNew(long routeId, Date end, String tourGuide, int maxUsers);
    Set<EventEntity> getActual();
}
