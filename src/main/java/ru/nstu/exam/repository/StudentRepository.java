package ru.nstu.exam.repository;


import org.springframework.stereotype.Repository;
import ru.nstu.exam.bean.StudentBean;
import ru.nstu.exam.entity.Group;
import ru.nstu.exam.entity.Student;

import java.util.List;

@Repository
public interface StudentRepository extends PersistableEntityRepository<Student>{
    List<Student> findAllByGroup(Group group);
}
