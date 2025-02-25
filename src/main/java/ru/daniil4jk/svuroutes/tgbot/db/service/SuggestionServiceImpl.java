package ru.daniil4jk.svuroutes.tgbot.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.daniil4jk.svuroutes.tgbot.db.entity.SuggestionEntity;
import ru.daniil4jk.svuroutes.tgbot.db.repository.SuggestionRepository;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.SuggestionService;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Component
public class SuggestionServiceImpl implements SuggestionService {
    private static final Predicate<SuggestionEntity> notRemoved =
            suggestion -> !suggestion.isRemoved();

    @Autowired
    private SuggestionRepository repository;

    @Override
    public SuggestionEntity save(SuggestionEntity entity) {
        return repository.save(entity);
    }

    @Override
    public SuggestionEntity get(long id) {
        return repository.findById(id).filter(notRemoved).orElseThrow();
    }

    @Override
    public SuggestionEntity get(SuggestionEntity entity) {
        return get(entity.getId());
    }

    @Override
    public boolean contains(long id) {
        return repository.existsByIdAndRemoved(id, false);
    }

    @Override
    public boolean contains(SuggestionEntity entity) {
        return contains(entity.getId());
    }

    @Override
    public void update(long id, Consumer<SuggestionEntity> updates) {
        var entity = get(id);
        updates.accept(entity);
        save(entity);
    }

    @Override
    public void update(SuggestionEntity entity, Consumer<SuggestionEntity> updates) {
        update(entity.getId(), updates);
    }

    @Override
    public void remove(long id) {
        update(id, e -> e.setRemoved(true));
    }

    @Override
    public void remove(SuggestionEntity entity) {
        remove(entity.getId());
    }
}
