/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.setup;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.utility.document.Document;
import jline.console.ConsoleReader;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Builder class for setup sequences.
 */
public class Setup implements ISetup {

    private static final String CANCEL = "cancel";

    /**
     * Queue for setup requests in order of insertion.
     * Builds the sequence.
     */
    private final Queue<SetupRequest> requests = new LinkedBlockingQueue<>();

    /**
     * Document containing the valid answers of the user.
     */
    private final Document document = new Document();

    /**
     * Method that called once this setup sequence has been completed successfully.
     */
    private ISetupComplete setupComplete = null;

    /**
     * Method that called when this setup sequence is cancelled.
     */
    private ISetupCancel setupCancel = null;

    @Override
    public void start(ConsoleReader consoleReader) {
        SetupRequest setupRequest = null;
        while (!requests.isEmpty()) {
            if (setupRequest == null) {
                setupRequest = requests.poll();
            }
            System.out.print(setupRequest.getQuestion() + " | " + setupRequest.getResponseType().toString());

            String input;
            try {
                input = consoleReader.readLine();
            } catch (Exception ex) {
                System.out.println("Error while reading input: " + ex.getLocalizedMessage());
                continue;
            }

            if (input.equalsIgnoreCase(CANCEL)) {
                if (setupCancel != null) {
                    setupCancel.cancel();
                }
                return;
            }

            if (!input.isEmpty() && !input.equals(NetworkUtils.SPACE_STRING)) {
                switch (setupRequest.getResponseType()) {
                    case NUMBER:
                        if (!NetworkUtils.checkIsNumber(input)) {
                            System.out.println(setupRequest.getInValidMessage());
                            continue;
                        }
                        if (setupRequest.getValidater() != null) {
                            if (setupRequest.getValidater().doCatch(input)) {
                                document.append(setupRequest.getName(), Integer.parseInt(input));
                                setupRequest = null;
                            } else {
                                System.out.println(setupRequest.getInValidMessage());
                                continue;
                            }
                        } else {
                            document.append(setupRequest.getName(), Integer.parseInt(input));
                            setupRequest = null;
                        }
                        break;
                    case BOOL:
                        if (input.equalsIgnoreCase("yes") || (setupRequest.getValidater() != null && setupRequest.getValidater().doCatch(
                            input))) {
                            document.append(setupRequest.getName(), true);
                            setupRequest = null;
                            continue;
                        }
                        if (input.equalsIgnoreCase("no") || (setupRequest.getValidater() != null && setupRequest.getValidater().doCatch(
                            input))) {
                            document.append(setupRequest.getName(), false);
                            setupRequest = null;
                            continue;
                        }

                        System.out.println(setupRequest.getInValidMessage());
                        break;
                    case STRING:
                        if (setupRequest.getValidater() != null) {
                            if (setupRequest.getValidater().doCatch(input)) {
                                document.append(setupRequest.getName(), input);
                                setupRequest = null;
                            } else {
                                System.out.println(setupRequest.getInValidMessage());
                                continue;
                            }
                        } else {
                            document.append(setupRequest.getName(), input);
                            setupRequest = null;
                        }
                        break;
                }
            } else {
                System.out.println(setupRequest.getInValidMessage());
            }

        }

        if (setupComplete != null) {
            setupComplete.complete(document);
        }

    }

    /**
     * Add a setup request to this setup sequence.
     *
     * @param setupRequest the setup request to be added
     *
     * @return this setup instance
     */
    public Setup request(SetupRequest setupRequest) {
        requests.offer(setupRequest);
        return this;
    }

    /**
     * Add a method that will be called when this setup sequence completes
     * successfully.
     *
     * @param iSetupComplete the function that will be called, when the setup
     *                       sequence completes successfully.
     *
     * @return this setup instance
     */
    public Setup setupComplete(ISetupComplete iSetupComplete) {
        this.setupComplete = iSetupComplete;
        return this;
    }

    /**
     * Add a method that will be called when this setup sequence is cancelled.
     *
     * @param iSetupCancel the function that will be called, if this setup
     *                     sequence is cancelled.
     *
     * @return this setup instance
     */
    public Setup setupCancel(ISetupCancel iSetupCancel) {
        this.setupCancel = iSetupCancel;
        return this;
    }

}
