package ru.nstu.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Artefact;

@Repository
public interface ArtefactRepository extends JpaRepository<Artefact, Long> {
}
