package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Discipline;
import ru.nstu.exam.entity.Group;
import ru.nstu.exam.entity.Teacher;

import java.util.List;

@Repository
public interface DisciplineRepository extends PersistableEntityRepository<Discipline> {
    List<Discipline> findByGroupsContaining(Group group);

    List<Discipline> findByTeachersContaining(Teacher teacher);
}
