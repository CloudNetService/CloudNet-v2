package eu.cloudnetservice.cloudnet.v2.command;

import eu.cloudnetservice.cloudnet.v2.console.ConsoleManager;
import eu.cloudnetservice.cloudnet.v2.console.model.ConsoleChangeInputPromote;
import eu.cloudnetservice.cloudnet.v2.console.model.ConsoleInputDispatch;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Class that manages commands for the interfaces of CloudNet.
 */
public final class CommandManager implements ConsoleInputDispatch, ConsoleChangeInputPromote {

    private final Map<String, Command> commands = new ConcurrentHashMap<>();
    private final ConsoleCommandSender consoleSender = new ConsoleCommandSender();
    private final ConsoleManager consoleManager;
    private boolean showDescription = true;
    private boolean aliases = true;
    private final Consumer<CommandManager> defaultCommandPromote;

    /**
     * Constructs a new command manager with a {@link ConsoleCommandSender} and
     * no commands.
     *
     * @param consoleManager for tab completion
     */
    public CommandManager(ConsoleManager consoleManager, Consumer<CommandManager> defaultCommandPromote) {
        this.consoleManager = consoleManager;
        this.defaultCommandPromote = defaultCommandPromote;
    }

    /**
     * Clears all the commands that are currently registered.
     *
     * @return the command manager this was called on, allows for chaining
     */
    public CommandManager clearCommands() {
        commands.clear();
        return this;
    }

    /**
     * Register a new command and all of its aliases to this command manager.
     *
     * @param command the command to register
     *
     * @return the command manager this was called on, allows for chaining
     */
    public CommandManager registerCommand(Command command) {
        if (command == null) {
            return this;
        }

        this.commands.put(command.getName().toLowerCase(), command);

        if (command.getAliases().length != 0) {
            for (String aliases : command.getAliases()) {
                commands.put(aliases.toLowerCase(), command);
            }
        }

        return this;
    }

    /**
     * Get the registered commands.
     *
     * @return a set containing all the registered command names and aliases
     */
    public Set<String> getCommands() {
        return commands.keySet();
    }

    public ConsoleCommandSender getConsoleSender() {
        return consoleSender;
    }

    /**
     * Parses the given {@code command} from the console and dispatches it using
     * a {@link ConsoleCommandSender}.
     *
     * <ol>
     * <li>Then the {@link Command} is executed with the processed commands</li>
     * <li>Last all arguments are processed again</li>
     * </ol>
     *
     * @param command the command line to parse and dispatch
     *
     * @return whether the command executed successfully
     *
     * @see CommandManager#dispatchCommand(CommandSender, String)
     */
    public boolean dispatchCommand(String command) {
        return dispatchCommand(consoleSender, command);
    }

    /**
     * Parses the given {@code command} and dispatches it using the
     * given {@code sender}.
     *
     * <ol>
     * <li>Then the {@link Command} is executed with the processed commands</li>
     * <li>Last all arguments are processed again</li>
     * </ol>
     *
     * @param sender  the sender to execute the command as
     * @param command the command line to parse and dispatch
     *
     * @return whether the command executed successfully
     */
    public boolean dispatchCommand(CommandSender sender, String command) {
        String[] a = command.split(" ");
        if (this.commands.containsKey(a[0].toLowerCase())) {
            ParsedLine parse = this.consoleManager.getLineReader().getParser().parse(command, 0, Parser.ParseContext.SPLIT_LINE);
            this.commands.get(a[0].toLowerCase()).onExecuteCommand(sender, parse);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the command for a given name.
     *
     * @param name the name to get the command for
     *
     * @return the command, if there is one with the given {@code name} or alias
     * or {@code null}, if no command matches the {@code name}
     */
    public Command getCommand(String name) {
        return commands.get(name.toLowerCase());
    }

    @Override
    public void dispatch(final String line, final LineReader lineReader) {
        if (line.length() > 0) {
            final ParsedLine parsedLine = lineReader.getParser().parse(line, 0, Parser.ParseContext.SPLIT_LINE);
            final String command = parsedLine.words().get(0).toLowerCase();
            if (this.commands.containsKey(command)) {
                try {
                    this.commands.get(command).onExecuteCommand(consoleSender, parsedLine);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                System.out.println("Command not found. Use the command \"§ehelp§r\" for further information!");
            }
        }

    }

    @Override
    public boolean history() {
        return true;
    }

    @Override
    public Collection<Candidate> get() {
        Collection<Candidate> strings = new ArrayList<>();
        if (this.consoleManager != null && this.consoleManager.getLineReader() != null) {
            final String buffer = this.consoleManager.getLineReader().getBuffer().toString();
            if (buffer.length() > 0) {
                final ParsedLine parse = this.consoleManager.getLineReader().getParser().parse(buffer, 0, Parser.ParseContext.SPLIT_LINE);
                if (parse.words().size() >= 1) {
                    Command command = getCommand(parse.words().get(0));
                    if (command == null) {
                        strings.addAll(this.commands.values().stream().filter(command1 -> command1.name.startsWith(buffer)).map(command1 -> new Candidate(command1.name, command1.name, command1.name, this.showDescription ? command1.description : null, null, null, true)).collect(
                            Collectors.toList()));
                        if (aliases)
                        this.commands.values().forEach(command1 -> strings.addAll(Arrays.stream(command1.aliases).filter(s -> s.startsWith(buffer))
                                                                                        .map(s -> new Candidate(s,
                                                                                                                s,
                                                                                                                command1.name,
                                                                                                                this.showDescription ? command1.description : null,
                                                                                                                null,
                                                                                                                null,
                                                                                                                true))
                                                                                        .collect(Collectors.toList())));
                    }
                    if (command instanceof TabCompletable) {
                        TabCompletable tabCompletable = (TabCompletable) command;
                        String lastWord = parse.words().get(parse.words().size() - 1);
                        if (lastWord.isEmpty()) {
                            final List<Candidate> onTab = tabCompletable.onTab( parse).stream().map(candidate -> new Candidate(
                                candidate.value(), candidate.displ(), candidate.group(), this.showDescription ? candidate.descr() : null,
                                candidate.suffix(), candidate.key(), candidate.complete())).collect(Collectors.toList());
                            for (Candidate argument : onTab) {
                                if (argument != null && argument.value().toLowerCase().startsWith(lastWord.toLowerCase())) {
                                    strings.add(argument);
                                }
                            }
                        } else {
                            strings.addAll(tabCompletable.onTab(parse).stream().map(candidate -> new Candidate(
                                candidate.value(), candidate.displ(), candidate.group(), this.showDescription ? candidate.descr() : null,
                                candidate.suffix(), candidate.key(), candidate.complete())).collect(Collectors.toList()));
                        }
                    }
                }
            } else {
                strings.addAll(this.commands.values().stream().map(command -> new Candidate(command.name, command.name, command.name,
                                                                                            this.showDescription ? command.description: null, null, null, true)).collect(
                    Collectors.toList()));
                if (aliases) this.commands.values().forEach(command -> strings.addAll(Arrays.stream(command.aliases)
                                                                               .map(s -> new Candidate(s,
                                                                                                       s,
                                                                                                       command.name,
                                                                                                       this.showDescription ?command.description:null,
                                                                                                       null,
                                                                                                       null,
                                                                                                       true))
                                                                               .collect(Collectors.toList())));
            }
        }

        return strings;
    }

    public void setShowDescription(final boolean showDescription) {
        this.showDescription = showDescription;
    }

    public boolean isShowDescription() {
        return showDescription;
    }

    public boolean isAliases() {
        return aliases;
    }

    public void setAliases(final boolean aliases) {
        this.aliases = aliases;
    }

    @Override
    public void changePromote(String oldPromote) {
        this.defaultCommandPromote.accept(this);
    }
}
