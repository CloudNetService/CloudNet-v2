/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.screen;

import de.dytanic.cloudnet.lib.server.screen.ScreenInfo;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import de.dytanic.cloudnetwrapper.network.packet.out.PacketOutSendScreenLine;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Getter
public class ScreenLoader implements Runnable {

    private Thread thread;

    private Screenable screenable;

    public ScreenLoader(Screenable screenable)
    {
        this.screenable = screenable;
    }

    @Override
    public void run()
    {
        this.thread = Thread.currentThread();
        String input;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                screenable.getInstance().getInputStream(), StandardCharsets.UTF_8)
        );
        try
        {
            while ((input = bufferedReader.readLine()) != null)
            {
                CloudNetWrapper.getInstance().getNetworkConnection()
                        .sendPacket(new PacketOutSendScreenLine(Arrays.asList(
                                new ScreenInfo(screenable.getServiceId(), input)
                        )));
            }
        } catch (IOException e)
        {
        }
    }

    public void cancel()
    {
        thread.stop();
    }
}