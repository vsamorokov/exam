package ru.nstu.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;
import ru.nstu.exam.entity.convert.UserRolesStringConverter;
import ru.nstu.exam.security.UserRole;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Set;

@Data
@Entity
@Table(name = "account")
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted = false")
public class Account extends PersistableEntity {

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "roles", nullable = false, columnDefinition = "string")
    @Convert(converter = UserRolesStringConverter.class)
    private Set<UserRole> roles;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;
}
