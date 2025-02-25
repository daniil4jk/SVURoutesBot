package ru.daniil4jk.svuroutes.tgbot.keyboard.inline;

import org.springframework.stereotype.Component;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.db.entity.EventEntity;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.EventService;
import ru.daniil4jk.svuroutes.tgbot.keyboard.KeyboardConfig;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.assets.TListKeyboard;

@Component
public class EventsKeyboard extends TListKeyboard<EventEntity> {
    public EventsKeyboard(EventService service, KeyboardConfig config) {
        super(service.getActual(), CommandData.EVENT, config);
    }

    protected String getName(EventEntity event) {
        return event.getName() + "#" + event.getId();
    }

    @Override
    protected Long getId(EventEntity event) {
        return event.getId();
    }
}
