package eu.cloudnetservice.cloudnet.v2.master.module;

import eu.cloudnetservice.cloudnet.v2.master.CloudNet;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

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

    @Override
    public void log(final LogRecord record) {
        record.setMessage(String.format("[%s] %s", moduleName, record.getMessage()));
        super.log(record);
    }
}
