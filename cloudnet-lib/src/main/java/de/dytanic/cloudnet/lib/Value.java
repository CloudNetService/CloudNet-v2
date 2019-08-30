package de.dytanic.cloudnet.lib;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by Tareko on 23.06.2017.
 */
@ToString
@EqualsAndHashCode
public class Value<E> {

    private E value;

    public Value(E value) {
        this.value = value;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }
}