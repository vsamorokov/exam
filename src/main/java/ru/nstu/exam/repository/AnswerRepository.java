package ru.nstu.exam.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Answer;
import ru.nstu.exam.entity.StudentRating;

import java.util.List;

@Repository
public interface AnswerRepository extends PersistableEntityRepository<Answer> {
    List<Answer> findAllByStudentRating(StudentRating studentRating, Pageable pageable);
}
