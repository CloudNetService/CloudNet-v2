/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.command;

import de.dytanic.cloudnet.lib.NetworkUtils;
import jline.console.completer.Completer;
import lombok.Getter;

import java.util.List;
import java.util.Set;


@Getter
public final class CommandManager
        implements Completer
{

    private final java.util.Map<String, Command> commands = NetworkUtils.newConcurrentHashMap();
    private ConsoleCommandSender consoleSender = new ConsoleCommandSender();

    public CommandManager() {}

    public CommandManager clearCommands()
    {
        commands.clear();
        return this;
    }

    public CommandManager registerCommand(Command command)
    {
        if (command == null) return this;

        this.commands.put(command.getName().toLowerCase(), command);

        if (command.getAliases().length != 0)
        {
            for (String aliases : command.getAliases())
            {
                commands.put(aliases.toLowerCase(), command);
            }
        }

        return this;
    }

    public Set<String> getCommands()
    {
        return commands.keySet();
    }

    public Command getCommand(String name)
    {
        return commands.get(name.toLowerCase());
    }

    public boolean dispatchCommand(CommandSender sender, String command)
    {
        String[] a = command.split(" ");
        if(this.commands.containsKey(a[0].toLowerCase()))
        {
            String b = command.replace((command.contains(" ") ? command.split(" ")[0] + " " : command), NetworkUtils.EMPTY_STRING);
            try
            {
                if(b.equals(NetworkUtils.EMPTY_STRING))
                {
                    this.commands.get(a[0].toLowerCase()).onExecuteCommand(sender, new String[0]);
                }else{
                    String[] c = b.split(" ");
                    this.commands.get(a[0].toLowerCase()).onExecuteCommand(sender, c);
                }
            }catch
                    (Exception ex)
            {
                ex.printStackTrace();
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean dispatchCommand(String command)
    {
        String[] a = command.split(" ");
        if(this.commands.containsKey(a[0].toLowerCase()))
        {
            String b = command.replace((command.contains(" ") ? command.split(" ")[0] + " " : command), NetworkUtils.EMPTY_STRING);
            try
            {

                for(String argument : a)
                {
                    for(CommandArgument argumenents : this.commands.get(a[0].toLowerCase()).getCommandArguments())
                    {
                        if(argumenents.getName().equalsIgnoreCase(argument)) argumenents.preExecute(this.commands.get(a[0]), command);
                    }
                }

                if(b.equals(NetworkUtils.EMPTY_STRING))
                {
                    this.commands.get(a[0].toLowerCase()).onExecuteCommand(consoleSender, new String[0]);
                }else{
                    String[] c = b.split(" ");
                    this.commands.get(a[0].toLowerCase()).onExecuteCommand(consoleSender, c);
                }

                for(String argument : a)
                {
                    for(CommandArgument argumenents : this.commands.get(a[0].toLowerCase()).getCommandArguments())
                    {
                        if(argumenents.getName().equalsIgnoreCase(argument)) argumenents.postExecute(this.commands.get(a[0]), command);
                    }
                }

            }catch
                    (Exception ex)
            {
                ex.printStackTrace();
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates)
    {
        String[] input = buffer.split(" ");

        if(input.length == 0) return cursor;

        Command command = getCommand(input[0]);
        if(command != null && command instanceof TabCompleteable)
        {
            List<String> tabCompletions = ((TabCompleteable)command).onTab((input.length - 1), input[input.length - 1]);

            for(String t : tabCompletions)
            {
                candidates.add(t);
            }

            final int lastSpace = buffer.lastIndexOf( ' ' );
            if ( lastSpace == -1 )
            {
                return cursor - buffer.length();
            } else
            {
                return cursor - ( buffer.length() - lastSpace - 1 );
            }
        }
        return cursor;
    }
}
