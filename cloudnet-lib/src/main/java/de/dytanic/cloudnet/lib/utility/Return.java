package de.dytanic.cloudnet.lib.utility;

/**
 * Created by Tareko on 25.05.2017.
 */
public class Return<F, S> {

    private F first;
    private S second;

    public Return(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }
}
