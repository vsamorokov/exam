package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Discipline;
import ru.nstu.exam.entity.Group;

import java.util.List;

@Repository
public interface GroupRepository extends PersistableEntityRepository<Group> {
    List<Group> findAllByDisciplinesContaining(Discipline discipline);

    Long findCountByName(String name);
}
