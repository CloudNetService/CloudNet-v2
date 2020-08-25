package eu.cloudnetservice.cloudnet.v2.console;

import org.jline.reader.LineReader;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class JLine3ConsoleHandler extends Handler {


    private boolean closed;
    private final LineReader lineReader;

    public JLine3ConsoleHandler(final LineReader lineReader) {
        this.lineReader = lineReader;
    }

    @Override
    public void publish(LogRecord record) {
        if (closed) {
            return;
        }
        if (isLoggable(record)) {
            lineReader.printAbove(getFormatter().format(record));
        }
    }

    @Override
    public void flush() {
        lineReader.getTerminal().flush();
    }

    @Override
    public void close() throws SecurityException {
        closed = true;
    }
}
