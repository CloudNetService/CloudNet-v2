package eu.cloudnetservice.cloudnet.v2.wrapper;

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import jline.console.ConsoleReader;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class CloudNetWrapperConfig {

    private final Path path = Paths.get("config.yml");

    private Configuration configuration;

    private Optional<String> cloudnet4Host = Optional.empty();
    private Optional<String> cloudnet6Host = Optional.empty();

    private String internalIP, wrapperId, proxyConfigHost;

    private boolean savingRecords, autoUpdate, maintenanceCopy;

    private int cloudnetPort, startPort, processQueueSize, maxMemory, webPort;

    private double percentOfCPUForANewServer;
    private double percentOfCPUForANewProxy;

    public CloudNetWrapperConfig(ConsoleReader reader) throws Exception {

        for (File directory : new File[] {
            new File("local/servers"),
            new File("local/templates"),
            new File("local/cache/web_templates"),
            new File("local/cache/web_plugins"),
            new File("local/records"),
            new File("local/global/plugins"),
            new File("local/plugins"),
            new File("local/proxy_versions"),
            new File("temp")}) {
            directory.mkdirs();
        }

        if (!Files.exists(path)) {
            Files.createFile(path);

            String hostName = NetworkUtils.getHostName();
            if (System.getProperty("hostAddress") != null) {
                hostName = System.getProperty("hostAddress");
            }

            Optional<String> host4Name = Optional.empty();
            Optional<String> host6Name = Optional.empty();
            InetAddressValidator validator = new InetAddressValidator();

            if (hostName.equals("127.0.0.1") || hostName.equals("127.0.1.1") || hostName.split("\\.").length != 4) {
                String input;
                System.out.println("Please provide your service ipv4, leave blank for only ipv6");
                System.out.println("Only Ipv6 is not recommended!");
                while ((input = reader.readLine()) != null) {
                    if (input.isEmpty()) {
                        host4Name = Optional.empty();
                        break;
                    }
                    if (!validator.isValidInet4Address(input)) {
                        System.out.println("Please provide your real ipv4 address :)");
                        continue;
                    }
                    host4Name = Optional.of(input);
                    break;
                }

                System.out.println("please provide your service ipv6, leave blank for only ipv4");
                while ((input = reader.readLine()) != null) {
                    if (input.isEmpty()) {
                        host6Name = Optional.empty();
                        break;
                    }
                    if (!validator.isValidInet6Address(input)) {
                        System.out.println("Please provide your real ipv6 address :)");
                        continue;
                    }
                    host6Name = Optional.of(input);
                    break;
                }
            }

            if (!host4Name.isPresent() && !host6Name.isPresent()) {
                //TODO: Exit or rerequest data
                System.exit(0);
            }


            String wrapperId = null;
            if (System.getProperty("wrapper-id") != null) {
                wrapperId = System.getProperty("wrapper-id");
            }

            if (wrapperId == null) {
                System.out.println("Please provide the name of this wrapper (example: Wrapper-1)");
                wrapperId = reader.readLine().replace(NetworkUtils.SPACE_STRING, NetworkUtils.EMPTY_STRING);

                if (wrapperId.isEmpty()) {
                    wrapperId = "Wrapper-" + NetworkUtils.RANDOM.nextInt();
                }
            }

            String cloudNetHost = NetworkUtils.getHostName();

            if (System.getProperty("cloudnet-host") != null) {
                cloudNetHost = System.getProperty("cloudnet-host");
            }

            Optional<String> cloud4Host = Optional.empty();
            Optional<String> cloud6Host = Optional.empty();

            if (cloudNetHost.equals("127.0.0.1") || cloudNetHost.equals("127.0.1.1") || cloudNetHost.split("\\.").length != 4) {
                String input;
                System.out.println("Please provide your master ipv4, leave blank for only ipv6");
                System.out.println("Only Ipv6 is not recommended!");
                while ((input = reader.readLine()) != null) {
                    if (input.isEmpty()) {
                        cloud4Host = Optional.empty();
                        break;
                    }
                    if (!validator.isValidInet4Address(input)) {
                        System.out.println("Please provide the real ipv4 address :)");
                        continue;
                    }
                    cloud4Host = Optional.of(input);
                    break;
                }

                System.out.println("please provide your service ipv6, leave blank for only ipv4");
                while ((input = reader.readLine()) != null) {
                    if (input.isEmpty()) {
                        cloud6Host = Optional.empty();
                        break;
                    }
                    if (!validator.isValidInet6Address(input)) {
                        System.out.println("Please provide the real ipv6 address :)");
                        continue;
                    }
                    cloud6Host = Optional.of(input);
                    break;
                }
            }

            if (!cloud6Host.isPresent() && !cloud4Host.isPresent()) {
                //TODO: Exit or rerequest data
                System.exit(0);
            }

            long memory = ((NetworkUtils.systemMemory() / 1048576) - 2048);
            if (memory < 1024) {
                System.out.println("WARNING: YOU CAN'T USE THE CLOUD NETWORK SOFTWARE WITH SUCH A SMALL MEMORY SIZE!");
            }

            Configuration configuration = new Configuration();
            configuration.set("connection.cloudnet-4host", cloud4Host.orElse(""));
            configuration.set("connection.cloudnet-6host", cloud6Host.orElse(""));
            configuration.set("connection.cloudnet-port", 1410);
            configuration.set("connection.cloudnet-web", 1420);
            configuration.set("general.wrapperId", wrapperId);
            configuration.set("general.internalIp", hostName);
            configuration.set("general.proxy-config-host", hostName);
            configuration.set("general.max-memory", memory);
            configuration.set("general.startPort", 41570);
            configuration.set("general.auto-update", false);
            configuration.set("general.saving-records", false);
            configuration.set("general.maintenance-copyFileToDirectory", false);
            configuration.set("general.processQueueSize", (Runtime.getRuntime().availableProcessors() / 2));
            configuration.set("general.percentOfCPUForANewServer", 100D);
            configuration.set("general.percentOfCPUForANewProxy", 100D);

            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8)) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, outputStreamWriter);
            }
        }
        load();
    }

    public CloudNetWrapperConfig load() {
        try (InputStream inputStream = Files.newInputStream(path); InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                                                                                                                               StandardCharsets.UTF_8)) {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(inputStreamReader);

            this.internalIP = configuration.getString("general.internalIp");
            this.savingRecords = configuration.getBoolean("general.saving-records");
            this.wrapperId = configuration.getString("general.wrapperId");
            this.startPort = configuration.getInt("general.startPort");
            this.processQueueSize = configuration.getInt("general.processQueueSize");
            this.maxMemory = configuration.getInt("general.max-memory");
            this.autoUpdate = configuration.getBoolean("general.auto-update");
            this.maintenanceCopy = configuration.getBoolean("maintenance-copyFileToDirectory");
            this.percentOfCPUForANewProxy = configuration.getDouble("general.percentOfCPUForANewProxy");
            this.percentOfCPUForANewServer = configuration.getDouble("general.percentOfCPUForANewServer");

            this.cloudnet4Host = !configuration.getString("connection.cloudnet-4host").isEmpty() ? Optional.of(configuration.getString(
                "connection.cloudnet-4host")) : Optional.empty();
            this.cloudnet6Host = !configuration.getString("connection.cloudnet-6host").isEmpty() ? Optional.of(configuration.getString(
                "connection.cloudnet-6host")) : Optional.empty();
            this.cloudnetPort = configuration.getInt("connection.cloudnet-port");
            this.webPort = configuration.getInt("connection.cloudnet-web");

            if (!configuration.getSection("general").contains("proxy-config-host")) {
                configuration.set("general.proxy-config-host", this.internalIP);
                save();
            }

            //generatated configurations

            this.proxyConfigHost = configuration.getString("general.proxy-config-host");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public void save() {
        try (OutputStream outputStream = Files.newOutputStream(path); OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
            outputStream)) {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, outputStreamWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public int getMaxMemory() {
        return maxMemory;
    }

    public int getCloudnetPort() {
        return cloudnetPort;
    }

    public int getProcessQueueSize() {
        return processQueueSize;
    }

    public int getStartPort() {
        return startPort;
    }

    public Path getPath() {
        return path;
    }

    public Optional<InetAddress> getCloudnetHost() {
        try {
            InetAddressValidator validator = new InetAddressValidator();
            if (cloudnet6Host.isPresent() && validator.isValidInet6Address(cloudnet6Host.get())) {
                return Optional.of(InetAddress.getByName(cloudnet6Host.get()));
            }

            if (cloudnet4Host.isPresent() && validator.isValidInet6Address(cloudnet4Host.get())) {
                return Optional.of(InetAddress.getByName(cloudnet4Host.get()));
            }
        } catch (UnknownHostException ignore) {
        }

        System.err.println("No valid cloudnet Host!");
        return Optional.empty();
    }

    public InetAddress getInternalIP() {
        try {
            return InetAddress.getByName(internalIP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getProxyConfigHost() {
        return proxyConfigHost;
    }

    public String getWrapperId() {
        return wrapperId;
    }

    public double getPercentOfCPUForANewProxy() {
        return percentOfCPUForANewProxy;
    }

    public double getPercentOfCPUForANewServer() {
        return percentOfCPUForANewServer;
    }

    public int getWebPort() {
        return webPort;
    }

    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    public boolean isMaintenanceCopy() {
        return maintenanceCopy;
    }

    public boolean isSavingRecords() {
        return savingRecords;
    }

}
