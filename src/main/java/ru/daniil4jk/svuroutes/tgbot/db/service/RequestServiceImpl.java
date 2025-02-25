package ru.daniil4jk.svuroutes.tgbot.db.service;

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
import java.util.function.Predicate;

@RequiredArgsConstructor
@Service
public class RequestServiceImpl implements RequestService {
    private static final Predicate<RequestEntity> notRemoved =
            request -> !request.isRemoved();
    @Autowired
    private RequestRepository repository;

    @Override
    public RequestEntity createNew(String firstName, String lastName, Integer age,
                                   Integer classNumber, String schoolName,
                                   EventEntity event, UserEntity user) {
        return save(new RequestEntity(null, firstName, lastName, age, classNumber,
                schoolName, event, user, RequestEntity.Status.WAITING, false));
    }

    @Override
    public Optional<RequestEntity> getNext(Long currentId) {
        return Optional.ofNullable(currentId).map(
                        currId -> repository
                                .getFirstByIdGreaterThanAndStatus(currId, RequestEntity.Status.WAITING)
                                .filter(notRemoved))
                .orElseGet(() -> repository
                        .getFirstByIdGreaterThanAndStatus(0L, RequestEntity.Status.WAITING)
                        .filter(notRemoved));
    }

    @Override
    public Set<RequestEntity> getByUserId(long userId) {
        return repository.getByUser_IdAndRemoved(userId, false);
    }

    @Override
    public RequestEntity save(RequestEntity requestEntity) {
        return repository.save(requestEntity);
    }

    @Override
    public RequestEntity get(long id) {
        return repository.findById(id).filter(notRemoved).orElseThrow();
    }

    @Override
    public RequestEntity get(RequestEntity requestEntity) {
        return get(requestEntity.getId());
    }

    @Override
    public boolean contains(long id) {
        return repository.existsByIdAndRemoved(id, false);
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
        update(id, e -> e.setRemoved(true));
    }

    @Override
    public void remove(RequestEntity requestEntity) {
        remove(requestEntity.getId());
    }
}
