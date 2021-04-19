package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.command.TabCompletable;
import eu.cloudnetservice.cloudnet.v2.lib.user.User;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.setup.user.ChangePasswordSetup;
import eu.cloudnetservice.cloudnet.v2.master.setup.user.CreateUserSetup;
import org.jline.reader.Candidate;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.List;

public final class CommandUser extends Command implements TabCompletable {
    public CommandUser() {
        super("user", "cloudnet.command.user");

        description = "Manages CloudNet internal users";
    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {
        if (parsedLine.words().size() == 1) {
            sender.sendMessage("user create",
                               "user changePassword",
                               "user delete <username> ",
                               "user permission add <username> <permission>",
                               "user permission remove <username> <permission>",
                               "user list");
            return;
        }
        switch (parsedLine.words().size()) {
            case 2: {
                String commandArgument = parsedLine.words().get(1);
                if (commandArgument.equalsIgnoreCase("create")) {
                    CloudNet.getInstance().getConsoleRegistry().registerInput(new CreateUserSetup(sender));
                    CloudNet.getInstance().getConsoleManager().changeConsoleInput(CreateUserSetup.class);
                    return;
                }
                if (commandArgument.equalsIgnoreCase("changePassword")) {
                    CloudNet.getInstance().getConsoleRegistry().registerInput(new ChangePasswordSetup(sender));
                    CloudNet.getInstance().getConsoleManager().changeConsoleInput(ChangePasswordSetup.class);
                    return;
                }
                if (commandArgument.equalsIgnoreCase("list")) {
                    sender.sendMessage("Users:");
                    for (User user : CloudNet.getInstance().getUsers()) {
                        sender.sendMessage(" - §e" + user.getName() + "§r/" + user.getUniqueId() + ':',
                                           "   API-Token: §9" + user.getApiToken(),
                                           "   Permissions: ");
                        for (String permission : user.getPermissions()) {
                            sender.sendMessage("   - " + permission);
                        }
                    }
                    return;
                }
            }
            case 3: {
                String commandArgument = parsedLine.words().get(1);
                if (commandArgument.equalsIgnoreCase("delete")) {
                    String username = parsedLine.words().get(2);
                    User user = CloudNet.getInstance().getUser(username);
                    if (user == null) {
                        sender.sendMessage("§eA user with that name does not exist!");
                        return;
                    }
                    CloudNet.getInstance().getUsers().remove(user);
                    CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
                    sender.sendMessage("§aSuccessfully deleted the user \"" + user.getName() + '"');
                    return;
                }
            }
            case 5: {
                String commandArgument = parsedLine.words().get(1);
                if (commandArgument.equalsIgnoreCase("permission")) {
                    String secondCommandArgument = parsedLine.words().get(2);
                    if (secondCommandArgument.equalsIgnoreCase("add") || secondCommandArgument.equalsIgnoreCase("remove")) {
                        String username = parsedLine.words().get(3);
                        String permission = parsedLine.words().get(4);;
                        User user = CloudNet.getInstance().getUser(username);
                        if (user == null) {
                            sender.sendMessage("§eA user with that name does not exist!");
                            return;
                        }
                        if (secondCommandArgument.equalsIgnoreCase("add")) {
                            if (user.getPermissions().contains(permission)) {
                                sender.sendMessage("§eThe user \"" + user.getName() + "\" already has the permission \"" + permission + '"');
                                return;
                            }
                            user.getPermissions().add(permission);
                            CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
                            sender.sendMessage("§aSuccessfully edited the permissions of the user \"" + user.getName() + '"');
                            return;
                        }
                        if (secondCommandArgument.equalsIgnoreCase("remove")) {
                            if (!user.getPermissions().contains(permission)) {
                                sender.sendMessage("§eThe user \"" + user.getName() + "\" does not have the permission \"" + permission + '"');
                                return;
                            }
                            user.getPermissions().remove(permission);
                            CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
                            sender.sendMessage("§aSuccessfully edited the permissions of the user \"" + user.getName() + '"');
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<Candidate> onTab(final ParsedLine parsedLine) {
        List<Candidate> candidates = new ArrayList<>();
        String command = parsedLine.words().get(0);
        switch (parsedLine.words().size()) {
            case 1: {
                if (command.equalsIgnoreCase("user")) {
                    candidates.add(new Candidate("create", "create", null, "Create a new user", null, null, true));
                    candidates.add(new Candidate("changePassword", "changePassword", null, "Change a user passowrd", null, null, true));
                    candidates.add(new Candidate("list", "list", null, "List all password", null, null, true));
                    candidates.add(new Candidate("permission", "permission", null, "Edit permissions", null, null, true));
                    candidates.add(new Candidate("delete", "delete", null, "Delete a user", null, null, true));
                }
                break;
            }
            case 2: {
                if (command.equalsIgnoreCase("user")) {
                    String commandArgument = parsedLine.words().get(1);
                    if (commandArgument.equalsIgnoreCase("delete")) {
                        for (User user : CloudNet.getInstance().getUsers()) {
                            candidates.add(new Candidate(user.getName(), user.getName(), "user", "A user", null, null, true));
                        }
                        break;
                    }
                    if (commandArgument.equalsIgnoreCase("permission")) {
                        candidates.add(new Candidate("add", "add", "permission", "", null, null, true));
                        candidates.add(new Candidate("remove", "remove", "permission", "", null, null, true));
                        break;
                    }
                }
            }
            case 3: {
                if (command.equalsIgnoreCase("user")) {
                    String commandArgument = parsedLine.words().get(1);
                    if (commandArgument.equalsIgnoreCase("permission")) {
                        String secondCommandArgument = parsedLine.words().get(2);
                        if (secondCommandArgument.equalsIgnoreCase("add") || secondCommandArgument.equalsIgnoreCase("remove")) {
                            for (User user : CloudNet.getInstance().getUsers()) {
                                candidates.add(new Candidate(user.getName(), user.getName(), "user", "A user", null, null, true));
                            }
                            break;
                        }
                    }
                }
            }
            case 4: {
                if (command.equalsIgnoreCase("user")) {
                    String commandArgument = parsedLine.words().get(1);
                    if (commandArgument.equalsIgnoreCase("permission")) {
                        String secondCommandArgument = parsedLine.words().get(2);
                        if (secondCommandArgument.equalsIgnoreCase("add") || secondCommandArgument.equalsIgnoreCase("remove")) {
                            if (secondCommandArgument.equalsIgnoreCase("remove")) {
                                String thirdCommandArgument = parsedLine.words().get(3);
                                User user = CloudNet.getInstance().getUser(thirdCommandArgument);
                                if (user != null) {
                                    for (String userPermission : user.getPermissions()) {
                                        candidates.add(new Candidate(userPermission, userPermission, "permission", "A permission", null, null, true));
                                    }
                                    break;
                                }
                            } else {
                                for (User user : CloudNet.getInstance().getUsers()) {
                                    for (String userPermission : user.getPermissions()) {
                                        candidates.add(new Candidate(userPermission, userPermission, "permission", "A permission", null, null, true));
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        return candidates;
    }
}
