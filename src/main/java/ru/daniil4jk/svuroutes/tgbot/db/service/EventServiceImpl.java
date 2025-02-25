package ru.daniil4jk.svuroutes.tgbot.db.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.Route;
import ru.daniil4jk.svuroutes.tgbot.db.entity.EventEntity;
import ru.daniil4jk.svuroutes.tgbot.db.repository.EventRepository;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.EventService;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {
    private static final Predicate<EventEntity> notRemoved =
            event -> !event.isRemoved();
    @Autowired
    private Map<Long, Route> routes;
    @Autowired
    private EventRepository repository;

    @Override
    public EventEntity get(long id) {
        return repository.findById(id).filter(notRemoved).orElseThrow();
    }

    @Override
    public EventEntity get(EventEntity eventEntity) {
        return get(eventEntity.getId());
    }

    @Override
    public boolean contains(long id) {
        return repository.existsByIdAndRemoved(id, false);
    }

    @Override
    public boolean contains(EventEntity eventEntity) {
        return contains(eventEntity.getId());
    }

    @Override
    public EventEntity createNew(long routeId, Date end) {
        return save(new EventEntity(null, routes.get(routeId).getName(),
                new ArrayList<>(), end, routeId, false));
    }

    @Override
    public EventEntity save(EventEntity eventEntity) {
        return repository.save(eventEntity);
    }

    @Override
    public void remove(long id) {
        update(id, e -> e.setRemoved(true));
    }

    @Override
    public void remove(EventEntity eventEntity) {
        remove(eventEntity.getId());
    }

    @Override
    public void update(long id, Consumer<EventEntity> updates) {
        var entity = get(id);
        updates.accept(entity);
        save(entity);
    }

    @Override
    public void update(EventEntity eventEntity, Consumer<EventEntity> updates) {
        update(eventEntity.getId(), updates);
    }

    public Set<EventEntity> getActual() {
        return repository.findByDateAfter(new Date()); //TODO replace by async updating date
    }
}
