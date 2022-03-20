package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Theme;

@Repository
public interface ThemeRepository extends PersistableEntityRepository<Theme> {
}
