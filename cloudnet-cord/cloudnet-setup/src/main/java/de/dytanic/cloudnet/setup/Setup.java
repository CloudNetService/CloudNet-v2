/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.setup;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.utility.document.Document;
import jline.console.ConsoleReader;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

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
    private Consumer<Document> setupComplete = null;

    /**
     * Method that called when this setup sequence is cancelled.
     */
    private Runnable setupCancel = null;

    @Override
    public void start(ConsoleReader consoleReader) {
        boolean successful = true;
        while (!requests.isEmpty()) {
            SetupRequest setupRequest = null;
            if (successful) {
                setupRequest = requests.poll();
            }
            if (setupRequest == null) {
                return;
            }
            SetupResponseType<?> responseType = setupRequest.getResponseType();
            System.out.println(String.format("%s | %s", setupRequest.getQuestion(), responseType.userFriendlyString()));

            String input;

            try {
                input = consoleReader.readLine();
            } catch (Exception ex) {
                System.out.println("Error while reading input: " + ex.getLocalizedMessage());
                setupCancel.run();
                return;
            }

            if (input.equalsIgnoreCase(CANCEL)) {
                if (setupCancel != null) {
                    setupCancel.run();
                }
                return;
            }

            if (!input.isEmpty() && !input.equals(NetworkUtils.SPACE_STRING)) {
                if (responseType.isValidInput(input)) {
                    if ((setupRequest.hasValidator() &&
                        setupRequest.getValidator().test(input)) ||
                        !setupRequest.hasValidator()) {
                        successful = true;
                        responseType.appendDocument(document, setupRequest.getName(), input);
                    } else {
                        successful = false;
                        System.out.println(setupRequest.getInvalidMessage());
                    }
                }
            } else {
                successful = false;
                System.out.println(setupRequest.getInvalidMessage());
            }
        }

        if (setupComplete != null) {
            setupComplete.accept(document);
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
    public Setup setupComplete(Consumer<Document> iSetupComplete) {
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
    public Setup setupCancel(Runnable iSetupCancel) {
        this.setupCancel = iSetupCancel;
        return this;
    }

}
