package ru.nstu.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(
        name = "access_token",
        indexes = @Index(name = "token_idx", columnList = "token")
)
@EqualsAndHashCode(callSuper = true)
public class AccessToken extends AbstractPersistable<Long> {

    @Column(name = "token", nullable = false)
    private String token;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "issue_time", nullable = false)
    private LocalDateTime issueTime;
}
