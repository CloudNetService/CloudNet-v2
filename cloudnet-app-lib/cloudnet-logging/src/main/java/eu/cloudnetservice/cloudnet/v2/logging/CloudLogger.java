package eu.cloudnetservice.cloudnet.v2.logging;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CloudLogger extends Logger {

    private Boolean debugging;

    public CloudLogger() {
        super("CloudLogger", null);
        setLevel(Level.ALL);
        Path logsFolder = Paths.get("local", "logs");
        try {
            Files.createDirectories(logsFolder);
            FileHandler fh = new FileHandler("local/logs/latest.%g.log", 80000, 10, true);
            fh.setFormatter(new LoggingFormatter());
            fh.setEncoding(StandardCharsets.UTF_8.name());
            fh.setLevel(Level.ALL);
            addHandler(fh);
        } catch (IOException e) {
            e.printStackTrace();
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
}
