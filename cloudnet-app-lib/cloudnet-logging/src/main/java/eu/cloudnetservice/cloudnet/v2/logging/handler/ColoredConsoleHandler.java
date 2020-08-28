package eu.cloudnetservice.cloudnet.v2.logging.handler;
import eu.cloudnetservice.cloudnet.v2.logging.color.AnsiColorReplacer;

import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;

public class ColoredConsoleHandler extends ConsoleHandler  {



    @Override
    public void publish(final LogRecord record) {
        record.setMessage(AnsiColorReplacer.replaceAnsi(record.getMessage()));
        super.publish(record);
    }
}
