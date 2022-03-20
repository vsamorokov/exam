package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.ExamPeriod;

@Repository
public interface ExamPeriodRepository extends PersistableEntityRepository<ExamPeriod> {
}
