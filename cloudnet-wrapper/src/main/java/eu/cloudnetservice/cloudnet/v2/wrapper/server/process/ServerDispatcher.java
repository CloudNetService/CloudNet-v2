package eu.cloudnetservice.cloudnet.v2.wrapper.server.process;

import eu.cloudnetservice.cloudnet.v2.lib.interfaces.Executable;
import eu.cloudnetservice.cloudnet.v2.wrapper.screen.Screenable;

import java.io.IOException;

public interface ServerDispatcher extends Executable, Screenable {

    default void executeCommand(String consoleCommand) {
        if (!isAlive()) {
            return;
        }

        try {
            getInstance().getOutputStream().write((consoleCommand + '\n').getBytes());
            getInstance().getOutputStream().flush();
        } catch (Exception ex) {
            ex.printStackTrace();
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
