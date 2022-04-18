package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.RatingSystem;

@Repository
public interface RatingSystemRepository extends PersistableEntityRepository<RatingSystem> {
}
