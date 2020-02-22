package de.dytanic.cloudnetwrapper.handlers;

import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import de.dytanic.cloudnetwrapper.server.BungeeCord;
import de.dytanic.cloudnetwrapper.server.GameServer;

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
