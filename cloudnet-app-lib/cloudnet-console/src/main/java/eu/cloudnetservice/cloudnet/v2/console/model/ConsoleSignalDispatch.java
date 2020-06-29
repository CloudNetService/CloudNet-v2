package eu.cloudnetservice.cloudnet.v2.console.model;

import org.jline.terminal.Terminal;

public interface ConsoleSignalDispatch {

    void dispatchSignal(Terminal.Signal signal);
}
