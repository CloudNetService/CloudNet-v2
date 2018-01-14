/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper;

import de.dytanic.cloudnet.lib.NetworkUtils;
import jline.console.ConsoleReader;
import lombok.Getter;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;
import java.util.UUID;

@Getter
public class CloudNetWrapperConfig {

    private final Path path = Paths.get("config.yml");

    private Configuration configuration;

    private String internalIP;

    private boolean savingRecords;

    private String wrapperId;

    private String cloudnetHost;

    private int cloudnetPort;

    private int startPort;

    private int processQueueSize;

    private int maxMemory;

    private boolean viaVersion;

    private boolean autoUpdate;

    private double percentOfCPUForANewServer;

    private double percentOfCPUForANewCloudServer;

    private double percentOfCPUForANewProxy;

    private int webPort;

    private boolean maintenance_copy;

    private String devServicePath;

    public CloudNetWrapperConfig(ConsoleReader reader) throws Exception
    {

        if (!Files.exists(Paths.get("local")))
            Files.createDirectory(Paths.get("local"));
        if (!Files.exists(Paths.get("local/servers")))
            Files.createDirectory(Paths.get("local/servers"));
        if (!Files.exists(Paths.get("local/templates")))
            Files.createDirectory(Paths.get("local/templates"));
        if (!Files.exists(Paths.get("local/cache")))
            Files.createDirectory(Paths.get("local/cache"));
        if (!Files.exists(Paths.get("local/cache/web_templates")))
            Files.createDirectory(Paths.get("local/cache/web_templates"));
        if (!Files.exists(Paths.get("local/cache/web_plugins")))
            Files.createDirectory(Paths.get("local/cache/web_plugins"));
        if (!Files.exists(Paths.get("local/records")))
            Files.createDirectory(Paths.get("local/records"));
        if (!Files.exists(Paths.get("temp")))
            Files.createDirectory(Paths.get("temp"));
        if (!Files.exists(Paths.get("modules")))
            Files.createDirectory(Paths.get("modules"));
        if (!Files.exists(Paths.get("local/global")))
            Files.createDirectory(Paths.get("local/global"));
        if (!Files.exists(Paths.get("local/global_cloudserver")))
            Files.createDirectory(Paths.get("local/global_cloudserver"));
        if (!Files.exists(Paths.get("local/global/plugins")))
            Files.createDirectory(Paths.get("local/global/plugins"));
        if (!Files.exists(Paths.get("local/proxy_versions")))
            Files.createDirectory(Paths.get("local/proxy_versions"));
        if (!Files.exists(Paths.get("local/plugins")))
            Files.createDirectory(Paths.get("local/plugins"));
        if (!Files.exists(Paths.get("temp/cloudserver")))
            Files.createDirectory(Paths.get("temp/cloudserver"));

        if (!Files.exists(path))
        {
            Files.createFile(path);

            String hostName = NetworkUtils.getHostName();
            if (System.getProperty("hostAddress") != null)
            {
                hostName = System.getProperty("hostAddress");
            }

            if (hostName.equals("127.0.0.1") || hostName.equals("127.0.1.1") || hostName.split("\\.").length != 4)
            {
                String input;
                System.out.println("Your local IP address is 127.0.0.1, please provide your service ip");
                while ((input = reader.readLine()) != null)
                {
                    if ((input.equals("127.0.0.1") || input.equals("127.0.1.1") || input.split("\\.").length != 4))
                    {
                        System.out.println("Please provide your real ip address :)");
                        continue;
                    }

                    hostName = input;
                    break;
                }
            }

            String wrapperId = null;
            if (System.getProperty("wrapper-id") != null)
            {
                wrapperId = System.getProperty("wrapper-id");
            }

            if (wrapperId == null)
            {
                System.out.println("Please provide the name of this wrapper (example: Wrapper-1)");
                wrapperId = reader.readLine().replace(" ", "");

                if (wrapperId.isEmpty() || wrapperId.equals(""))
                {
                    wrapperId = "Wrapper-" + NetworkUtils.RANDOM.nextInt();
                }
            }

            String cloudNetHost = NetworkUtils.getHostName();

            if (System.getProperty("cloudnet-host") != null)
            {
                cloudNetHost = System.getProperty("cloudnet-host");
            }

            if (cloudNetHost.equals("127.0.0.1") || cloudNetHost.equals("127.0.1.1") || cloudNetHost.split("\\.").length != 4)
            {
                String input;
                System.out.println("Provide the ip address of the cloudnet-master, please");
                while ((input = reader.readLine()) != null)
                {
                    if ((input.equals("127.0.0.1") || input.equals("127.0.1.1") || input.split("\\.").length != 4))
                    {
                        System.out.println("Please provide the real ip address :)");
                        continue;
                    }

                    cloudNetHost = input;
                    break;
                }
            }

            long memory = ((NetworkUtils.systemMemory() / 1048576) - 2048);
            if (memory < 1024)
            {
                System.out.println("WARINING: YOU CAN'T USE THE CLOUD NETWORK SOFTWARE WITH SUCH A SMALL MEMORY SIZE!");
            }

            Configuration configuration = new Configuration();
            configuration.set("connection.cloudnet-host", cloudNetHost);
            configuration.set("connection.cloudnet-port", 1410);
            configuration.set("connection.cloudnet-web", 1420);
            configuration.set("general.wrapperId", wrapperId);
            configuration.set("general.internalIp", hostName);
            configuration.set("general.max-memory", memory);
            configuration.set("general.startPort", 41570);
            configuration.set("general.auto-update", true);
            configuration.set("general.saving-records", false);
            configuration.set("general.viaversion", false);
            configuration.set("general.maintenance-copy", false);
            configuration.set("general.devservicePath", new File("Development").getAbsolutePath());
            configuration.set("general.processQueueSize", (Runtime.getRuntime().availableProcessors() / 2));
            configuration.set("general.percentOfCPUForANewServer", 70D);
            configuration.set("general.percentOfCPUForANewCloudServer", 70D);
            configuration.set("general.percentOfCPUForANewProxy", 80D);

            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8))
            {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, outputStreamWriter);
            }
        }
        load();
    }

    public CloudNetWrapperConfig load()
    {
        try (InputStream inputStream = Files.newInputStream(path); InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(inputStreamReader);

            this.internalIP = configuration.getString("general.internalIp");
            this.savingRecords = configuration.getBoolean("general.saving-records");
            this.wrapperId = configuration.getString("general.wrapperId");
            this.startPort = configuration.getInt("general.startPort");
            this.processQueueSize = configuration.getInt("general.processQueueSize");
            this.maxMemory = configuration.getInt("general.max-memory");
            this.viaVersion = configuration.getBoolean("general.viaversion");
            this.autoUpdate = configuration.getBoolean("general.auto-update");
            this.maintenance_copy = configuration.getBoolean("maintenance-copy");
            this.percentOfCPUForANewProxy = configuration.getDouble("general.percentOfCPUForANewProxy");
            this.percentOfCPUForANewServer = configuration.getDouble("general.percentOfCPUForANewServer");

            this.cloudnetHost = configuration.getString("connection.cloudnet-host");
            this.cloudnetPort = configuration.getInt("connection.cloudnet-port");
            this.webPort = configuration.getInt("connection.cloudnet-web");

            if(!configuration.getSection("general").self.containsKey("percentOfCPUForANewCloudServer"))
            {
                configuration.set("general.percentOfCPUForANewCloudServer", 70D);
                save();
            }

            if(!configuration.getSection("general").self.containsKey("devservicePath"))
            {
                configuration.set("general.devservicePath", new File("Development").getAbsolutePath());
                save();
            }

            this.percentOfCPUForANewCloudServer = configuration.getDouble("general.percentOfCPUForANewCloudServer");
            this.devServicePath = configuration.getString("general.devservicePath");
            if(!Files.exists(Paths.get(devServicePath)))
            {
                Files.createDirectories(Paths.get(devServicePath));
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    public void save()
    {
        try (OutputStream outputStream = Files.newOutputStream(path); OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream))
        {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, outputStreamWriter);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}