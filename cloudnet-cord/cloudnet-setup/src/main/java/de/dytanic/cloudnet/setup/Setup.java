/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.setup;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.lib.utility.threading.Runnabled;
import jline.console.ConsoleReader;
import lombok.Getter;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Tareko on 21.10.2017.
 */
public class Setup implements ISetup {

    private static final String CANCEL = "cancel";

    private final Queue<SetupRequest> requests = new LinkedBlockingQueue<>();

    private final Document document = new Document();

    private ISetupComplete setupComplete = null;

    private ISetupCancel setupCancel = null;

    @Override
    public void start(ConsoleReader consoleReader)
    {
        SetupRequest setupRequest = null;
        while (!requests.isEmpty())
        {
            if (setupRequest == null)
            {
                setupRequest = requests.poll();
            }
            System.out.print(setupRequest.getQuestion() + " | " + setupRequest.getResponseType().toString());

            String input = null;
            try
            {
                input = consoleReader.readLine();
            } catch (Exception ex)
            {
            }

            if (input.equalsIgnoreCase(CANCEL))
            {
                if (setupCancel != null) setupCancel.cancel();
                return;
            }

            if (!input.isEmpty() && !input.equals(" "))
            {
                switch (setupRequest.getResponseType())
                {
                    case NUMBER:
                        if(!NetworkUtils.checkIsNumber(input))
                        {
                            System.out.println(setupRequest.getInValidMessage());
                            continue;
                        }
                        if(setupRequest.getValidater() != null)
                        {
                            if(setupRequest.getValidater().doCatch(input))
                            {
                                document.append(setupRequest.getName(), Integer.parseInt(input));
                                setupRequest = null;
                            }
                            else
                            {
                                System.out.println(setupRequest.getInValidMessage());
                                continue;
                            }
                        }
                        else
                        {
                            document.append(setupRequest.getName(), Integer.parseInt(input));
                            setupRequest = null;
                        }
                        break;
                    case BOOL:
                        if (input.equalsIgnoreCase("yes") || (setupRequest.getValidater() != null && setupRequest.getValidater().doCatch(input)))
                        {
                            document.append(setupRequest.getName(), true);
                            setupRequest = null;
                            continue;
                        }
                        if (input.equalsIgnoreCase("no") || (setupRequest.getValidater() != null && setupRequest.getValidater().doCatch(input)))
                        {
                            document.append(setupRequest.getName(), false);
                            setupRequest = null;
                            continue;
                        }

                        System.out.println(setupRequest.getInValidMessage());
                        break;
                    case STRING:
                        if(setupRequest.getValidater() != null)
                        {
                            if(setupRequest.getValidater().doCatch(input))
                            {
                                document.append(setupRequest.getName(), input);
                                setupRequest = null;
                            }
                            else
                            {
                                System.out.println(setupRequest.getInValidMessage());
                                continue;
                            }
                        }
                        else
                        {
                            document.append(setupRequest.getName(), input);
                            setupRequest = null;
                        }
                        break;
                }
            } else
            {
                System.out.println(setupRequest.getInValidMessage());
            }

        }

        if (setupComplete != null) setupComplete.complete(document);

    }

    public Setup request(SetupRequest setupRequest)
    {
        requests.offer(setupRequest);
        return this;
    }

    public Setup setupComplete(ISetupComplete iSetupComplete)
    {
        this.setupComplete = iSetupComplete;
        return this;
    }

    public Setup setupCancel(ISetupCancel iSetupCancel)
    {
        this.setupCancel = iSetupCancel;
        return this;
    }

}