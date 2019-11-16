/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.setup;

import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.setup.Setup;
import de.dytanic.cloudnet.setup.SetupRequest;
import de.dytanic.cloudnet.setup.SetupResponseType;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.WrapperMeta;

/**
 * Created by Tareko on 21.10.2017.
 */
public class SetupWrapper {

    private String name;
    private final Setup setup;

    public SetupWrapper(CommandSender commandSender, String name) {
        this.name = name;

        setup = new Setup().setupCancel(() -> System.out.println("Setup was cancelled"))
                           .setupComplete(data -> {
                               String host = data.getString("address");
                               String user = data.getString("user");

                               WrapperMeta wrapperMeta = new WrapperMeta(name, host, user);
                               CloudNet.getInstance().getConfig().createWrapper(wrapperMeta);
                               commandSender.sendMessage(String.format("Wrapper [%s] was registered on CloudNet",
                                                                       wrapperMeta.getId()));
                           });

        setup.request(new SetupRequest("address",
                                       "What's the IP address of the wrapper?",
                                       "Specified IP address is invalid!",
                                       SetupResponseType.STRING,
                                       key -> key.split("\\.").length == 4 && !key.equalsIgnoreCase("127.0.0.1")));
        setup.request(new SetupRequest("user",
                                       "What's the user of the wrapper?",
                                       "Specified name is invalid!",
                                       SetupResponseType.STRING,
                                       key -> CloudNet.getInstance().getUser(key) != null));
    }

    public String getName() {
        return name;
    }

    public void startSetup() {
        setup.start(CloudNet.getLogger().getReader());
    }
}
