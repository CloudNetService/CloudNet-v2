package eu.cloudnetservice.cloudnet.v2.wrapper.setup;

import eu.cloudnetservice.cloudnet.v2.command.CommandManager;
import eu.cloudnetservice.cloudnet.v2.console.ConsoleManager;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
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

public class CloudNetWrapperConfigSetup extends Setup {
    public CloudNetWrapperConfigSetup(final ConsoleManager consoleManager) {
        super(consoleManager);
        request(new RequestIpAddress());
        request(new RequestWrapperId());
        request(new RequestMasterAddress());
        setupCancel(() -> System.exit(-1));
        setupComplete(data-> {
            long memory = ((NetworkUtils.systemMemory() / 1048576) - 2048);
            if (memory < 1024) {
                System.out.println("WARNING: YOU CAN'T USE THE CLOUD NETWORK SOFTWARE WITH SUCH A SMALL MEMORY SIZE!");
            }

            Configuration configuration = new Configuration();
            configuration.set("connection.cloudnet-host", data.getString("masteraddress"));
            configuration.set("connection.cloudnet-port", 1410);
            configuration.set("connection.cloudnet-web", 1420);
            configuration.set("general.wrapperId", data.getString("wrapperid"));
            configuration.set("general.internalIp", data.getString("ipaddress"));
            configuration.set("general.proxy-config-host", data.getString("ipaddress"));
            configuration.set("general.max-memory", memory);
            configuration.set("general.startPort", 41570);
            configuration.set("general.auto-update", false);
            configuration.set("general.saving-records", false);
            configuration.set("general.maintenance-copyFileToDirectory", false);
            configuration.set("general.processQueueSize", (Runtime.getRuntime().availableProcessors() / 2));
            configuration.set("general.percentOfCPUForANewServer", 100D);
            configuration.set("general.percentOfCPUForANewProxy", 100D);

            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("config.yml")), StandardCharsets.UTF_8)) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, outputStreamWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }
            consoleManager.changeConsoleInput(CommandManager.class);
            consoleManager.getConsoleRegistry().unregisterInput(CloudNetWrapperConfigSetup.class);
        });
    }

    static final class RequestIpAddress extends SetupRequest {

        public RequestIpAddress() {
            super(
                "ipaddress",
                "Your local IP address is 127.0.0.1, please provide your service ip",
                "Please provide your real ip address \uD83D\uDE07",
                StringResponseType.getInstance(), s -> s.equals("127.0.0.1") || s.equals("127.0.1.1") || s.split("\\.").length != 4);
        }
    }

    static final class RequestWrapperId extends SetupRequest {

        public RequestWrapperId() {
            super("wrapperid",
                  "Please provide the name of this wrapper (example: Wrapper-1)",
                  "",
                  StringResponseType.getInstance(),
                  s -> true);
        }
    }

    static final class RequestMasterAddress extends SetupRequest {

        public RequestMasterAddress() {
            super("masteraddress",
                  "Provide the ip address of the cloudnet-master, please",
                  "Please provide your real ip address \uD83D\uDE07",
                  StringResponseType.getInstance(),
                  s ->  s.equals("127.0.0.1") || s.equals("127.0.1.1") || s.split("\\.").length != 4);
        }
    }
}
