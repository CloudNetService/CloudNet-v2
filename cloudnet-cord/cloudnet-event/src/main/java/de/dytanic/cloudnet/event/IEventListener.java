package de.dytanic.cloudnet.event;

/**
 * Created by Tareko on 23.07.2017.
 */
public interface IEventListener<E extends Event> {

    void onCall(E event);

}