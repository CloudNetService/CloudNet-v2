/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.logging;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.logging.handler.ICloudLoggerHandler;
import jline.console.ConsoleReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.*;

/**
 * Custom logger configured for CloudNet.
 */
public class CloudLogger extends Logger {

    private final String separator = System.getProperty("line.separator");
    private final LoggingFormatter formatter = new LoggingFormatter();
    private final ConsoleReader reader;
    private final String name = System.getProperty("user.name");

    private final List<ICloudLoggerHandler> handler = new LinkedList<>();

    private boolean debugging = false;
    private boolean showPrompt = !Boolean.getBoolean("cloudnet.logging.prompt.disabled");

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

        if (!Files.exists(Paths.get("local"))) {
            Files.createDirectory(Paths.get("local"));
        }
        if (!Files.exists(Paths.get("local", "logs"))) {
            Files.createDirectory(Paths.get("local", "logs"));
        }

        setLevel(Level.ALL);

        this.reader = new ConsoleReader(System.in, System.out);
        this.reader.setExpandEvents(false);

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

        this.reader.setPrompt(NetworkUtils.EMPTY_STRING);
        this.reader.resetPromptLine(NetworkUtils.EMPTY_STRING, "", 0);
    }

    public LoggingFormatter getFormatter() {
        return formatter;
    }

    public boolean isShowPrompt() {
        return showPrompt;
    }

    public void setShowPrompt(boolean showPrompt) {
        this.showPrompt = showPrompt;
    }

    public boolean isDebugging() {
        return debugging;
    }

    public void setDebugging(boolean debugging) {
        this.debugging = debugging;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getSeparator() {
        return separator;
    }

    public ConsoleReader getReader() {
        return reader;
    }

    public List<ICloudLoggerHandler> getHandler() {
        return handler;
    }

    /**
     * This posts a new debug message, if {@link #debugging} is true.
     *
     * @param message the message to send to the log
     */
    public void debug(String message) {
        if (debugging) {
            log(Level.WARNING, "[DEBUG] " + message);
        }
    }

    public String readLine(String prompt) {
        try {
            String line = this.reader.readLine(this.showPrompt ? prompt : null);
            this.reader.setPrompt(NetworkUtils.EMPTY_STRING);
            return line;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Shuts down all handlers and the reader.
     */
    public void shutdownAll() {
        for (Handler handler : getHandlers()) {
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
    private class LoggingOutputStream extends ByteArrayOutputStream {
        private final Level level;

        public LoggingOutputStream(Level level) {
            this.level = level;
        }

        @Override
        public void flush() throws IOException {
            String contents = toString(StandardCharsets.UTF_8.name());
            super.reset();
            if (!contents.isEmpty() && !contents.equals(separator)) {
                logp(level, NetworkUtils.EMPTY_STRING, NetworkUtils.EMPTY_STRING, contents);
            }
        }
    }

    /**
     * Handler class that forwards all records to {@link CloudLogger#handler}.
     */
    private class LoggingHandler extends Handler {

        private boolean closed;

        @Override
        public void publish(LogRecord record) {
            if (closed) {
                return;
            }

            String formatMessage = getFormatter().formatMessage(record);
            for (ICloudLoggerHandler handler : CloudLogger.this.getHandler()) {
                handler.handleConsole(formatMessage);
            }

            if (isLoggable(record)) {
                try {
                    reader.print(ConsoleReader.RESET_LINE + getFormatter().format(record));
                    reader.drawLine();
                    reader.flush();
                } catch (Throwable ignored) {
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

    private class LoggingFormatter extends Formatter {

        private final DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            if (record.getThrown() != null) {
                StringWriter writer = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(writer));
                builder.append(writer).append('\n');
            }

            return ConsoleReader.RESET_LINE + "[" + format.format(record.getMillis()) + "] " + record.getLevel()
                                                                                                     .getName() + ": " + formatMessage(
                record) + '\n' + builder.toString();
        }
    }
}
