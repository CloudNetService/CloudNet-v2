package eu.cloudnetservice.cloudnet.v2.wrapper.handlers;

import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;
import eu.cloudnetservice.cloudnet.v2.wrapper.server.BungeeCord;
import eu.cloudnetservice.cloudnet.v2.wrapper.server.GameServer;

public final class StopTimeHandler implements IWrapperHandler {

    @Override
    public void accept(CloudNetWrapper wrapper) {
        for (GameServer gameServer : CloudNetWrapper.getInstance().getServers().values()) {
            try {
                if (!gameServer.isAlive()) {
                    if (System.currentTimeMillis() > (gameServer.getStartupTimeStamp() + 1600)) {
                        gameServer.shutdown();
                    } else {
                        gameServer.restart();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        for (BungeeCord bungeeCord : CloudNetWrapper.getInstance().getProxies().values()) {
            try {
                if (!bungeeCord.isAlive()) {
                    bungeeCord.shutdown();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

}
