package ru.daniil4jk.svuroutes.tgbot.db.service.assets;

import ru.daniil4jk.svuroutes.tgbot.db.entity.EventEntity;
import ru.daniil4jk.svuroutes.tgbot.db.entity.RequestEntity;
import ru.daniil4jk.svuroutes.tgbot.db.entity.UserEntity;

import java.util.Optional;
import java.util.Set;

public interface RequestService extends TService<RequestEntity> {
    RequestEntity createNew(String firstName, String lastName, Integer age,
                            Integer classNumber, String schoolName,
                            EventEntity event, UserEntity user);
    Optional<RequestEntity> getNext(Long current);
    Set<RequestEntity> getByUserId(long userId);
}
