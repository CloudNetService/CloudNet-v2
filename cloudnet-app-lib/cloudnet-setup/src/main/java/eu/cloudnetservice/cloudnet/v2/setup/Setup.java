package eu.cloudnetservice.cloudnet.v2.setup;

import eu.cloudnetservice.cloudnet.v2.console.ConsoleManager;
import eu.cloudnetservice.cloudnet.v2.console.model.ConsoleChangeInputPromote;
import eu.cloudnetservice.cloudnet.v2.console.model.ConsoleInputDispatch;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * Builder class for setup sequences.
 */
public class Setup implements ConsoleInputDispatch, ConsoleChangeInputPromote {

    private String oldPrompt;
    private final ConsoleManager consoleManager;
    private SetupResponseType<?> responseType;


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
    private SetupRequest setupRequest;

    public Setup(ConsoleManager consoleManager) {
        this.consoleManager = consoleManager;

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

    @Override
    public void dispatch(String line, LineReader lineReader) {
        printQuestion(lineReader);
        if (!line.isEmpty()) {
            if (line.equalsIgnoreCase(CANCEL)) {
                if (setupCancel != null) {
                    this.consoleManager.setPrompt(oldPrompt);
                    setupCancel.run();
                }
                return;
            }
            if (responseType != null && responseType.isValidInput(line)) {
                if (setupRequest.hasValidator() && setupRequest.getValidator().test(line)) {
                    responseType.appendDocument(document, setupRequest.getName(), line);
                    setupRequest = null;
                    printQuestion(lineReader);
                    if (requests.isEmpty() && setupComplete != null) {
                        setupComplete.accept(document);
                        this.consoleManager.setPrompt(oldPrompt);
                    }
                } else {
                    System.out.println(setupRequest.getInvalidMessage());
                }
            }
        }
    }

    private void printQuestion(LineReader lineReader) {
        if (!requests.isEmpty() && setupRequest == null) {
            setupRequest = requests.poll();
            responseType = setupRequest.getResponseType();
            lineReader.printAbove(String.format("%s | %s%n", setupRequest.getQuestion(), responseType.userFriendlyString()));
        }
    }

    @Override
    public Collection<Candidate> get() {
        return new ArrayList<>();
    }

    @Override
    public void changePromote(final String oldPromote) {
        if (!requests.isEmpty() && setupRequest == null) {
            setupRequest = requests.poll();
            responseType = setupRequest.getResponseType();
            this.consoleManager.getLineReader().printAbove(String.format("%s | %s%n", setupRequest.getQuestion(), responseType.userFriendlyString()));
        }
        if (!this.consoleManager.getPrompt().equals(">")) {
            this.oldPrompt = oldPromote;
            this.consoleManager.setPrompt(">");
        }
    }
}
