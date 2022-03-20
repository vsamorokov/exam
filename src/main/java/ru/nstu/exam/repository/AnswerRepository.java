package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Answer;

@Repository
public interface AnswerRepository extends PersistableEntityRepository<Answer> {
}
