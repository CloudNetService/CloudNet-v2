/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.setup;

import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.setup.ISetupCancel;
import de.dytanic.cloudnet.setup.ISetupComplete;
import de.dytanic.cloudnet.setup.Setup;
import de.dytanic.cloudnet.setup.SetupRequest;
import de.dytanic.cloudnet.setup.SetupResponseType;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.WrapperMeta;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.Getter;

/**
 * Created by Tareko on 21.10.2017.
 */
@Getter
public class SetupWrapper {

    private String name;

    public SetupWrapper(CommandSender commandSender, String name)
    {
        this.name = name;

        Setup setup = new Setup().setupCancel(() -> System.out.println("Setup was cancelled")).setupComplete(data -> {
            String host = data.getString("address");
            String user = data.getString("user");

            WrapperMeta wrapperMeta = new WrapperMeta(name, host, user);
            CloudNet.getInstance().getConfig().createWrapper(wrapperMeta);
            commandSender.sendMessage("Wrapper [" + wrapperMeta.getId() + "] was registered on CloudNet");
        });

        Consumer<SetupRequest> request = setup::request;
        request.accept(new SetupRequest("address", "What's the IP address of the wrapper?", "Specified IP address is invalid!", SetupResponseType.STRING, key -> key.split("\\.").length == 4 && !key.equalsIgnoreCase("127.0.0.1")));
        request.accept(new SetupRequest("user", "What's the user of the wrapper?", "Specified name is invalid!", SetupResponseType.STRING, key -> CloudNet.getInstance().getUser(key) != null));
        setup.start(CloudNet.getLogger().getReader());
    }

}