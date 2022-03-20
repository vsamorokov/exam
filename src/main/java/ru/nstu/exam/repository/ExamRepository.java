package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Exam;

@Repository
public interface ExamRepository extends PersistableEntityRepository<Exam> {
}
