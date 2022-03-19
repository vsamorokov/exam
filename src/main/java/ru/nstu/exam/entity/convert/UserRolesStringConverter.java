package ru.nstu.exam.entity.convert;

import ru.nstu.exam.security.UserRole;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class UserRolesStringConverter implements AttributeConverter<Set<UserRole>, String> {

    private final static String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(Set<UserRole> roles) {
        return String.join(DELIMITER, roles.stream().map(UserRole::name).collect(Collectors.toSet()));
    }

    @Override
    public Set<UserRole> convertToEntityAttribute(String roles) {
        return Arrays.stream(roles.split(DELIMITER)).map(UserRole::valueOf).collect(Collectors.toSet());
    }
}
