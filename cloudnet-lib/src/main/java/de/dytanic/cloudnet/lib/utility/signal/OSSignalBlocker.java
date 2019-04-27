package de.dytanic.cloudnet.lib.utility.signal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import sun.misc.Signal;
import sun.misc.SignalHandler;

public final class OSSignalBlocker {

    public static void initSignalCancel()
    {

        SignalHandler signalHandler = new SignalHandlerImpl();

        try
        {
            Signal.handle(new Signal("TERM"), signalHandler);
        } catch (Exception ex)
        {
        }

        try
        {
            Signal.handle(new Signal("HUP"), signalHandler);
        } catch (Exception ex)
        {
        }

        try
        {
            Signal.handle(new Signal("INT"), signalHandler);
        } catch (Exception ex)
        {
        }

        try
        {
            Signal.handle(new Signal("SIGTERM"), signalHandler);
        } catch (Exception ex)
        {
        }
    }

    private static class SignalHandlerImpl implements SignalHandler {

        @Override
        public void handle(Signal sig)
        {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

            try
            {
                System.out.println("Do you want to terminate the process? Then print 'y' and enter! You should execute the 'stop' command.");
                if (bufferedReader.readLine().equals("y"))
                    System.exit(1);
                else
                    System.out.println("You cancelled the termination!");

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}