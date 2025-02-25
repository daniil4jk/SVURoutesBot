package ru.daniil4jk.svuroutes.tgbot.db.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.daniil4jk.svuroutes.tgbot.db.entity.SuggestionEntity;

@Repository
public interface SuggestionRepository extends JpaRepository<SuggestionEntity, Long> {
    boolean existsByIdAndRemoved(@NotNull Long id, boolean removed);
}