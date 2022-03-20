package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Exam;
import ru.nstu.exam.entity.ExamPeriod;

import java.util.List;

@Repository
public interface ExamPeriodRepository extends PersistableEntityRepository<ExamPeriod> {

    List<ExamPeriod> findAllByExam(Exam exam);

}
