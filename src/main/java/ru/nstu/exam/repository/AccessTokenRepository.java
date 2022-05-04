package ru.nstu.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.AccessToken;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {
    AccessToken findByToken(String token);

    List<AccessToken> findAllByIssueTimeBefore(LocalDateTime time);

}
