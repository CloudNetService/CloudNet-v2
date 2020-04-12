package de.dytanic.cloudnetwrapper.server.process;

import de.dytanic.cloudnet.lib.server.ServerProcessMeta;

public class ServerProcess {

    private ServerProcessMeta meta;

    public ServerProcess(ServerProcessMeta meta) {
        this.meta = meta;
    }

    public ServerProcessMeta getMeta() {
        return meta;
    }
}