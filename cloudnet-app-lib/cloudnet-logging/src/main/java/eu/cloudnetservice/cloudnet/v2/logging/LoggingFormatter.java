package eu.cloudnetservice.cloudnet.v2.logging;

import org.fusesource.jansi.Ansi;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LoggingFormatter extends Formatter {

    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @Override
    public String format(final LogRecord record) {
        StringBuilder builder = new StringBuilder();
        if (record != null) {

            colorMessage(builder, record);

            if (record.getThrown() != null) {
                final StringWriter stringWriter = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(stringWriter));
                builder.append(stringWriter);
                builder.append(System.lineSeparator());
            }

        }
        return builder.toString();
    }

    private void colorMessage(StringBuilder builder, LogRecord record) {
        String message = formatMessage(record);
        builder.append('[');
        builder.append(dateFormat.format(record.getMillis()));
        builder.append("] ");
        builder.append(colorLevel(record));
        builder.append(": ");
        builder.append(message);
        if (!message.endsWith(System.lineSeparator())) {
            builder.append(System.lineSeparator());
        }
    }

    private String colorLevel(LogRecord logRecord) {
        if (logRecord.getLevel().equals(Level.WARNING)) {
            return Ansi.ansi().fg(Ansi.Color.YELLOW).a(logRecord.getLevel().getName()).reset().toString();
        }
        if (logRecord.getLevel().equals(Level.INFO)) {
            return Ansi.ansi().fgBright(Ansi.Color.BLUE).a(logRecord.getLevel().getName()).reset().toString();
        }
        return logRecord.getLevel().getName();
    }
}
