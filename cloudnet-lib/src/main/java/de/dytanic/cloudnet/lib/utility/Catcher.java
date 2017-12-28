package de.dytanic.cloudnet.lib.utility;

/**
 * Created by Tareko on 19.07.2017.
 */
public interface Catcher<E, V> {

    E doCatch(V key);

}
