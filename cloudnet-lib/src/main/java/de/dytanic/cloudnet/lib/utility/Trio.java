/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.utility;

public class Trio<F, S, T> {

    private F first;

    private S second;

    private T third;

    public Trio(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T getThird() {
        return third;
    }

    public S getSecond() {
        return second;
    }

    public F getFirst() {
        return first;
    }
}