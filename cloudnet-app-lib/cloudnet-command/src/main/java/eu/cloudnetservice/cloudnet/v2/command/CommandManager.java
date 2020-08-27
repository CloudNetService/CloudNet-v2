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

package eu.cloudnetservice.cloudnet.v2.command;

import eu.cloudnetservice.cloudnet.v2.console.ConsoleManager;
import eu.cloudnetservice.cloudnet.v2.console.model.ConsoleInputDispatch;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.DefaultParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Class that manages commands for the interfaces of CloudNet.
 */
public final class CommandManager implements ConsoleInputDispatch {

    private final Map<String, Command> commands = new ConcurrentHashMap<>();
    private final ConsoleCommandSender consoleSender = new ConsoleCommandSender();
    private final ConsoleManager consoleManager;

    /**
     * Constructs a new command manager with a {@link ConsoleCommandSender} and
     * no commands.
     *
     * @param consoleManager for tab completion
     */
    public CommandManager(final ConsoleManager consoleManager) {
        this.consoleManager = consoleManager;
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
     * <li>First all arguments get processed by the {@link CommandArgument} handlers.</li>
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
     * <li>First all arguments get processed by the {@link CommandArgument} handlers.</li>
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
            String b = command.replace((command.contains(" ") ? command.split(" ")[0] + ' ' : command), NetworkUtils.EMPTY_STRING);
            try {
                for (String argument : a) {
                    for (CommandArgument commandArgument : this.commands.get(a[0].toLowerCase()).getCommandArguments()) {
                        if (commandArgument.getName().equalsIgnoreCase(argument)) {
                            commandArgument.preExecute(this.commands.get(a[0]), command);
                        }
                    }
                }

                if (b.equals(NetworkUtils.EMPTY_STRING)) {
                    this.commands.get(a[0].toLowerCase()).onExecuteCommand(sender, this.consoleManager.getLineReader().getParser().parse(command,0),new String[0]);
                } else {
                    String[] c = b.split(" ");
                    this.commands.get(a[0].toLowerCase()).onExecuteCommand(sender, this.consoleManager.getLineReader().getParser().parse(command,0),c);
                }

                for (String argument : a) {
                    for (CommandArgument commandArgument : this.commands.get(a[0].toLowerCase()).getCommandArguments()) {
                        if (commandArgument.getName().equalsIgnoreCase(argument)) {
                            commandArgument.postExecute(this.commands.get(a[0]), command);
                        }
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
            final ParsedLine parsedLine = lineReader.getParser().parse(line, 0);
            String[] lineArguments = parsedLine.words().toArray(new String[0]);
            if (this.commands.containsKey(lineArguments[0].toLowerCase())) {
                String lineArgumentsWithoutCommand = line.replace((line.contains(" ") ? line.split(" ")[0] + ' ' : line),
                                                                  NetworkUtils.EMPTY_STRING);
                try {
                    for (String argument : lineArguments) {
                        for (CommandArgument commandArgument : this.commands.get(lineArguments[0].toLowerCase()).getCommandArguments()) {
                            if (commandArgument.getName().equalsIgnoreCase(argument)) {
                                commandArgument.preExecute(this.commands.get(lineArguments[0]), line);
                            }
                        }
                    }

                    if (!lineArgumentsWithoutCommand.equals(NetworkUtils.EMPTY_STRING)) {
                        this.commands.get(lineArguments[0].toLowerCase()).onExecuteCommand(consoleSender, null, new String[0]);
                    } else {
                        String[] c = lineArgumentsWithoutCommand.split(" ");
                        this.commands.get(lineArguments[0].toLowerCase()).onExecuteCommand(consoleSender, parsedLine, c);
                    }

                    for (String argument : lineArguments) {
                        for (CommandArgument commandArgument : this.commands.get(lineArguments[0].toLowerCase()).getCommandArguments()) {
                            if (commandArgument.getName().equalsIgnoreCase(argument)) {
                                commandArgument.postExecute(this.commands.get(lineArguments[0]), line);
                            }
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                System.out.println("Command not found. Use the command \"help\" for further information!");
            }
        }

    }

    @Override
    public Collection<Candidate> get() {
        Collection<Candidate> strings = new ArrayList<>();
        if (this.consoleManager != null && this.consoleManager.getLineReader() != null) {
            final String buffer = this.consoleManager.getLineReader().getBuffer().toString();
            if (buffer.length() > 0) {
                final ParsedLine parse = this.consoleManager.getLineReader().getParser().parse(buffer, 0);
                String[] args = buffer.split(" ");
                Command command = getCommand(args[0]);
                if (command == null) {
                    strings.addAll(this.commands.values().stream().filter(command1 -> command1.name.startsWith(buffer)).map(command1 -> new Candidate(command1.name, command1.name, command1.name,
                                                                                                command1.description, null, null, true)).collect(
                        Collectors.toList()));
                    this.commands.values().forEach(command1 -> strings.addAll(Arrays.stream(command1.aliases).filter(s -> s.startsWith(buffer))
                                                                                   .map(s -> new Candidate(s,
                                                                                                           s,
                                                                                                           command1.name,
                                                                                                           command1.description,
                                                                                                           null,
                                                                                                           null,
                                                                                                           true))
                                                                                   .collect(Collectors.toList())));
                }
                if (command instanceof TabCompletable) {
                    TabCompletable tabCompletable = (TabCompletable) command;
                    String testString = args[args.length - 1];
                    if (testString.isEmpty()) {
                        final List<Candidate> onTab = tabCompletable.onTab(args.length - 1, args[args.length - 1], parse,args);
                        if (onTab != null) {
                            for (Candidate argument : onTab) {
                                if (argument != null) {
                                    if (argument.value().toLowerCase().contains(testString.toLowerCase())) {
                                        strings.add(argument);
                                    }
                                }
                            }
                        }
                    } else {
                        strings.addAll(tabCompletable.onTab(args.length - 1, args[args.length - 1],parse, args));
                    }
                }
            } else {
                strings.addAll(this.commands.values().stream().map(command -> new Candidate(command.name, command.name, command.name,
                                                                                            command.description, null, null, true)).collect(
                    Collectors.toList()));
                this.commands.values().forEach(command -> strings.addAll(Arrays.stream(command.aliases)
                                                                               .map(s -> new Candidate(s,
                                                                                                       s,
                                                                                                       command.name,
                                                                                                       command.description,
                                                                                                       null,
                                                                                                       null,
                                                                                                       true))
                                                                               .collect(Collectors.toList())));
            }
        }

        return strings;
    }
}
