package ru.daniil4jk.svuroutes.tgbot.db.service;

import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.daniil4jk.svuroutes.tgbot.db.entity.SuggestionEntity;
import ru.daniil4jk.svuroutes.tgbot.db.repository.SuggestionRepository;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.SuggestionService;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.UserService;

import java.util.function.Consumer;

@Component
public class SuggestionServiceImpl implements SuggestionService {
    @Autowired
    private SuggestionRepository repository;
    @Autowired
    private UserService userService;

    @Override
    public SuggestionEntity save(SuggestionEntity entity) {
        try {
            return repository.save(entity);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public SuggestionEntity get(long id) {
        return repository.findById(id).orElseThrow();
    }

    @Override
    public SuggestionEntity get(SuggestionEntity entity) {
        return get(entity.getId());
    }

    @Override
    public boolean contains(long id) {
        return repository.existsById(id);
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
        repository.deleteById(id);
    }

    @Override
    public void remove(SuggestionEntity entity) {
        repository.delete(entity);
    }

    @Override
    public SuggestionEntity createNew(long chatId, String text) {
        return save(new SuggestionEntity(
                null,
                userService.get(chatId),
                text
        ));
    }
}
