package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.GroupRating;
import ru.nstu.exam.entity.Student;
import ru.nstu.exam.entity.StudentRating;

import java.util.List;

@Repository
public interface StudentRatingRepository extends PersistableEntityRepository<StudentRating> {
    List<StudentRating> findAllByStudent(Student student);

    boolean existsByStudentAndGroupRating(Student student, GroupRating groupRating);
}
