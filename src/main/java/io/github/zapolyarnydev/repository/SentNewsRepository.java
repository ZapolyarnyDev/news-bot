package io.github.zapolyarnydev.repository;

import io.github.zapolyarnydev.entity.SentNewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SentNewsRepository extends JpaRepository<SentNewsEntity, String> {
}
