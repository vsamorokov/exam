package ru.nstu.exam.service.mapper;

public interface Mapper<B, E> {
    default B map(E entity) {
        return map(entity, 0);
    }

    B map(E entity, int level);
}
