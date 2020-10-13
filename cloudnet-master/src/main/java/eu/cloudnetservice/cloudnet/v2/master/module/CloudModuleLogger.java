/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
