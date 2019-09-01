/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.server.process;

import de.dytanic.cloudnet.lib.server.ServerProcessMeta;
import de.dytanic.cloudnetwrapper.server.ServerStage;

public class ServerProcess {

    private ServerProcessMeta meta;

    private ServerStage serverStage;

    public ServerProcess(ServerProcessMeta meta, ServerStage serverStage) {
        this.meta = meta;
        this.serverStage = serverStage;
    }

    public ServerProcessMeta getMeta() {
        return meta;
    }

    public ServerStage getServerStage() {
        return serverStage;
    }

    public void setServerStage(ServerStage serverStage) {
        this.serverStage = serverStage;
    }
}