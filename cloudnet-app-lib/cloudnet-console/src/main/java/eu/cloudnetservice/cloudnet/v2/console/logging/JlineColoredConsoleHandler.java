package eu.cloudnetservice.cloudnet.v2.console.logging;

import eu.cloudnetservice.cloudnet.v2.logging.color.AnsiColorReplacer;
import eu.cloudnetservice.cloudnet.v2.logging.handler.ColoredConsoleHandler;
import org.jline.reader.LineReader;

import java.util.logging.LogRecord;

public class JlineColoredConsoleHandler extends ColoredConsoleHandler {

    private final LineReader lineReader;

    public JlineColoredConsoleHandler(final LineReader lineReader) {
        this.lineReader = lineReader;
    }

    @Override
    public void publish(final LogRecord record) {
        record.setMessage(AnsiColorReplacer.replaceAnsi(record.getMessage()));
        this.lineReader.printAbove(getFormatter().format(record));
    }
}
