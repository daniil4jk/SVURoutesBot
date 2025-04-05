package ru.daniil4jk.svuroutes.tgbot.db.service;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.daniil4jk.svuroutes.tgbot.db.entity.EventEntity;
import ru.daniil4jk.svuroutes.tgbot.db.entity.RequestEntity;
import ru.daniil4jk.svuroutes.tgbot.db.entity.UserEntity;
import ru.daniil4jk.svuroutes.tgbot.db.repository.RequestRepository;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.RequestService;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Service
public class RequestServiceImpl implements RequestService {
    @Autowired
    private RequestRepository repository;

    @Override
    public RequestEntity createNew(String firstName, String lastName, Integer age,
                                   Integer classNumber, String schoolName,
                                   EventEntity event, UserEntity user) {
        return save(new RequestEntity(null, firstName, lastName, age, classNumber,
                schoolName, event, user, RequestEntity.Status.WAITING));
    }

    @Override
    public Optional<RequestEntity> getNext(Long currentId) {
        return Optional.ofNullable(currentId).map(
                        currId -> repository.getFirstByIdGreaterThanAndStatus(
                                currId, RequestEntity.Status.WAITING))
                .orElseGet(() -> repository.getFirstByIdGreaterThanAndStatus(
                        0L, RequestEntity.Status.WAITING));
    }

    @Override
    public Set<RequestEntity> getByUserId(long userId) {
        return repository.findAllByUser_Id(userId);
    }

    @Override
    public RequestEntity save(RequestEntity requestEntity) {
        try {
            return repository.save(requestEntity);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public RequestEntity get(long id) {
        return repository.findById(id).orElseThrow();
    }

    @Override
    public RequestEntity get(RequestEntity requestEntity) {
        return get(requestEntity.getId());
    }

    @Override
    public boolean contains(long id) {
        return repository.existsById(id);
    }

    @Override
    public boolean contains(RequestEntity requestEntity) {
        return contains(requestEntity.getId());
    }

    @Override
    public void update(long id, Consumer<RequestEntity> updates) {
        var entity = get(id);
        updates.accept(entity);
        save(entity);
    }

    @Override
    public void update(RequestEntity requestEntity, Consumer<RequestEntity> updates) {
        update(requestEntity.getId(), updates);
    }

    @Override
    public void remove(long id) {
        repository.deleteById(id);
    }

    @Override
    public void remove(RequestEntity requestEntity) {
        repository.delete(requestEntity);
    }

    @PostConstruct
    private void fixAnomalies() {
        for (var request : repository.findAllByStatus(RequestEntity.Status.IN_PROGRESS)) {
            request.setStatus(RequestEntity.Status.WAITING);
            save(request);
        }
    }
}
