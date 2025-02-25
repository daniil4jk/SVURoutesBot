package ru.daniil4jk.svuroutes.tgbot.db.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "suggestion_entity")
public class SuggestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(cascade = CascadeType.MERGE)
    private UserEntity user;
    @Column(name = "text", nullable = false)
    private String text;
    @Column(name = "removed")
    private boolean removed;

}