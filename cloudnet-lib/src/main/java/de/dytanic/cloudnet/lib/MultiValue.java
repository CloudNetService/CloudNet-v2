package de.dytanic.cloudnet.lib;

import java.util.Objects;

/**
 * Created by Tareko on 26.07.2017.
 */
public class MultiValue<F, S> {

    private F first;
    private S second;

    public MultiValue(F first, S second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MultiValue)) {
            return false;
        }
        final MultiValue<?, ?> that = (MultiValue<?, ?>) o;
        return Objects.equals(first, that.first) && Objects.equals(second, that.second);
    }

    @Override
    public String toString() {
        return "MultiValue{" + "first=" + first + ", second=" + second + '}';
    }

    public S getSecond() {
        return second;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public void setFirst(F first) {
        this.first = first;
    }
}
