package ru.daniil4jk.svuroutes.tgbot.db.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "requests")
public class RequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "age", nullable = false)
    private Integer age;
    @Column(name = "class_number")
    private Integer classNumber;
    @Column(name = "school_name")
    private String schoolName;
    @JoinColumn(name = "event_id")
    @ManyToOne(cascade = {CascadeType.PERSIST})
    private EventEntity event;
    @JoinColumn(name = "user_id")
    @ManyToOne(cascade = CascadeType.MERGE)
    private UserEntity user;
    @Column(name = "status", nullable = false)
    private Status status;

    @PrePersist
    public void prePersist() {
        System.err.println("REQUEST PRE PERSIST");
    }

    @PreUpdate
    public void preUpdate() {
        System.err.println("REQUEST PRE UPDATE");
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        RequestEntity requestEntity = (RequestEntity) o;
        return getId() != null && Objects.equals(getId(), requestEntity.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public enum Status {
        WAITING,
        IN_PROGRESS,
        ACCEPTED,
        REJECTED
    }
}
