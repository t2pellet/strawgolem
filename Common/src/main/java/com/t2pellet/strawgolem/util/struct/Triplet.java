package com.t2pellet.strawgolem.util.struct;

import java.util.Objects;

public class Triplet<T1, T2, T3> {
    private final T1 first;
    private final T2 second;
    private final T3 third;

    public Triplet(T1 first, T2 second, T3 third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T1 getFirst() {
        return first;
    }

    public T2 getSecond() {
        return second;
    }

    public T3 getThird() {
        return third;
    }

    @Override
    public int hashCode() {
        if (first == null || second == null || third == null) {
            throw new NullPointerException("Some of the triplet values are null!");
        }
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        result = 31 * result + third.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Triplet<?, ?, ?> other)) return false;
        return Objects.equals(first, other.first)
                && Objects.equals(second, other.second)
                && Objects.equals(third, other.third);
    }
}