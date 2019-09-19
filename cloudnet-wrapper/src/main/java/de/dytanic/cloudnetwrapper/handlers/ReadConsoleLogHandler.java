package de.dytanic.cloudnetwrapper.handlers;

import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import de.dytanic.cloudnetwrapper.screen.AbstractScreenService;
import de.dytanic.cloudnetwrapper.server.BungeeCord;
import de.dytanic.cloudnetwrapper.server.CloudGameServer;
import de.dytanic.cloudnetwrapper.server.GameServer;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class ReadConsoleLogHandler implements IWrapperHandler {

    private final StringBuffer stringBuffer = new StringBuffer();

    private final byte[] buffer = new byte[1024];

    @Override
    public void run(CloudNetWrapper obj) {
        for (CloudGameServer cloudGameServer : obj.getCloudServers().values()) {
            if (cloudGameServer.isAlive() && cloudGameServer.getInstance() != null) {
                readConsoleLog(cloudGameServer);
            }
        }

        for (BungeeCord bungeeCord : obj.getProxys().values()) {
            if (bungeeCord.isAlive() && bungeeCord.getInstance() != null) {
                readConsoleLog(bungeeCord);
            }
        }

        for (GameServer gameServer : obj.getServers().values()) {
            if (gameServer.isAlive() && gameServer.getInstance() != null) {
                readConsoleLog(gameServer);
            }
        }
    }

    private synchronized void readConsoleLog(AbstractScreenService server) {
        if (server.getInstance().isAlive() && server.getInstance().getInputStream() != null) {
            readStream(server, server.getInstance().getInputStream());
            readStream(server, server.getInstance().getErrorStream());
        }
    }

    private synchronized void readStream(AbstractScreenService screenService, InputStream inputStream) {
        try {
            int len;
            while (inputStream.available() > 0 && (len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                stringBuffer.append(new String(buffer, 0, len, StandardCharsets.UTF_8));
            }

            String stringText = stringBuffer.toString();
            if (!stringText.contains("\n") && !stringText.contains("\r")) {
                return;
            }

            for (String input : stringText.split("\r")) {
                for (String text : input.split("\n")) {
                    if (!text.trim().isEmpty()) {
                        screenService.addCachedItem(text);
                    }
                }
            }

            stringBuffer.setLength(0);

        } catch (Exception ignored) {
            stringBuffer.setLength(0);
        }
    }

    @Override
    public int getTicks() {
        return 40;
    }
}
