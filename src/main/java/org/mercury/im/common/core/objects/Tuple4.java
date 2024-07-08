package org.mercury.im.common.core.objects;

public class Tuple4<T1, T2,T3, T4>{
    private final T1 first;

    private final T2 second;
    private final T3 third;
    private final T4 fourth;

    public Tuple4(T1 first, T2 second, T3 third, T4 fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
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

    public T4 getFourth() {
        return fourth;
    }
}
