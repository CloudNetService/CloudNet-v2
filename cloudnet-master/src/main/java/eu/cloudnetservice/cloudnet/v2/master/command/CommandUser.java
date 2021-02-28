package eu.cloudnetservice.cloudnet.v2.master.command;
/*
 * Created by derrop on 04.06.2019
 */

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.lib.user.BasicUser;
import eu.cloudnetservice.cloudnet.v2.lib.user.User;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;

public class CommandUser extends Command {
    public CommandUser() {
        super("user", "cloudnet.command.user");

        description = "Manages CloudNet internal users";
    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {

        if (parsedLine.words().size() == 4 && parsedLine.words().get(1).equalsIgnoreCase("create")) {
            if (CloudNet.getInstance().getUser(parsedLine.words().get(2)) != null) {
                sender.sendMessage("A user with that name already exists!");
                return;
            }
            User user = new BasicUser(parsedLine.words().get(2), parsedLine.words().get(3), new ArrayList<>());
            CloudNet.getInstance().getUsers().add(user);
            CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
            sender.sendMessage("Successfully created a new user!");
        } else if (parsedLine.words().size() == 4 && parsedLine.words().get(1).equalsIgnoreCase("changePassword")) {
            User user = CloudNet.getInstance().getUser(parsedLine.words().get(2));
            if (user == null) {
                sender.sendMessage("A user with that name does not exist!");
                return;
            }
            user.setPassword(parsedLine.words().get(3));
            CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
            sender.sendMessage("Successfully changed the password of the user \"" + user.getName() + "\"!");
        } else if (parsedLine.words().size() == 3 && parsedLine.words().get(1).equalsIgnoreCase("delete")) {
            User user = CloudNet.getInstance().getUser(parsedLine.words().get(2));
            if (user == null) {
                sender.sendMessage("A user with that name does not exist!");
                return;
            }
            CloudNet.getInstance().getUsers().remove(user);
            CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
            sender.sendMessage("Successfully deleted the user \"" + user.getName() + '"');
        } else if (parsedLine.words().size() == 2 && parsedLine.words().get(1).equalsIgnoreCase("list")) {
            sender.sendMessage("Users:");
            for (User user : CloudNet.getInstance().getUsers()) {
                sender.sendMessage(" - " + user.getName() + '/' + user.getUniqueId() + ':',
                                   "  API-Token: " + user.getApiToken(),
                                   "  Permissions: ");
                for (String permission : user.getPermissions()) {
                    sender.sendMessage("   - " + permission);
                }
            }
        } else if (parsedLine.words().size() == 5 && parsedLine.words().get(1).equalsIgnoreCase("permission")) {
            User user = CloudNet.getInstance().getUser(parsedLine.words().get(3));
            if (user == null) {
                sender.sendMessage("A user with that name does not exist!");
                return;
            }

            String permission = parsedLine.words().get(4);

            if (parsedLine.words().get(2).equalsIgnoreCase("add")) {
                if (user.getPermissions().contains(permission)) {
                    sender.sendMessage("The user \"" + user.getName() + "\" already has the permission \"" + permission + '"');
                    return;
                }
                user.getPermissions().add(permission);
            } else if (parsedLine.words().get(2).equalsIgnoreCase("remove")) {
                if (!user.getPermissions().contains(permission)) {
                    sender.sendMessage("The user \"" + user.getName() + "\" does not have the permission \"" + permission + '"');
                    return;
                }
                user.getPermissions().remove(permission);
            }
            CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
            sender.sendMessage("Successfully edited the permissions of the user \"" + user.getName() + '"');
        } else {
            sender.sendMessage("user create <name> <password>",
                               "user delete <name>",
                               "user changePassword <name> <newPassword>",
                               "user permission add <name> <permission>",
                               "user permission remove <name> <permission>",
                               "user list");
        }
    }
}
