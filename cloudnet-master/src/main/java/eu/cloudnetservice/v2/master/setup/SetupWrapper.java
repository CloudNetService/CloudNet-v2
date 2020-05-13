package eu.cloudnetservice.v2.master.setup;

import eu.cloudnetservice.v2.command.CommandSender;
import eu.cloudnetservice.v2.setup.Setup;
import eu.cloudnetservice.v2.setup.SetupRequest;
import eu.cloudnetservice.v2.setup.responsetype.StringResponseType;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.network.components.WrapperMeta;

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
                                       "What is the IP address of this wrapper?",
                                       "The specified IP address is invalid!",
                                       StringResponseType.getInstance(),
                                       key -> key.split("\\.").length == 4 && !key.equalsIgnoreCase("127.0.0.1")));
        setup.request(new SetupRequest("user",
                                       "What is the user of the wrapper?",
                                       "The specified user does not exist!",
                                       StringResponseType.getInstance(),
                                       key -> CloudNet.getInstance().getUser(key) != null));
    }

    public String getName() {
        return name;
    }

    public void startSetup() {
        setup.start(CloudNet.getLogger().getReader());
    }
}
