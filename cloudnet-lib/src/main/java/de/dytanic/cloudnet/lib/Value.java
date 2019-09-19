package de.dytanic.cloudnet.lib;

import java.util.Objects;

/**
 * Created by Tareko on 23.06.2017.
 */
public class Value<E> {

    private E value;

    public Value(E value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Value)) {
            return false;
        }
        final Value<?> value1 = (Value<?>) o;
        return Objects.equals(value, value1.value);
    }

    @Override
    public String toString() {
        return "Value{" + "value=" + value + '}';
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }
}
