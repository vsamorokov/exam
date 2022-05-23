package ru.nstu.exam.utils;

import lombok.Data;

@Data
public class Pair<L, R> {
    public final L left;
    public final R right;

    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }
}
