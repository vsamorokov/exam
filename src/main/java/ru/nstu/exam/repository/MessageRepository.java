package ru.nstu.exam.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.entity.Answer;
import ru.nstu.exam.entity.ExamPeriod;
import ru.nstu.exam.entity.Message;

import java.util.List;

@Repository
public interface MessageRepository extends PersistableEntityRepository<Message> {

    Page<Message> findAllByExamPeriodAndAnswerIsNull(ExamPeriod examPeriod, Pageable pageable);

    Page<Message> findAllByAnswer(Answer answer, Pageable pageable);

    List<Message> findAllByAccount(Account account);
}
