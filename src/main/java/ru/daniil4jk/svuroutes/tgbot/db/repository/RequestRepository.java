package ru.daniil4jk.svuroutes.tgbot.db.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.daniil4jk.svuroutes.tgbot.db.entity.RequestEntity;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
    Optional<RequestEntity> getFirstByIdGreaterThanAndStatus(long id, RequestEntity.Status status);
    boolean existsByIdAndRemoved(@NotNull Long id, boolean removed);
    Set<RequestEntity> getByUser_IdAndRemovedAndEvent_Removed(long userId, boolean removed, boolean eventRemoved);
    Set<RequestEntity> getAllByStatus(RequestEntity.Status status);
}