package ru.nstu.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import ru.nstu.exam.entity.PersistableEntity;

@NoRepositoryBean
public interface PersistableEntityRepository<T extends PersistableEntity> extends JpaRepository<T, Long> {
}
