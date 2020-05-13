package eu.cloudnetservice.v2.wrapper.server.process;

import eu.cloudnetservice.v2.lib.server.ServerProcessMeta;

public class ServerProcess {

    private ServerProcessMeta meta;

    public ServerProcess(ServerProcessMeta meta) {
        this.meta = meta;
    }

    public ServerProcessMeta getMeta() {
        return meta;
    }
}