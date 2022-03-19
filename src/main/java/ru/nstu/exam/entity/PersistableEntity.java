package ru.nstu.exam.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;


@Data
@MappedSuperclass
@Where(clause = "deleted = false")
@EqualsAndHashCode(callSuper = true)
public class PersistableEntity extends AbstractPersistable<Long> {

    @Transient
    private final static ZoneId UTC = ZoneId.of("UTC");

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "updated")
    private LocalDateTime updated;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now(UTC);
        created = now;
        updated = now;
    }

    @PreUpdate
    public void preUpdate() {
        updated = LocalDateTime.now(UTC);
    }
}

