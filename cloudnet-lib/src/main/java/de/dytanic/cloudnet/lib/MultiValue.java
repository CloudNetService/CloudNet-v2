package de.dytanic.cloudnet.lib;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by Tareko on 26.07.2017.
 */
@ToString
@EqualsAndHashCode
public class MultiValue<F, S> {

    private F first;

    private S second;

    public MultiValue(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public S getSecond() {
        return second;
    }

    public F getFirst() {
        return first;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    public void setFirst(F first) {
        this.first = first;
    }
}