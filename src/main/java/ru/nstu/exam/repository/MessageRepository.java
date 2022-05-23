package ru.nstu.exam.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Answer;
import ru.nstu.exam.entity.Message;

@Repository
public interface MessageRepository extends PersistableEntityRepository<Message> {

    Page<Message> findAllByAnswer(Answer answer, Pageable pageable);
}
