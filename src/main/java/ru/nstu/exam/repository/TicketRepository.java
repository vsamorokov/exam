package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.ExamPeriod;
import ru.nstu.exam.entity.Student;
import ru.nstu.exam.entity.Ticket;

import java.util.List;

@Repository
public interface TicketRepository extends PersistableEntityRepository<Ticket> {
    List<Ticket> findAllByStudent(Student student);

    List<Ticket> findAllByExamPeriod(ExamPeriod examPeriod);
}
