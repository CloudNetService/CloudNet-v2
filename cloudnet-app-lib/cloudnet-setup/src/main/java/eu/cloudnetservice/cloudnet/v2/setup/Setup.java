/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.setup;

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
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
                if (setupCancel != null) {
                    setupCancel.run();
                }
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
