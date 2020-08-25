package eu.cloudnetservice.cloudnet.v2.master.setup;

import eu.cloudnetservice.cloudnet.v2.command.CommandManager;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.lib.ConnectableAddress;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.WrapperMeta;
import eu.cloudnetservice.cloudnet.v2.setup.Setup;
import eu.cloudnetservice.cloudnet.v2.setup.SetupRequest;
import eu.cloudnetservice.cloudnet.v2.setup.responsetype.IntegerResponseType;
import eu.cloudnetservice.cloudnet.v2.setup.responsetype.StringResponseType;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SetupCreateWrapper extends Setup {
    public SetupCreateWrapper(CommandSender sender) {
        super(CloudNet.getInstance().getConsoleManager());
        this.setupCancel(() -> {
            System.out.println("Setup was cancelled");
            CloudNet.getInstance().getConsoleManager().changeConsoleInput(CommandManager.class);
            CloudNet.getInstance().getConsoleRegistry().unregisterInput(SetupWrapper.class);
        })
            .setupComplete(data -> {
                String host = data.getString("listingIp");
                Integer memory = data.getInt("memory");
                Integer queue = data.getInt("queue");
                String wrapperId = data.getString("wrapperId");
                String user = data.getString("user");

                WrapperMeta wrapperMeta = new WrapperMeta(wrapperId, host, user);
                CloudNet.getInstance().getConfig().createWrapper(wrapperMeta);
                sender.sendMessage(String.format("Wrapper [%s] was registered on CloudNet",
                                                        wrapperMeta.getId()));
                Configuration configuration = new Configuration();
                configuration.set("connection.cloudnet-host", CloudNet.getInstance().getConfig().getAddresses().toArray(new ConnectableAddress[0])[0].getHostName());
                configuration.set("connection.cloudnet-port", 1410);
                configuration.set("connection.cloudnet-web", 1420);
                configuration.set("general.wrapperId", wrapperId);
                configuration.set("general.internalIp", host);
                configuration.set("general.proxy-config-host",host);
                configuration.set("general.max-memory", memory);
                configuration.set("general.startPort", 41570);
                configuration.set("general.auto-update", false);
                configuration.set("general.saving-records", false);
                configuration.set("general.maintenance-copyFileToDirectory", false);
                configuration.set("general.processQueueSize", queue);
                configuration.set("general.percentOfCPUForANewServer", 100D);
                configuration.set("general.percentOfCPUForANewProxy", 100D);
                try {
                    Files.createDirectories(Paths.get("local","wrapper",wrapperId));
                    Files.copy(Paths.get("WRAPPER_KEY.cnd"), Paths.get("local","wrapper",wrapperId,"WRAPPER_KEY.cnd"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("local","wrapper",wrapperId,"config.yml")), StandardCharsets.UTF_8)) {

                    ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, outputStreamWriter);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sender.sendMessage(String.format("Wrapper [%s] config is generated under -> %s",
                                                 wrapperMeta.getId(), "local/wrapper/"+wrapperId));

                sender.sendMessage("WRAPPER_KEY.cnd is also copied to local/wrapper/"+wrapperId );
                CloudNet.getInstance().getConsoleManager().changeConsoleInput(CommandManager.class);
                CloudNet.getInstance().getConsoleRegistry().unregisterInput(SetupWrapper.class);
            })

            .request(new SetupRequest("wrapperId",
                                      "What is the ID of this wrapper?",
                                      "",
                                      StringResponseType.getInstance(),
                                      key -> true))
            .request(new SetupRequest("listingIp",
                                      "What is the listing address of the wrapper for proxies and servers?",
                                      "The specified IP address is invalid!",
                                      StringResponseType.getInstance(),
                                      key -> key.split("\\.").length == 4 && !key.equalsIgnoreCase("127.0.0.1")))
            .request(new SetupRequest("user",
                                      "What is the user of the wrapper?",
                                      "The specified user does not exist!",
                                      StringResponseType.getInstance(),
                                      key -> CloudNet.getInstance().getUser(key) != null))
            .request(new SetupRequest("memory",
                                      "How many memory is allowed to use for the wrapper services?",
                                      "Not allowed under 1Gb",
                                      IntegerResponseType.getInstance(),
                                  key ->  Integer.parseInt(key) > 1024))
            .request(new SetupRequest("queue",
                                  "How large should the server queue be?",
                                  "Not allowed under 2",
                                  IntegerResponseType.getInstance(),
                                  key ->  Integer.parseInt(key) <= 2));
    }
}