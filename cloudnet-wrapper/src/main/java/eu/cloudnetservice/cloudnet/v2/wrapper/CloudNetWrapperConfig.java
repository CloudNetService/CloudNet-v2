package eu.cloudnetservice.cloudnet.v2.wrapper;

import eu.cloudnetservice.cloudnet.v2.console.model.ConsoleInputDispatch;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jline.reader.LineReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

public class CloudNetWrapperConfig {

    private final Path path = Paths.get("config.yml");

    private Configuration configuration;

    private String internalIP, wrapperId, cloudnetHost, proxyConfigHost;

    private boolean savingRecords, autoUpdate, maintenanceCopy;

    private int cloudnetPort, startPort, processQueueSize, maxMemory, webPort;

    private double percentOfCPUForANewServer;
    private double percentOfCPUForANewProxy;

    public CloudNetWrapperConfig() {

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

            this.cloudnetHost = configuration.getString("connection.cloudnet-host");
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

    public String getCloudnetHost() {
        return cloudnetHost;
    }

    public String getInternalIP() {
        return internalIP;
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
