package ru.daniil4jk.svuroutes.tgbot.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.daniil4jk.svuroutes.tgbot.db.entity.EventEntity;

import java.util.Date;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {
    @Query("select e from EventEntity e where e.date > ?1")
    Set<EventEntity> findByDateAfter(Date date);
}