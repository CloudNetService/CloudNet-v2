package eu.cloudnetservice.cloudnet.v2.lib.interfaces;

public interface SaveAndLoadable<E> {

    boolean save(E value);

    E load();

}