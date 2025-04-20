package ru.daniil4jk.svuroutes.tgbot.db.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.InvalidPropertyException;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@SoftDelete
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "events")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "request_id", nullable = false)
    @OneToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<RequestEntity> requestEntities;
    @Column(name = "max_users")
    private Integer maxUsers;
    @Column(name = "date", nullable = false)
    private Date date;
    @Column(name = "tour_guide")
    private String guideName;
    @Column(name = "route_id", nullable = false)
    private Long routeId;
    @Column(name = "dot_id")
    private Long dotId;

    @PrePersist
    @PreUpdate
    protected void isRequestsInRange() {
        if (countRequests() > maxUsers) throw new InvalidPropertyException(EventEntity.class,
                "requestEntities","Невалидное количество записей");;
    }

    public boolean canAddRequest() {
        return countRequests() + 1 <= maxUsers;
    }

    private int countRequests() {
        if (getRequestEntities() == null) return 0;
        int count = 0;
        for (var e : getRequestEntities()) {
            if (!RequestEntity.Status.REJECTED.equals(e.getStatus())) {
                count++;
            }
        }
        return count;
    }

    public TimeBefore getTimeBefore() {
        return TimeBefore.of(date);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        EventEntity eventEntity = (EventEntity) o;
        return this.getId() != null && Objects.equals(this.getId(), eventEntity.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}

