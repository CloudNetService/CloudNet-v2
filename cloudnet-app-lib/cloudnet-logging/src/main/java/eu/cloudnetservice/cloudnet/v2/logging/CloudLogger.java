package eu.cloudnetservice.cloudnet.v2.logging;

import eu.cloudnetservice.cloudnet.v2.logging.handler.ColoredConsoleHandler;
import eu.cloudnetservice.cloudnet.v2.logging.stream.LoggingOutputStream;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CloudLogger extends Logger {

    private boolean debugging;

    public CloudLogger() {
        super("CloudLogger", null);
        setLevel(Level.ALL);
        Path logsFolder = Paths.get("local", "logs");
        try {
            Files.createDirectories(logsFolder);
            FileHandler fh = new FileHandler("local/logs/latest.%g.log", 1600000, 25, true);
            fh.setFormatter(new FileLoggingFormatter());
            fh.setEncoding(StandardCharsets.UTF_8.name());
            fh.setLevel(Level.ALL);
            addHandler(fh);
            ColoredConsoleHandler consoleHandler = new ColoredConsoleHandler();
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setFormatter(new LoggingFormatter());
            consoleHandler.setEncoding(StandardCharsets.UTF_8.name());
            addHandler(consoleHandler);
            System.setOut(new PrintStream(new LoggingOutputStream(this, Level.INFO), true, StandardCharsets.UTF_8.name()));
            System.setErr(new PrintStream(new LoggingOutputStream(this, Level.SEVERE), true, StandardCharsets.UTF_8.name()));
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE,"Something goes wrong on logger creation", e);
        }
    }

    /**
     * This posts a new debug message, if [.debugging] is true.
     *
     * @param message the message to send to the log
     */
    public void debug(String message) {
        if (debugging) {
            log(Level.WARNING, "[DEBUG] "+message);
        }
    }

    /**
     * Shuts down all handlers and the reader.
     */
    public void shutdownAll() {
        for (Handler handler : getHandlers()) {
            handler.close();
        }
    }

    public void setDebugging(final Boolean debugging) {
        this.debugging = debugging;
    }

    public boolean isDebugging() {
        return debugging;
    }
}
