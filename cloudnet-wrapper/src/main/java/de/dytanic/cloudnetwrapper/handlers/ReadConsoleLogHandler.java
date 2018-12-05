package de.dytanic.cloudnetwrapper.handlers;

import de.dytanic.cloudnet.lib.server.screen.ScreenInfo;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import de.dytanic.cloudnetwrapper.network.packet.out.PacketOutSendScreenLine;
import de.dytanic.cloudnetwrapper.server.BungeeCord;
import de.dytanic.cloudnetwrapper.server.CloudGameServer;
import de.dytanic.cloudnetwrapper.server.GameServer;
import de.dytanic.cloudnetwrapper.server.process.ServerDispatcher;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public final class ReadConsoleLogHandler implements IWrapperHandler {

    private final StringBuffer stringBuffer = new StringBuffer();

    private final byte[] buffer = new byte[1024];

    private int len;

    @Override
    public void run(CloudNetWrapper obj)
    {
        for (CloudGameServer cloudGameServer : obj.getCloudServers().values())
            if (cloudGameServer.isAlive() && cloudGameServer.getInstance() != null)
                readConsoleLog(cloudGameServer);

        for (BungeeCord bungeeCord : obj.getProxys().values())
            if (bungeeCord.isAlive() && bungeeCord.getInstance() != null)
                readConsoleLog(bungeeCord);

        for (GameServer gameServer : obj.getServers().values())
            if (gameServer.isAlive() && gameServer.getInstance() != null)
                readConsoleLog(gameServer);
    }

    private void readConsoleLog(ServerDispatcher server)
    {
        if (server.getInstance().isAlive() && server.getInstance().getInputStream() != null)
        {
            try
            {

                InputStream inputStream = server.getInstance().getInputStream();

                while (inputStream.available() > 0 && (len = inputStream.read(buffer, 0, buffer.length)) != -1)
                    stringBuffer.append(new String(buffer, 0, len, StandardCharsets.UTF_8));

                for (String input : stringBuffer.toString().split("\r"))
                    for (String text : input.split("\n"))
                        if (!text.trim().isEmpty())
                        {
                            CloudNetWrapper.getInstance().getNetworkConnection()
                                    .sendPacket(new PacketOutSendScreenLine(Collections.singletonList(new ScreenInfo(server.getServiceId(), input))));
                        }

            } catch (Exception ignored)
            {
            } finally
            {
                stringBuffer.setLength(0);
            }
        }
    }

    @Override
    public int getTicks()
    {
        return 40;
    }
}