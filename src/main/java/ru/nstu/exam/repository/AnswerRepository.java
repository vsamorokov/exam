package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Answer;
import ru.nstu.exam.entity.Ticket;

import java.util.List;

@Repository
public interface AnswerRepository extends PersistableEntityRepository<Answer> {
    List<Answer> findAllByTicket(Ticket ticket);
}
