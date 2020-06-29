package eu.cloudnetservice.cloudnet.v2.console;

import eu.cloudnetservice.cloudnet.v2.console.model.ConsoleInputDispatch;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 */
public final class ConsoleRegistry {

    private final Map<String, ConsoleInputDispatch> inputDispatchMap;

    public ConsoleRegistry() {
        this.inputDispatchMap = new HashMap<>();
    }

    public void registerInput(ConsoleInputDispatch inputDispatch) {
        if (!this.inputDispatchMap.containsKey(inputDispatch.getClass().getName())) {
            this.inputDispatchMap.putIfAbsent(inputDispatch.getClass().getName(),inputDispatch);
        }
    }

    public void unregisterInput(Class<? extends ConsoleInputDispatch> clazz) {
        this.inputDispatchMap.remove(clazz.getName());
    }

    protected Optional<ConsoleInputDispatch> getConsole(Class<? extends ConsoleInputDispatch> clazz) {
        Optional<ConsoleInputDispatch> inputDispatch = Optional.empty();
        if (this.inputDispatchMap.containsKey(clazz.getName())) {
            inputDispatch = Optional.of(this.inputDispatchMap.get(clazz.getName()));
        }
        return inputDispatch;
    }

    public Optional<ConsoleInputDispatch> getInput(Class<? extends ConsoleInputDispatch> clazz) {
        Optional<ConsoleInputDispatch> inputDispatch = Optional.empty();
        if (this.inputDispatchMap.containsKey(clazz.getName())) {
            inputDispatch = Optional.of(this.inputDispatchMap.get(clazz.getName()));
        }
        return inputDispatch;
    }
}
