package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Discipline;

@Repository
public interface DisciplineRepository extends PersistableEntityRepository<Discipline> {
}
