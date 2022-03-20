package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Discipline;
import ru.nstu.exam.entity.ExamRule;

import java.util.List;

@Repository
public interface ExamRuleRepository extends PersistableEntityRepository<ExamRule> {

    List<ExamRule> findAllByDiscipline(Discipline discipline);
}
