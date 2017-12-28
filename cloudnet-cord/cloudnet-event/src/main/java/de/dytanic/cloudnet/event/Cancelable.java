package de.dytanic.cloudnet.event;

/**
 * Created by Tareko on 26.07.2017.
 */
public interface Cancelable {

    void setCancelled(boolean cancel);

    boolean isCancelled();

}