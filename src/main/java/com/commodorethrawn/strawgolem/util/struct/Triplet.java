package com.commodorethrawn.strawgolem.util.struct;

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

    public T1 getFirst() { return first; }
    public T2 getSecond() { return second; }
    public T3 getThird() { return third; }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Triplet)) return false;
        Triplet<?,?,?> other = (Triplet<?,?,?>) obj;
        return Objects.equals(first, other.first) && Objects.equals(second, other.second) && Objects.equals(third, other.third);
    }
}