package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Task;

@Repository
public interface TaskRepository extends PersistableEntityRepository<Task>{
}
