package eu.cloudnetservice.cloudnet.v2.console;

import eu.cloudnetservice.cloudnet.v2.console.completer.CloudNetCompleter;
import eu.cloudnetservice.cloudnet.v2.console.model.ConsoleChangeInputPromote;
import eu.cloudnetservice.cloudnet.v2.console.model.ConsoleInputDispatch;
import eu.cloudnetservice.cloudnet.v2.logging.CloudLogger;
import org.fusesource.jansi.AnsiOutputStream;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultExpander;
import org.jline.reader.impl.DefaultHighlighter;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;

public final class ConsoleManager {

    private boolean password;
    private String prompt;
    private char passwordMask = '*';

    private LineReader lineReader;
    private final ConsoleRegistry consoleRegistry;
    private ConsoleInputDispatch consoleInputDispatch;
    private final SignalManager signalManager;
    private boolean running;
    private CloudLogger cloudLogger;

    public ConsoleManager(ConsoleRegistry consoleRegistry, SignalManager signalManager, CloudLogger logger) {
        this.consoleRegistry = consoleRegistry;
        this.signalManager = signalManager;
        this.prompt = String.format("%s@Master $ ", System.getProperty("user.name"));
        this.cloudLogger = logger;
    }


    public void useDefaultConsole() {
        //Attributes attributes = new Attributes();
        //AnsiConsole.systemInstall();
        Terminal terminal = null;
        try {
            terminal = TerminalBuilder.builder()
                                           .streams(System.in, new PrintStream(new AnsiOutputStream(System.out), true, "UTF-8"))
                                           .jansi(true)
                                           .jna(true)
                                           .encoding(StandardCharsets.UTF_8)
                                           .name(
                                               "CloudNet-Terminal")
                                           //.attributes(attributes)
                                           .system(true)
                                           .dumb(true)
                                           .signalHandler(this.signalManager)
                                           .build();
        } catch (IOException e) {
            this.cloudLogger.log(Level.SEVERE, "Something went wrong on creating a virtual terminal", e);
        }
        if (this.consoleInputDispatch != null && terminal != null) {
            final ArgumentCompleter argumentCompleter = new ArgumentCompleter(new CloudNetCompleter(this.consoleInputDispatch));
            argumentCompleter.setStrict(false);
            argumentCompleter.setStrictCommand(false);
            final DefaultParser defaultParser = new DefaultParser();
            defaultParser.setEofOnUnclosedBracket(DefaultParser.Bracket.ANGLE, DefaultParser.Bracket.CURLY, DefaultParser.Bracket.ROUND, DefaultParser.Bracket.SQUARE);
            this.lineReader = LineReaderBuilder.builder()
                                               .appName("CloudNet-Console")
                                               .option(LineReader.Option.ERASE_LINE_ON_FINISH, true)
                                               .option(LineReader.Option.INSERT_BRACKET, true)
                                               .option(LineReader.Option.GROUP, true)
                                               .option(LineReader.Option.LIST_PACKED, true)
                                               .highlighter(new DefaultHighlighter())
                                               .expander(new DefaultExpander())
                                               .history(new DefaultHistory())
                                               .variable(LineReader.HISTORY_FILE, Paths.get(".cn_history"))
                                               .terminal(terminal)
                                               .parser(defaultParser)
                                               .completer(argumentCompleter)
                                               .build();
            lineReader.setAutosuggestion(LineReader.SuggestionType.COMPLETER);
        } else {
            System.exit(-1);
            throw new NullPointerException("Console input dispatcher is empty");
        }
    }

    public void changeConsoleInput(Class<? extends ConsoleInputDispatch> clazz) {
        final Optional<ConsoleInputDispatch> console = this.consoleRegistry.getConsole(clazz);
        console.ifPresent(inputDispatch -> this.consoleInputDispatch = inputDispatch);
        if (this.consoleInputDispatch != null) {
            if (this.consoleInputDispatch instanceof ConsoleChangeInputPromote) {
                ((ConsoleChangeInputPromote) this.consoleInputDispatch).changePromote(this.prompt);
            }

        }
    }

    public void startConsole() {
        while (this.running) {
            if (this.consoleInputDispatch != null) {
                try {
                    if (!this.password) {
                        this.consoleInputDispatch.dispatch(this.lineReader.readLine(this.prompt), this.lineReader);
                    } else {
                        this.consoleInputDispatch.dispatch(this.lineReader.readLine(this.prompt, this.passwordMask), this.lineReader);
                    }
                } catch (UserInterruptException e) {
                    System.out.println("User interrupt detected!");
                    System.exit(1);
                }
            }
        }
    }

    public void setPassword(boolean password) {
        this.password = password;
    }

    public void setRunning(final boolean running) {
        this.running = running;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(final String prompt) {
        this.prompt = prompt;
    }

    public char getPasswordMask() {
        return passwordMask;
    }

    public void setPasswordMask(char passwordMask) {
        this.passwordMask = passwordMask;
    }

    public LineReader getLineReader() {
        return lineReader;
    }

    public ConsoleRegistry getConsoleRegistry() {
        return consoleRegistry;
    }
}
