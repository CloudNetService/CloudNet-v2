/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper;

import de.dytanic.cloudnet.lib.NetworkUtils;
import jline.console.ConsoleReader;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CloudNetWrapperConfig {

    private final Path path = Paths.get("config.yml");

    private Configuration configuration;

    private String internalIP, wrapperId, cloudnetHost, devServicePath, proxy_config_host;

    private boolean savingRecords, viaVersion, autoUpdate, maintenance_copy;

    private int cloudnetPort, startPort, processQueueSize, maxMemory, webPort;

    private double percentOfCPUForANewServer, percentOfCPUForANewCloudServer, percentOfCPUForANewProxy;

    public CloudNetWrapperConfig(ConsoleReader reader) throws Exception {

        for (File directory : new File[] {new File("local/servers"), new File("local/templates"), new File("local/cache/web_templates"), new File(
            "local/cache/web_plugins"), new File("local/records"), new File("local/plugins"), new File("local/records"), new File(
            "local/global_cloudserver"), new File("local/global/plugins"), new File("local/proxy_versions"), new File("temp"), new File(
            "temp/cloudserver"), new File("modules")}) {
            directory.mkdirs();
        }

        if (!Files.exists(path)) {
            Files.createFile(path);

            String hostName = NetworkUtils.getHostName();
            if (System.getProperty("hostAddress") != null) {
                hostName = System.getProperty("hostAddress");
            }

            if (hostName.equals("127.0.0.1") || hostName.equals("127.0.1.1") || hostName.split("\\.").length != 4) {
                String input;
                System.out.println("Your local IP address is 127.0.0.1, please provide your service ip");
                while ((input = reader.readLine()) != null) {
                    if ((input.equals("127.0.0.1") || input.equals("127.0.1.1") || input.split("\\.").length != 4)) {
                        System.out.println("Please provide your real ip address :)");
                        continue;
                    }

                    hostName = input;
                    break;
                }
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

            if (cloudNetHost.equals("127.0.0.1") || cloudNetHost.equals("127.0.1.1") || cloudNetHost.split("\\.").length != 4) {
                String input;
                System.out.println("Provide the ip address of the cloudnet-master, please");
                while ((input = reader.readLine()) != null) {
                    if ((input.equals("127.0.0.1") || input.equals("127.0.1.1") || input.split("\\.").length != 4)) {
                        System.out.println("Please provide the real ip address :)");
                        continue;
                    }

                    cloudNetHost = input;
                    break;
                }
            }

            long memory = ((NetworkUtils.systemMemory() / 1048576) - 2048);
            if (memory < 1024) {
                System.out.println("WARNING: YOU CAN'T USE THE CLOUD NETWORK SOFTWARE WITH SUCH A SMALL MEMORY SIZE!");
            }

            Configuration configuration = new Configuration();
            configuration.set("connection.cloudnet-host", cloudNetHost);
            configuration.set("connection.cloudnet-port", 1410);
            configuration.set("connection.cloudnet-web", 1420);
            configuration.set("general.wrapperId", wrapperId);
            configuration.set("general.internalIp", hostName);
            configuration.set("general.proxy-config-host", hostName);
            configuration.set("general.max-memory", memory);
            configuration.set("general.startPort", 41570);
            configuration.set("general.auto-update", false);
            configuration.set("general.saving-records", false);
            configuration.set("general.viaversion", false);
            configuration.set("general.maintenance-copyFileToDirectory", false);
            configuration.set("general.devservicePath", new File("Development").getAbsolutePath());
            configuration.set("general.processQueueSize", (Runtime.getRuntime().availableProcessors() / 2));
            configuration.set("general.percentOfCPUForANewServer", 100D);
            configuration.set("general.percentOfCPUForANewCloudServer", 100D);
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
            this.viaVersion = configuration.getBoolean("general.viaversion");
            this.autoUpdate = configuration.getBoolean("general.auto-update");
            this.maintenance_copy = configuration.getBoolean("maintenance-copyFileToDirectory");
            this.percentOfCPUForANewProxy = configuration.getDouble("general.percentOfCPUForANewProxy");
            this.percentOfCPUForANewServer = configuration.getDouble("general.percentOfCPUForANewServer");

            this.cloudnetHost = configuration.getString("connection.cloudnet-host");
            this.cloudnetPort = configuration.getInt("connection.cloudnet-port");
            this.webPort = configuration.getInt("connection.cloudnet-web");
            this.percentOfCPUForANewCloudServer = configuration.getDouble("general.percentOfCPUForANewCloudServer");
            this.devServicePath = configuration.getString("general.devservicePath");

            if (!configuration.getSection("general").self.containsKey("proxy-config-host")) {
                configuration.set("general.proxy-config-host", this.internalIP);
                save();
            }

            //generatated configurations

            this.proxy_config_host = configuration.getString("general.proxy-config-host");

            if (!Files.exists(Paths.get(devServicePath))) {
                Files.createDirectories(Paths.get(devServicePath));
            }

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

    public String getCloudnetHost() {
        return cloudnetHost;
    }

    public String getDevServicePath() {
        return devServicePath;
    }

    public String getInternalIP() {
        return internalIP;
    }

    public String getProxy_config_host() {
        return proxy_config_host;
    }

    public String getWrapperId() {
        return wrapperId;
    }

    public double getPercentOfCPUForANewCloudServer() {
        return percentOfCPUForANewCloudServer;
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

    public boolean isMaintenance_copy() {
        return maintenance_copy;
    }

    public boolean isSavingRecords() {
        return savingRecords;
    }

    public boolean isViaVersion() {
        return viaVersion;
    }
}
