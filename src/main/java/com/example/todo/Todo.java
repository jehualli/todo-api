package com.example.todo;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "todos")
public class Todo extends PanacheEntityBase {

    @Id
    @GeneratedValue
    @UuidGenerator
    public UUID id;

    @Column(nullable = false, length = 120)
    public String title;

    @Column(columnDefinition = "TEXT")
    public String description;

    @Column(nullable = false)
    public boolean completed;

    @Column(name = "created_at", nullable = false)
    public OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        touch();
    }

    void touch() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
