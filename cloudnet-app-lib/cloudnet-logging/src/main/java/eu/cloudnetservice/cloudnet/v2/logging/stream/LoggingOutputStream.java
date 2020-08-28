package eu.cloudnetservice.cloudnet.v2.logging.stream;

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.logging.CloudLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class LoggingOutputStream extends ByteArrayOutputStream {

    private final CloudLogger cloudLogger;
    private final Level level;

    public LoggingOutputStream(final CloudLogger cloudLogger, final Level level) {
        this.cloudLogger = cloudLogger;
        this.level = level;
    }

    @Override
    public void flush() throws IOException {
        String contents = toString(StandardCharsets.UTF_8.name());
        super.reset();
        if (!contents.isEmpty() && !contents.equals(System.getProperty("line.separator"))) {
            cloudLogger.logp(level, NetworkUtils.EMPTY_STRING, NetworkUtils.EMPTY_STRING, contents);
        }
    }
}
