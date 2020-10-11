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

package eu.cloudnetservice.cloudnet.v2.master.command;
/*
 * Created by derrop on 04.06.2019
 */

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.lib.user.BasicUser;
import eu.cloudnetservice.cloudnet.v2.lib.user.User;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;

import java.util.ArrayList;

public class CommandUser extends Command {
    public CommandUser() {
        super("user", "cloudnet.command.user");

        description = "Manages CloudNet internal users";
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            if (CloudNet.getInstance().getUser(args[1]) != null) {
                sender.sendMessage("A user with that name already exists!");
                return;
            }
            User user = new BasicUser(args[1], args[2], new ArrayList<>());
            CloudNet.getInstance().getUsers().add(user);
            CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
            sender.sendMessage("Successfully created a new user!");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("changePassword")) {
            User user = CloudNet.getInstance().getUser(args[1]);
            if (user == null) {
                sender.sendMessage("A user with that name does not exist!");
                return;
            }
            user.setPassword(args[2]);
            CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
            sender.sendMessage("Successfully changed the password of the user \"" + user.getName() + "\"!");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {
            User user = CloudNet.getInstance().getUser(args[1]);
            if (user == null) {
                sender.sendMessage("A user with that name does not exist!");
                return;
            }
            CloudNet.getInstance().getUsers().remove(user);
            CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
            sender.sendMessage("Successfully deleted the user \"" + user.getName() + '"');
        } else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            sender.sendMessage("Users:");
            for (User user : CloudNet.getInstance().getUsers()) {
                sender.sendMessage(" - " + user.getName() + '/' + user.getUniqueId() + ':',
                                   "  API-Token: " + user.getApiToken(),
                                   "  Permissions: ");
                for (String permission : user.getPermissions()) {
                    sender.sendMessage("   - " + permission);
                }
            }
        } else if (args.length == 4 && args[0].equalsIgnoreCase("permission")) {
            User user = CloudNet.getInstance().getUser(args[2]);
            if (user == null) {
                sender.sendMessage("A user with that name does not exist!");
                return;
            }

            String permission = args[3];

            if (args[1].equalsIgnoreCase("add")) {
                if (user.getPermissions().contains(permission)) {
                    sender.sendMessage("The user \"" + user.getName() + "\" already has the permission \"" + permission + '"');
                    return;
                }
                user.getPermissions().add(permission);
            } else if (args[1].equalsIgnoreCase("remove")) {
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
