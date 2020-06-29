package eu.cloudnetservice.cloudnet.v2.console;

import eu.cloudnetservice.cloudnet.v2.console.model.ConsoleSignalDispatch;
import org.jline.terminal.Terminal;

import java.util.HashMap;
import java.util.Map;

public final class SignalManager implements Terminal.SignalHandler {

    private final Map<String,ConsoleSignalDispatch> signalDispatchList;

    public SignalManager() {
        this.signalDispatchList = new HashMap<>();
    }

    @Override
    public void handle(Terminal.Signal signal) {
        this.signalDispatchList.values().forEach(consoleSignalDispatch -> consoleSignalDispatch.dispatchSignal(signal));
    }

    public void registerSignalHandler(ConsoleSignalDispatch consoleSignalDispatch) {
        if (!this.signalDispatchList.containsKey(consoleSignalDispatch.getClass().getName())) {
            this.signalDispatchList.putIfAbsent(consoleSignalDispatch.getClass().getName(),consoleSignalDispatch);
        }
    }

    public void unregisterSignalHandler(ConsoleSignalDispatch consoleSignalDispatch) {
        this.signalDispatchList.remove(consoleSignalDispatch.getClass().getName());
    }

    public void unregisterSignalHandler(Class<? extends ConsoleSignalDispatch> consoleSignalDispatch) {
        this.signalDispatchList.remove(consoleSignalDispatch.getName());
    }

}
