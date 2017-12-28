package de.dytanic.cloudnet.lib;

/**
 * Created by Tareko on 22.06.2017.
 */
public interface RunnabledCall<E1, E2> {

    E2 call(E1 value);

}