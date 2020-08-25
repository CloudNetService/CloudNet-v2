package eu.cloudnetservice.cloudnet.v2.master.setup;

import eu.cloudnetservice.cloudnet.v2.command.CommandManager;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.WrapperMeta;
import eu.cloudnetservice.cloudnet.v2.setup.Setup;
import eu.cloudnetservice.cloudnet.v2.setup.SetupRequest;
import eu.cloudnetservice.cloudnet.v2.setup.responsetype.StringResponseType;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Tareko on 21.10.2017.
 */
public class SetupWrapper extends Setup {

    private final String name;

    public SetupWrapper(CommandSender commandSender, String name) {
        super(CloudNet.getInstance().getConsoleManager());
        this.name = name;

        this.setupCancel(() -> {
                System.out.println("Setup was cancelled");
                CloudNet.getInstance().getConsoleManager().changeConsoleInput(CommandManager.class);
                CloudNet.getInstance().getConsoleRegistry().unregisterInput(SetupWrapper.class);
            })
            .setupComplete(data -> {
                String host = data.getString("address");
                String user = data.getString("user");

                WrapperMeta wrapperMeta = new WrapperMeta(name, host, user);
                CloudNet.getInstance().getConfig().createWrapper(wrapperMeta);
                commandSender.sendMessage(String.format("Wrapper [%s] was registered on CloudNet",
                                                        wrapperMeta.getId()));

                CloudNet.getInstance().getConsoleManager().changeConsoleInput(CommandManager.class);
                CloudNet.getInstance().getConsoleRegistry().unregisterInput(SetupWrapper.class);
            })
            .request(new SetupRequest("address",
                                      "What is the IP address of this wrapper?",
                                      "The specified IP address is invalid!",
                                      StringResponseType.getInstance(),
                                      key -> key.split("\\.").length == 4 && !key.equalsIgnoreCase("127.0.0.1")))
            .request(new SetupRequest("user",
                                      "What is the user of the wrapper?",
                                      "The specified user does not exist!",
                                      StringResponseType.getInstance(),
                                      key -> CloudNet.getInstance().getUser(key) != null));
    }

    public String getName() {
        return name;
    }

}
