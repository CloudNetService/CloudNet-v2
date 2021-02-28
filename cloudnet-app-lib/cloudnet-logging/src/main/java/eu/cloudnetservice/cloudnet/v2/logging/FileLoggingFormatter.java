package eu.cloudnetservice.cloudnet.v2.logging;

import eu.cloudnetservice.cloudnet.v2.logging.color.ChatColor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class FileLoggingFormatter extends Formatter {

    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @Override
    public String format(final LogRecord record) {
        StringBuilder builder = new StringBuilder();
        if (record != null) {

            buildMessage(builder, record);

            if (record.getThrown() != null) {
                final StringWriter stringWriter = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(stringWriter));
                builder.append(stringWriter);
                builder.append(System.lineSeparator());
            }

        }
        return builder.toString();
    }

    private void buildMessage(StringBuilder builder, LogRecord record) {
        String message = formatMessage(record);
        builder.append('[');
        builder.append(dateFormat.format(record.getMillis()));
        builder.append("] ");
        builder.append(record.getLevel().getName());
        builder.append(": ");
        builder.append(ChatColor.stripColor(message));
        if (!message.endsWith(System.lineSeparator())) {
            builder.append(System.lineSeparator());
        }
    }

}
