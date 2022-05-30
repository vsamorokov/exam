package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Exam;
import ru.nstu.exam.entity.Teacher;
import ru.nstu.exam.enums.ExamState;

import java.util.List;
import java.util.Set;

@Repository
public interface ExamRepository extends PersistableEntityRepository<Exam> {

    List<Exam> findAllByStateIn(Set<ExamState> examStates);

    List<Exam> findAllByTeacher(Teacher teacher);
}
