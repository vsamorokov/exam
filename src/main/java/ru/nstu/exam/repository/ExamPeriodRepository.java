package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Exam;
import ru.nstu.exam.entity.ExamPeriod;
import ru.nstu.exam.enums.ExamPeriodState;

import java.util.Collection;
import java.util.List;

@Repository
public interface ExamPeriodRepository extends PersistableEntityRepository<ExamPeriod> {

    List<ExamPeriod> findAllByExam(Exam exam);

    List<ExamPeriod> findAllByStateIn(Collection<ExamPeriodState> states);

}
