package eu.cloudnetservice.cloudnet.v2.console.logging;

import eu.cloudnetservice.cloudnet.v2.logging.color.AnsiColorReplacer;
import eu.cloudnetservice.cloudnet.v2.logging.handler.ColoredConsoleHandler;
import org.jline.reader.LineReader;
import org.jline.utils.InfoCmp;

import java.util.logging.LogRecord;

public class JlineColoredConsoleHandler extends ColoredConsoleHandler {

    private final LineReader lineReader;

    public JlineColoredConsoleHandler(final LineReader lineReader) {
        this.lineReader = lineReader;
    }

    @Override
    public void publish(final LogRecord record) {
        record.setMessage(AnsiColorReplacer.replaceAnsi(record.getMessage()));
        this.lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
        this.lineReader.getTerminal().puts(InfoCmp.Capability.clr_eol);
        this.lineReader.getTerminal().writer().print(getFormatter().format(record));
        this.lineReader.getTerminal().writer().flush();
        if (!this.lineReader.isReading()) {
            return;
        }

        this.lineReader.callWidget(LineReader.REDRAW_LINE);
        this.lineReader.callWidget(LineReader.REDISPLAY);
    }
}
