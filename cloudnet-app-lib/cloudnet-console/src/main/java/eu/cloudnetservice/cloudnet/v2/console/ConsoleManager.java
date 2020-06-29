package eu.cloudnetservice.cloudnet.v2.console;

import eu.cloudnetservice.cloudnet.v2.console.model.ConsoleInputDispatch;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultExpander;
import org.jline.reader.impl.DefaultHighlighter;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Attributes;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Optional;

public final class ConsoleManager {

    //private boolean password;
    private Terminal terminal;
    private LineReader lineReader;
    private final ConsoleRegistry consoleRegistry;
    private ConsoleInputDispatch consoleInputDispatch;
    private final SignalManager signalManager;
    private boolean running;

    public ConsoleManager(ConsoleRegistry consoleRegistry, SignalManager signalManager) {
        this.consoleRegistry = consoleRegistry;
        this.signalManager = signalManager;
    }


    public void useDefaultConsole() {
        final Attributes attributes = new Attributes();
        try {
            this.terminal = TerminalBuilder.builder()
                                           .streams(System.in, System.out)
                                           .jansi(true)
                                           .jna(true)
                                           .encoding(StandardCharsets.UTF_8)
                                           .name(
                                               "CloudNet-Terminal")
                                           .dumb(true)
                                           .system(true)
                                           .attributes(attributes)
                                           .system(false)
                                           .signalHandler(this.signalManager)
                                           .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (this.consoleInputDispatch != null) {
            this.lineReader = LineReaderBuilder.builder()
                                               .appName("CloudNet-Console")
                                               .highlighter(new DefaultHighlighter())
                                               .expander(new DefaultExpander())
                                               .history(new DefaultHistory())
                                               .
                                                   variable(LineReader.HISTORY_FILE, Paths.get(".cn_history"))
                                               .terminal(this.terminal)
                                               .parser(new DefaultParser())
                                               .completer(new StringsCompleter(this.consoleInputDispatch.get()))
                                               .build();
        } else {
            throw new NullPointerException("Console input dispatcher is empty");
        }

    }

    public void changeConsoleInput(Class<? extends ConsoleInputDispatch> clazz) {
        final Optional<ConsoleInputDispatch> console = this.consoleRegistry.getConsole(clazz);
        console.ifPresent(inputDispatch -> this.consoleInputDispatch = inputDispatch);
    }

    public void startConsole() {
        /*while (this.running) {
            if (this.consoleInputDispatch != null) {
                if (this.password) {

                } else {

                }
            }
        }*/
    }

   /* public void setPassword(boolean password) {
        this.password = password;
    }*/

    public void setRunning(final boolean running) {
        this.running = running;
    }

    public void setLineReader(@NotNull LineReader lineReader) {
        this.lineReader = lineReader;
    }

    public void setTerminal(@NotNull Terminal terminal) {
        this.terminal = terminal;
    }
}
