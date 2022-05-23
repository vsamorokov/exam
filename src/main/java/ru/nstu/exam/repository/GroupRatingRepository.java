package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Discipline;
import ru.nstu.exam.entity.Group;
import ru.nstu.exam.entity.GroupRating;

@Repository
public interface GroupRatingRepository extends PersistableEntityRepository<GroupRating> {
    GroupRating findByDisciplineAndGroup(Discipline discipline, Group group);
}
