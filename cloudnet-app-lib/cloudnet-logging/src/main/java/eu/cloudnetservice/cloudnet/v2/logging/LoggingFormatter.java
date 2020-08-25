package eu.cloudnetservice.cloudnet.v2.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LoggingFormatter extends Formatter {

    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @Override
    public String format(final LogRecord record) {
        StringBuilder builder = new StringBuilder();
        if (record != null) {
            String message = formatMessage(record);
            builder.append('[')
                   .append(dateFormat.format(record.getMillis()))
                   .append("] ")
                   .append(record.getLevel().getName())
                   .append(": ")
                   .append(message);

            if (!message.endsWith(System.lineSeparator())) {
                builder.append(System.lineSeparator());
            }

            if (record.getThrown() != null) {
                final StringWriter stringWriter = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(stringWriter));
                builder.append(stringWriter).append(System.lineSeparator());
            }

        }
        return builder.toString();
    }
}
