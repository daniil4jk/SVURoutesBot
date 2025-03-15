package ru.daniil4jk.svuroutes.tgbot.db.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.daniil4jk.svuroutes.tgbot.db.entity.UserEntity;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> getByUsernameContainsIgnoreCase(String username);
    boolean existsByIdAndRemoved(@NotNull Long id, boolean removed);
}