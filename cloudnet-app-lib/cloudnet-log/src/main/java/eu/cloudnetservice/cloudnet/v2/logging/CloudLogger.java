/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.logging;

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.logging.handler.ICloudLoggerHandler;
import jline.console.ConsoleReader;

import java.io.*;
import java.nio.ByteBuffer;
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

    private static final String SEPARATOR = System.lineSeparator();
    private ConsoleReader reader;
    private final String name = System.getProperty("user.name");

    private final List<ICloudLoggerHandler> handler = new LinkedList<>();

    private boolean showPrompt = !Boolean.getBoolean("cloudnet.logging.prompt.disabled");
    private Handler loggingHandler;

    /**
     * Constructs a new cloud logger instance that handles logging messages from
     * all sources in an asynchronous matter.
     *
     */
    public CloudLogger() {
        super("CloudNetServerLogger", null);
        try {
            if (!Files.exists(Paths.get("local"))) {
                Files.createDirectory(Paths.get("local"));
            }
            if (!Files.exists(Paths.get("local", "logs"))) {
                Files.createDirectory(Paths.get("local", "logs"));
            }

            setLevel(Level.ALL);
            this.reader = new ConsoleReader(System.in, System.out);
            this.reader.setExpandEvents(false);

            final LoggingFormatter formatter = new LoggingFormatter();
            FileHandler fileHandler;

            fileHandler = new FileHandler("local/logs/cloudnet.log", 8000000, 8, false);
            fileHandler.setEncoding(StandardCharsets.UTF_8.name());
            fileHandler.setFormatter(formatter);

            addHandler(fileHandler);

            loggingHandler = new LoggingHandler(reader, this);
            loggingHandler.setFormatter(formatter);

            loggingHandler.setEncoding(StandardCharsets.UTF_8.name());
            loggingHandler.setLevel(Level.INFO);
            addHandler(loggingHandler);

            System.setOut(new PrintStream(new LoggingOutputStream(this, Level.INFO)));
            System.setErr(new PrintStream(new LoggingOutputStream(this, Level.SEVERE)));

            this.reader.setPrompt(NetworkUtils.EMPTY_STRING);
            this.reader.resetPromptLine(NetworkUtils.EMPTY_STRING, "", 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isShowPrompt() {
        return showPrompt;
    }

    public void setShowPrompt(boolean showPrompt) {
        this.showPrompt = showPrompt;
    }

    public boolean isDebugging() {
        return loggingHandler.getLevel().intValue() <= Level.ALL.intValue();
    }

    public void setDebugging(boolean debugging) {
        if (debugging) {
            loggingHandler.setLevel(Level.ALL);
        } else {
            loggingHandler.setLevel(Level.INFO);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public ConsoleReader getReader() {
        return reader;
    }

    public List<ICloudLoggerHandler> getHandler() {
        return handler;
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
            this.reader.flush();
            System.out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Output stream that sends the last message in the buffer to the log handlers
     * when flushed.
     */
    private static class LoggingOutputStream extends OutputStream {
        private static final int LINE_FEED = '\n';

        private final Logger logger;
        private final Level level;
        private final ByteBuffer byteBuffer;

        private boolean newLine = false;

        LoggingOutputStream(final Logger logger, Level level) {
            this.logger = logger;
            this.level = level;
            this.byteBuffer = ByteBuffer.allocate(8192);
        }

        @Override
        public void write(final int b) {
            this.byteBuffer.put((byte) b);
            if (b == LINE_FEED || this.byteBuffer.remaining() == 0) {
                this.newLine = true;
                this.flush();
                this.newLine = false;
            }
        }

        @Override
        public void flush() {
            if (this.newLine) {
                String message = new String(this.byteBuffer.array(), 0, this.byteBuffer.position());
                logger.log(level, message);
                this.byteBuffer.rewind();
            }
            //            String contents = toString(StandardCharsets.UTF_8.name());
            //            int linebreakIndex = contents.indexOf('\n');
            //            if (linebreakIndex != -1) {
            //                byte[] line = new byte[linebreakIndex];
            //                System.arraycopy(this.buf, 0, line, 0, linebreakIndex);
            //                super.reset();
            //                if (!contents.isEmpty() && !contents.equals(separator)) {
            //                    logp(level, NetworkUtils.EMPTY_STRING, NetworkUtils.EMPTY_STRING, contents);
            //                }
            //            }
        }
    }

    /**
     * Handler class that forwards all records to {@link CloudLogger#handler}.
     */
    private static class LoggingHandler extends Handler {

        private final ConsoleReader reader;
        private final CloudLogger logger;

        private boolean closed = false;

        private LoggingHandler(final ConsoleReader reader, final CloudLogger logger) {
            this.reader = reader;
            this.logger = logger;
        }

        @Override
        public void publish(LogRecord record) {
            if (closed) {
                return;
            }

            String formatMessage = getFormatter().formatMessage(record);
            for (ICloudLoggerHandler handler : logger.getHandler()) {
                handler.handleConsole(formatMessage);
            }

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

    private static class LoggingFormatter extends Formatter {

        private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            final String message = formatMessage(record);
            builder.append(ConsoleReader.RESET_LINE)
                   .append('[')
                   .append(dateFormat.format(record.getMillis()))
                   .append("] ")
                   .append(record.getLevel().getName())
                   .append(": ")
                   .append(message);

            if (!message.endsWith(SEPARATOR)) {
                builder.append(SEPARATOR);
            }

            if (record.getThrown() != null) {
                try (StringWriter writer = new StringWriter()) {
                    record.getThrown().printStackTrace(new PrintWriter(writer));
                    builder.append(writer).append(SEPARATOR);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return builder.toString();
        }
    }
}
