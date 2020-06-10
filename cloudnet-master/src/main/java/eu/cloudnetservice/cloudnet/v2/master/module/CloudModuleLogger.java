package eu.cloudnetservice.cloudnet.v2.master.module;

import eu.cloudnetservice.cloudnet.v2.master.CloudNet;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Own logger to show from which module the message was sent
 */
public final class CloudModuleLogger extends Logger {

    private final String moduleName;

    /**
     * Protected method to construct a logger for a named subsystem.
     * <p>
     * The logger will be initially configured with a null Level
     * and with useParentHandlers set to true.
     *
     * @param module used to refer to the module in messages.
     */
    public CloudModuleLogger(CloudModule module) {
        super(module.getClass().getCanonicalName(), null);
        this.moduleName = module.getModuleJson().getName();
        this.setUseParentHandlers(true);
        this.setParent(CloudNet.getLogger());
    }

    /**
     * Writes all log messages
     * @param record specifies to which message should be sent
     */
    @Override
    public void log(LogRecord record) {
        record.setMessage(String.format("[%s] %s", moduleName, record.getMessage()));
        super.log(record);
    }
}
