package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Task;
import ru.nstu.exam.entity.Theme;
import ru.nstu.exam.enums.TaskType;

import java.util.Collection;
import java.util.List;

@Repository
public interface TaskRepository extends PersistableEntityRepository<Task> {

    List<Task> findAllByThemeInAndTaskType(Collection<Theme> themes, TaskType taskType);

    List<Task> findAllByThemeIn(Collection<Theme> themes);
}
