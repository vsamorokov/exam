package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Exam;
import ru.nstu.exam.entity.Teacher;

import java.util.List;

@Repository
public interface ExamRepository extends PersistableEntityRepository<Exam> {

    List<Exam> findAllByTeacher(Teacher teacher);

}
