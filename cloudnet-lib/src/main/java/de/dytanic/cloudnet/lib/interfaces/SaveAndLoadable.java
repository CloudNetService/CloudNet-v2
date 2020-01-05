package de.dytanic.cloudnet.lib.interfaces;

public interface SaveAndLoadable<E> {

    boolean save(E value);

    E load();

}