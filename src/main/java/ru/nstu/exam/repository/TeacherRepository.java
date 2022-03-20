package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.entity.Teacher;

@Repository
public interface TeacherRepository extends PersistableEntityRepository<Teacher> {

    Teacher findByAccount(Account account);
}
