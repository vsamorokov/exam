package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Group;

@Repository
public interface GroupRepository extends PersistableEntityRepository<Group> {

    Long countByName(String name);
}
