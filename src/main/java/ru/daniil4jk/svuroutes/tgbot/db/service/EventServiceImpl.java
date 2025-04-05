package ru.daniil4jk.svuroutes.tgbot.db.service;

import jakarta.persistence.PersistenceException;
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

@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {
    @Autowired
    private Map<Long, Route> routes;
    @Autowired
    private EventRepository repository;

    @Override
    public EventEntity get(long id) {
        return repository.findById(id).orElseThrow();
    }

    @Override
    public EventEntity get(EventEntity eventEntity) {
        return get(eventEntity.getId());
    }

    @Override
    public boolean contains(long id) {
        return repository.existsById(id);
    }

    @Override
    public boolean contains(EventEntity eventEntity) {
        return contains(eventEntity.getId());
    }

    public EventEntity createNew(long routeId, Date end, String tourGuide, int maxUsers) {
        return save(new EventEntity(null, routes.get(routeId).getName(),
                new ArrayList<>(), maxUsers, end, tourGuide, routeId));
    }

    @Override
    public EventEntity save(EventEntity eventEntity) {
        try {
            return repository.save(eventEntity);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void remove(long id) {
        repository.deleteById(id);
    }

    @Override
    public void remove(EventEntity eventEntity) {
        repository.delete(eventEntity);
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
