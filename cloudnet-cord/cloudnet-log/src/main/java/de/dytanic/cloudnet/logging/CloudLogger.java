/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.logging;

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
        }catch (Exception ex) {

        }

        if (!Files.exists(Paths.get("local")))
            Files.createDirectory(Paths.get("local"));
        if (!Files.exists(Paths.get("local/logs")))
            Files.createDirectory(Paths.get("local/logs"));

        setLevel(Level.ALL);

        this.reader = new ConsoleReader(System.in, System.out);
        this.reader.setExpandEvents(false);

        /*
        FileLoggerHandler handler = new FileLoggerHandler(new FileFormatter(), "local/logs");
        addHandler(handler);*/

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
                logp(level, "", "", contents);
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

    private class FileFormatter
            extends Formatter {

        private final DateFormat format = new SimpleDateFormat("HH:mm:ss");

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

            return "[" + format.format(System.currentTimeMillis()) + "/" + name + "] " + record.getLevel().getLocalizedName() + ": " +
                    " " + formatMessage(record) + "\n" + builder.toString();
        }

    }

    private class LoggingFormatter
            extends Formatter {

        private final DateFormat format = new SimpleDateFormat("HH:mm:ss");

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

            StringBuilder stringBuilder = new StringBuilder(ConsoleReader.RESET_LINE)
                    .append("[")
                    .append(format.format(System.currentTimeMillis()))
                    .append("/")
                    .append(name)
                    .append("] ")
                    .append(record.getLevel().getName())
                    .append(": ")
                    .append(formatMessage(record))
                    .append("\n").append(builder.substring(0));

            return stringBuilder.substring(0);
        }
    }

    /*
    @Getter
    public class FileLoggerHandler extends Handler {

        private String directory;
        private PrintWriter printWriter;

        public FileLoggerHandler(Formatter formatter, String directory) throws Exception
        {
            super();
            setLevel(Level.INFO);
            this.directory = directory;
            try
            {
                setEncoding(StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            setFormatter(formatter);

            if (Files.exists(Paths.get(directory + "/latest.log")))
            {
                new File(directory + "/latest.log").renameTo(new File(directory + "/latest_" + new SimpleDateFormat("dd_MM_yyyy-HH_mm_ss").format(System.currentTimeMillis()) + ".log"));
            }
            new File(directory + "/latest.log").createNewFile();

            this.printWriter = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(directory + "/latest.log")), StandardCharsets.UTF_8));
        }

        @Override
        public void publish(LogRecord record)
        {
            printWriter.write(getFormatter().format(record));
            printWriter.flush();
        }

        @Override
        public void flush()
        {

        }

        @Override
        public void close() throws SecurityException
        {
            printWriter.close();
            if (Files.exists(Paths.get(directory + "/latest.log")))
            {
                new File(directory + "/latest.log").renameTo(new File(directory + "/latest_" + new SimpleDateFormat("dd_MM_yyyy-HH_mm_ss").format(System.currentTimeMillis()) + ".log"));
            }
        }
    }*/

}