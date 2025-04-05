package ru.daniil4jk.svuroutes.tgbot.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.daniil4jk.svuroutes.tgbot.db.entity.RequestEntity;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
    Optional<RequestEntity> getFirstByIdGreaterThanAndStatus(long id, RequestEntity.Status status);
    Set<RequestEntity> findAllByUser_Id(long userId);
    Set<RequestEntity> findAllByStatus(RequestEntity.Status status);
}