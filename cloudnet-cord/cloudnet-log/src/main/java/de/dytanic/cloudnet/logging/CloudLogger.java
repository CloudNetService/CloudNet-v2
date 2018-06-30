/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.logging;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.logging.handler.ICloudLoggerHandler;
import jline.console.ConsoleReader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.*;

/**
 * Custom logger configured for CloudNet.
 */
@Getter
public class CloudLogger
        extends Logger {

    private final String separator = System.getProperty("line.separator");
    private final LoggingFormatter formatter = new LoggingFormatter();
    private final ConsoleReader reader;
    private final String name = System.getProperty("user.name");

    private final List<ICloudLoggerHandler> handler = new LinkedList<>();

    @Setter
    private boolean debugging = false;

    /**
     * Constructs a new cloud logger instance that handles logging messages from
     * all sources in an asynchronous matter.
     *
     * @throws IOException            when creating directories or files in {@code local/}
     *                                was not possible
     * @throws NoSuchFieldException   when the default charset could not be set
     * @throws IllegalAccessException when the default charset could not be set
     */
    public CloudLogger() throws IOException, NoSuchFieldException, IllegalAccessException {
        super("CloudNetServerLogger", null);
        Field field = Charset.class.getDeclaredField("defaultCharset");
        field.setAccessible(true);
        field.set(null, StandardCharsets.UTF_8);

        if (!Files.exists(Paths.get("local")))
            Files.createDirectory(Paths.get("local"));
        if (!Files.exists(Paths.get("local", "logs")))
            Files.createDirectory(Paths.get("local", "logs"));

        setLevel(Level.ALL);

        this.reader = new ConsoleReader(System.in, System.out);
        this.reader.setExpandEvents(false);

        FileLoggerHandler handler = new FileLoggerHandler(new FileFormatter(), "local/logs");
        addHandler(handler);

        FileHandler fileHandler = new FileHandler("local/logs/cloudnet.log", 8000000, 8, true);
        fileHandler.setEncoding(StandardCharsets.UTF_8.name());
        fileHandler.setFormatter(new LoggingFormatter());

        addHandler(fileHandler);

        LoggingHandler loggingHandler = new LoggingHandler();
        loggingHandler.setFormatter(formatter);
        loggingHandler.setEncoding(StandardCharsets.UTF_8.name());
        loggingHandler.setLevel(Level.INFO);
        addHandler(loggingHandler);

        System.setOut(new AsyncPrintStream(new LoggingOutputStream(Level.INFO)));
        System.setErr(new AsyncPrintStream(new LoggingOutputStream(Level.SEVERE)));
    }

    /**
     * This posts a new debug message, if {@link #debugging} is true.
     *
     * @param message the message to send to the log
     */
    public void debug(String message) {
        if (debugging)
            log(Level.WARNING, "[DEBUG] " + message);
    }

    /**
     * Shuts down all handlers and the reader.
     */
    public void shutdownAll() {
        for (Handler handler: getHandlers()) {
            handler.close();
        }
        try {
            this.reader.killLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Output stream that sends the last message in the buffer to the log handlers
     * when flushed.
     */
    @RequiredArgsConstructor
    private class LoggingOutputStream extends ByteArrayOutputStream {
        private final Level level;

        @Override
        public void flush() throws IOException {
            String contents = toString(StandardCharsets.UTF_8.name());
            super.reset();
            if (!contents.isEmpty() && !contents.equals(separator))
                logp(level, NetworkUtils.EMPTY_STRING, NetworkUtils.EMPTY_STRING, contents);
        }
    }

    /**
     * Handler class that forwards all records to {@link CloudLogger#handler}.
     */
    private class LoggingHandler extends Handler {

        private boolean closed;

        @Override
        public void publish(LogRecord record) {
            if (closed) return;

            String formatMessage = getFormatter().formatMessage(record);
            for (ICloudLoggerHandler handler: CloudLogger.this.getHandler())
                handler.handleConsole(formatMessage);

            if (isLoggable(record)) {
                try {
                    reader.print(ConsoleReader.RESET_LINE + getFormatter().format(record));
                    reader.drawLine();
                    reader.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
            closed = true;
        }
    }

    private class FileFormatter
            extends Formatter {

        private final DateFormat format = new SimpleDateFormat("HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            if (record.getThrown() != null) {
                StringWriter writer = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(writer));
                builder.append(writer).append("\n");
            }

            return "[" + format.format(System.currentTimeMillis()) + NetworkUtils.SLASH_STRING + name + "] " + record.getLevel().getLocalizedName() + ": " +
                    NetworkUtils.SPACE_STRING + formatMessage(record) + "\n" + builder.toString();
        }

    }

    private class LoggingFormatter extends Formatter {

        private final DateFormat format = new SimpleDateFormat("HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            if (record.getThrown() != null) {
                StringWriter writer = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(writer));
                builder.append(writer).append("\n");
            }

            return "\r" + // ConsoleReader.RESET_LINE
                    "[" +
                    format.format(System.currentTimeMillis()) +
                    NetworkUtils.SLASH_STRING +
                    name +
                    "] " +
                    record.getLevel().getName() +
                    ": " +
                    formatMessage(record) +
                    "\n" + builder.substring(0);
        }
    }


    /**
     * Log handler that writes all log records to a log file and archives old files.
     */
    @Getter
    private class FileLoggerHandler extends Handler {

        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy-HH_mm_ss");
        private String directory;
        private PrintWriter printWriter;

        /**
         * Constructs a new handler that writes log records to files in the
         * provided directory.
         *
         * @param formatter the formatter instance to format incoming log records with
         * @param directory the directory to store the the files in
         * @throws IOException when the files couldn't be moved or created
         */
        FileLoggerHandler(Formatter formatter, String directory) throws IOException {
            super();
            setLevel(Level.INFO);
            this.directory = directory;
            try {
                setEncoding(StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            setFormatter(formatter);

            if (Files.exists(Paths.get(directory, "latest.log"))) {
                Files.move(Paths.get(directory, "latest.log"),
                        Paths.get(directory, "latest_" + dateFormat.format(System.currentTimeMillis()) + ".log"),
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            }
            Files.createFile(Paths.get(directory, "latest.log"));

            this.printWriter = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(directory + "/latest.log")), StandardCharsets.UTF_8));
        }

        @Override
        public void publish(LogRecord record) {
            printWriter.write(getFormatter().format(record));
            printWriter.flush();
        }

        @Override
        public void flush() {

        }

        @Override
        public void close() throws SecurityException {
            printWriter.close();
            if (Files.exists(Paths.get(directory, "latest.log"))) {
                try {
                    Files.move(Paths.get(directory, "latest.log"),
                            Paths.get(directory, "latest_" + dateFormat.format(System.currentTimeMillis()) + ".log"),
                            StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
