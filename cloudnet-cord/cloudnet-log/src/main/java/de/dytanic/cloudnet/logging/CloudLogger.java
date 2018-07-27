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
import java.util.logging.*;

/**
 * Created by Tareko on 21.05.2017.
 */
@Getter
public class CloudLogger
        extends Logger {

    private final String separator = System.getProperty("line.separator");
    private final LoggingFormatter formatter = new LoggingFormatter();
    private final ConsoleReader reader;
    private final String name = System.getProperty("user.name");

    private final java.util.List<ICloudLoggerHandler> handler = new LinkedList<>();

    @Setter
    private boolean debugging = false;

    public CloudLogger() throws Exception
    {
        super("CloudNetServerLogger", null);

        try
        {
            Field field = Charset.class.getDeclaredField("defaultCharset");
            field.setAccessible(true);
            field.set(null, Charset.forName("UTF-8"));
        } catch (Exception ex)
        {

        }

        if (!Files.exists(Paths.get("local")))
            Files.createDirectory(Paths.get("local"));
        if (!Files.exists(Paths.get("local/logs")))
            Files.createDirectory(Paths.get("local/logs"));

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

    }

    public void debug(String message)
    {
        if (debugging)
            log(Level.WARNING, "[DEBUG] " + message);
    }

    public void shutdownAll()
    {
        for (Handler handler : getHandlers()) handler.close();
        try
        {
            this.reader.killLine();
        } catch (IOException e)
        {
        }
    }

    @RequiredArgsConstructor
    private class LoggingOutputStream extends ByteArrayOutputStream {
        /*========================================================================*/
        private final Level level;

        @SuppressWarnings("deprecation")
        @Override
        public void flush() throws IOException
        {
            String contents = toString(StandardCharsets.UTF_8.name());
            super.reset();
            if (!contents.isEmpty() && !contents.equals(separator))
                logp(level, NetworkUtils.EMPTY_STRING, NetworkUtils.EMPTY_STRING, contents);
        }
    }

    private class LoggingHandler
            extends Handler {

        @Override
        public void publish(LogRecord record)
        {
            String formatMessage = getFormatter().formatMessage(record);
            for (ICloudLoggerHandler handler : CloudLogger.this.getHandler()) handler.handleConsole(formatMessage);

            if (isLoggable(record)) handle(getFormatter().format(record));
        }

        public void handle(String message)
        {
            AsyncPrintStream.ASYNC_QUEUE.offer(new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        reader.print(ConsoleReader.RESET_LINE + message);
                        reader.drawLine();
                        reader.flush();
                    } catch (Exception ex)
                    {
                    }
                }
            });
        }

        @Override
        public void flush()
        {
        }

        @Override
        public void close() throws SecurityException
        {
        }
    }

    private class LoggingFormatter
            extends Formatter {

        private final DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        @Override
        public String format(LogRecord record)
        {
            StringBuilder builder = new StringBuilder();
            if (record.getThrown() != null)
            {
                StringWriter writer = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(writer));
                builder.append(writer).append("\n");
            }

            return ConsoleReader.RESET_LINE +
                    "[" +
                    format.format(record.getMillis()) +
                    "] " +
                    record.getLevel().getName() +
                    ": " +
                    formatMessage(record) +
                    "\n" + builder.toString();
        }
    }
}