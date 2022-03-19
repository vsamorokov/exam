package ru.nstu.exam.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.nstu.exam.entity.Account;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Data
public class UserDetailsImpl implements UserDetails {

    private String username;
    private String password;
    private Set<UserRole> roles;

    private Collection<GrantedAuthority> authorities;

    public UserDetailsImpl(Account account) {
        this.username = account.getUsername();
        this.password = account.getPassword();
        this.roles = account.getRoles();
        this.authorities = getRoles().stream().map(r -> new SimpleGrantedAuthority(r.name())).collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
