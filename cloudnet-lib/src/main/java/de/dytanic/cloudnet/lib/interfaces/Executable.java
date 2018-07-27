package de.dytanic.cloudnet.lib.interfaces;

/**
 * Created by Tareko on 21.05.2017.
 */
public interface Executable {

    boolean bootstrap() throws Exception;

    boolean shutdown();

}