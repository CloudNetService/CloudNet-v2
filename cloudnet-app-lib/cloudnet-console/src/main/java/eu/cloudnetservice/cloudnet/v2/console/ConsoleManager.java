package eu.cloudnetservice.cloudnet.v2.console;

import eu.cloudnetservice.cloudnet.v2.console.completer.CloudNetCompleter;
import eu.cloudnetservice.cloudnet.v2.console.logging.JlineColoredConsoleHandler;
import eu.cloudnetservice.cloudnet.v2.console.model.ConsoleChangeInputPromote;
import eu.cloudnetservice.cloudnet.v2.console.model.ConsoleInputDispatch;
import eu.cloudnetservice.cloudnet.v2.logging.CloudLogger;
import eu.cloudnetservice.cloudnet.v2.logging.LoggingFormatter;
import eu.cloudnetservice.cloudnet.v2.logging.handler.ColoredConsoleHandler;
import eu.cloudnetservice.cloudnet.v2.logging.stream.LoggingOutputStream;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.AnsiPrintStream;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultExpander;
import org.jline.reader.impl.DefaultHighlighter;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ConsoleManager {

    private boolean password;
    private String prompt;
    private char passwordMask = '*';

    private LineReader lineReader;
    private final ConsoleRegistry consoleRegistry;
    private ConsoleInputDispatch consoleInputDispatch;
    private final SignalManager signalManager;
    private final History history;
    private final Path historyPath = Paths.get(".cn_history");
    private boolean running;
    private Terminal terminal;

    public ConsoleManager(ConsoleRegistry consoleRegistry, SignalManager signalManager) {
        this.consoleRegistry = consoleRegistry;
        this.signalManager = signalManager;
        this.prompt = String.format("%s@Master $ ", System.getProperty("user.name"));
        useDefaultTerminal();
        history = new DefaultHistory();
    }

    public void useDefaultTerminal() {
        try {
            terminal = TerminalBuilder.builder()
                                      .streams(System.in, System.out)
                                      .jansi(true)
                                      .jna(false)
                                      .encoding(StandardCharsets.UTF_8)
                                      .name("CloudNet-Terminal")
                                      .system(true)
                                      .dumb(false)
                                      .signalHandler(this.signalManager)
                                      .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void useDefaultConsole() {

        if (terminal != null) {
            final DefaultParser defaultParser = new DefaultParser();
            defaultParser.setEofOnUnclosedBracket(DefaultParser.Bracket.ANGLE, DefaultParser.Bracket.CURLY, DefaultParser.Bracket.ROUND, DefaultParser.Bracket.SQUARE);
            this.lineReader = LineReaderBuilder.builder()
                                               .appName("CloudNet-Console")
                                               .option(LineReader.Option.INSERT_BRACKET, true)
                                               .option(LineReader.Option.GROUP, true)
                                               .option(LineReader.Option.AUTO_FRESH_LINE, true)
                                               .option(LineReader.Option.LIST_PACKED, true)
                                               .option(LineReader.Option.HISTORY_IGNORE_SPACE, true)
                                               .option(LineReader.Option.AUTO_REMOVE_SLASH, true)
                                               .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
                                               .highlighter(new DefaultHighlighter())
                                               .expander(new DefaultExpander())
                                               .terminal(terminal)
                                               .parser(defaultParser)
                                               .build();
            lineReader.setAutosuggestion(LineReader.SuggestionType.COMPLETER);

        } else {
            System.exit(-1);
            throw new IllegalStateException("Console input dispatcher is empty");
        }
        Logger logger = Logger.getLogger("CloudLogger");
        for (final Handler handler : logger.getHandlers()) {
            if (handler instanceof ColoredConsoleHandler) {
                logger.removeHandler(handler);
            }
        }
        JlineColoredConsoleHandler consoleHandler = new JlineColoredConsoleHandler(this.lineReader);
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(new LoggingFormatter());
        try {
            consoleHandler.setEncoding(StandardCharsets.UTF_8.name());
            logger.addHandler(consoleHandler);
        } catch (UnsupportedEncodingException e) {
            System.exit(-1);
            throw new IllegalStateException("Something goes wrong to set the console encoding");
        }
    }

    public void changeConsoleInput(Class<? extends ConsoleInputDispatch> clazz) {
        final Optional<ConsoleInputDispatch> console = this.consoleRegistry.getConsole(clazz);
        console.ifPresent(inputDispatch -> this.consoleInputDispatch = inputDispatch);
        if (this.consoleInputDispatch != null) {
            if (this.consoleInputDispatch instanceof ConsoleChangeInputPromote) {

                ((ConsoleChangeInputPromote) this.consoleInputDispatch).changePromote(this.prompt);

            }
            if (this.lineReader != null) {
                LineReaderImpl lineReader = (LineReaderImpl) this.lineReader;
                final ArgumentCompleter argumentCompleter = new ArgumentCompleter(new CloudNetCompleter(this.consoleInputDispatch));
                argumentCompleter.setStrict(false);
                argumentCompleter.setStrictCommand(false);

                lineReader.setCompleter(argumentCompleter);
                if (this.consoleInputDispatch.history()) {

                    lineReader.variable(LineReader.HISTORY_FILE, historyPath);
                    lineReader.setHistory(this.history);
                } else {
                    lineReader.variable(LineReader.HISTORY_FILE, null);
                    lineReader.setHistory(new DefaultHistory());
                }
            }
        }
    }

    public void startConsole() {
        while (this.running && !Thread.interrupted()) {
            if (this.consoleInputDispatch != null) {
                try {
                    if (!this.password) {
                        this.consoleInputDispatch.dispatch(this.lineReader.readLine(this.prompt).trim(), this.lineReader);
                    } else {
                        this.consoleInputDispatch.dispatch(this.lineReader.readLine(this.prompt, this.passwordMask).trim(), this.lineReader);
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

    public SignalManager getSignalManager() {
        return signalManager;
    }
}
