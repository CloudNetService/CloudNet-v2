package eu.cloudnetservice.cloudnet.v2.master.setup.user;

import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.lib.user.BasicUser;
import eu.cloudnetservice.cloudnet.v2.lib.user.User;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.setup.Setup;
import eu.cloudnetservice.cloudnet.v2.setup.SetupRequest;
import eu.cloudnetservice.cloudnet.v2.setup.responsetype.StringResponseType;

import java.util.ArrayList;

public final class CreateUserSetup extends Setup {

    public CreateUserSetup(CommandSender sender) {
        super(CloudNet.getInstance().getConsoleManager());
        setupComplete(document -> {
            String username = document.getString("username");
            String password = document.getString("password");
            String passwordrepeat = document.getString("passwordrepeat");
            if (password.equals(passwordrepeat)) {
                User user = new BasicUser(username, password, new ArrayList<>());
                CloudNet.getInstance().getUsers().add(user);
                CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
                sender.sendMessage("§aSuccessfully created a new user!");
            } else {
                sender.sendMessage("§ePassword are not the same!");
            }
        });
        request(new SetupRequest("username",
                                 "What should the username be?",
                                 "§aThis user is already registerd!",
                                 StringResponseType.getInstance(),
                                 username -> CloudNet.getInstance().getUser(username) != null, null
        ));
        request(new SetupRequest("password",
                                 "What should be the password?",
                                 "",
                                 StringResponseType.getInstance(),
                                 username -> true, null
        ));
        request(new SetupRequest("passwordrepeat",
                                 "Repeat the password?",
                                 "",
                                 StringResponseType.getInstance(),
                                 password  -> true, null
        ));
    }


}
