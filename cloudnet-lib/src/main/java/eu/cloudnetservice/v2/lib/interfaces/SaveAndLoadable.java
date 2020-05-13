package eu.cloudnetservice.v2.lib.interfaces;

public interface SaveAndLoadable<E> {

    boolean save(E value);

    E load();

}