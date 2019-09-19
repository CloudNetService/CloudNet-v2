package de.dytanic.cloudnet.lib.utility;

/**
 * Created by Tareko on 20.01.2018.
 */
public class Quad<F, S, T, FF> {

    private F first;

    private S second;

    private T third;

    private FF fourth;

    public Quad() {
    }

    public Quad(F first, S second, T third, FF fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public F getFirst() {
        return first;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public FF getFourth() {
        return fourth;
    }

    public void setFourth(FF fourth) {
        this.fourth = fourth;
    }

    public S getSecond() {
        return second;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    public T getThird() {
        return third;
    }

    public void setThird(T third) {
        this.third = third;
    }
}
