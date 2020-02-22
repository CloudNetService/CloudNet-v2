package de.dytanic.cloudnetwrapper.server.process;

import de.dytanic.cloudnet.lib.interfaces.Executable;
import de.dytanic.cloudnetwrapper.screen.Screenable;

import java.io.IOException;

public interface ServerDispatcher extends Executable, Screenable {

    default void executeCommand(String consoleCommand) {
        if (getInstance() == null && !getInstance().isAlive()) {
            return;
        }

        try {
            getInstance().getOutputStream().write((consoleCommand + '\n').getBytes());
            getInstance().getOutputStream().flush();
        } catch (Exception ex) {
        }
    }

    Process getInstance();

    default boolean isAlive() {
        try {
            return getInstance() != null && getInstance().isAlive() && getInstance().getInputStream().available() != -1;
        } catch (IOException e) {
            return false;
        }
    }

}
