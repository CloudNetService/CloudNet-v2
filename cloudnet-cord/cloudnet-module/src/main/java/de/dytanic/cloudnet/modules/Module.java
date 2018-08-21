/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.modules;

import de.dytanic.cloudnet.event.EventKey;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.utility.document.Document;
import lombok.Data;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Data class for modules
 */
@Data
public abstract class Module<E> extends EventKey {

    /**
     * The folder where the data of this module is saved in.
     */
    private File dataFolder;

    /**
     * The configuration file of this module.
     */
    private File configFile;

    /**
     * The utility file of this module.
     */
    private File utilFile;

    /**
     * The configuration of this module's file.
     */
    private ModuleConfig moduleConfig;

    /*
     * The class loader that is assigned to this module.
     */
    private ModuleClassLoader classLoader;

    /**
     * The configuration for this module.
     */
    private Configuration configuration;

    /**
     * The loader that was used to load this module.
     */
    private ModuleLoader moduleLoader;

    /**
     * Method that is called when the module is loaded.
     */
    public void onLoad() {
    }

    /**
     * Method that is called when the module is enabled.
     */
    public void onBootstrap() {
    }

    /**
     * Method that is called when the plugin is about to be shut down.
     */
    public void onShutdown() {
    }

    /**
     * Returns the name of the module, if a module configuration is present.
     *
     * @return the name of the module if a module configuration is present,
     * {@code some_plugin-} + a random long value otherwise.
     */
    public String getName() {
        return moduleConfig != null ? moduleConfig.getName() : "some_plugin-" + NetworkUtils.RANDOM.nextLong();
    }

    /**
     * Returns the directory where the data of this module is stored.
     * Creates the directory, if needed.
     *
     * @return the directory for storing module data
     */
    public File getDataFolder() {
        if (dataFolder == null) {
            dataFolder = new File("modules", moduleConfig.getName());
            try {
                Files.createDirectories(dataFolder.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dataFolder;
    }

    /**
     * Returns the name of this module.
     *
     * @return the name as specified in the module configuration
     */
    public String getPluginName() {
        return moduleConfig.getName();
    }

    /**
     * Returns the version of this module.
     *
     * @return the version as specified in the module configuration
     */
    public String getVersion() {
        return moduleConfig.getVersion();
    }

    /**
     * Returns the author of this module.
     *
     * @return the author as specified in the module configuration
     */
    public String getAuthor() {
        return moduleConfig.getAuthor();
    }

    /**
     * Returns the currently loaded configuration; loads it,
     * if it isn't loaded already.
     *
     * @return the currently active configuration
     */
    public Configuration getConfig() {
        if (configuration == null) {
            loadConfiguration();
        }
        return configuration;
    }

    /**
     * Creates a new utility file holding the information in the given document.
     *
     * @param document the document that holds the utility information
     * @return the module
     */
    public Module<E> createUtils(Document document) {
        if (utilFile == null) {
            utilFile = new File("modules/" + moduleConfig.getName() + "/utils.json");
            if (!utilFile.exists()) {
                document.saveAsConfig(utilFile);
            }
        }
        return this;
    }

    /**
     * Loads and returns the document containing utility information.
     *
     * @return the document containing utility information
     */
    public Document getUtils() {
        if (utilFile == null) {
            utilFile = new File("modules/" + moduleConfig.getName() + "/utils.json");
            if (!utilFile.exists()) {
                new Document().saveAsConfig(utilFile);
            }
        }
        return Document.loadDocument(utilFile);
    }

    /**
     * Save the utility information.
     *
     * @param document the document containing the utility information
     * @return the module
     */
    public Module<E> saveUtils(Document document) {
        if (utilFile == null) {
            utilFile = new File("modules/" + moduleConfig.getName() + "/utils.json");
        }
        document.saveAsConfig(utilFile);
        return this;
    }

    /**
     * Save the configuration of this module.
     *
     * @return the module
     */
    public Module<E> saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(getConfig(), configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Load this module's configuration
     *
     * @return the module
     */
    public Module<E> loadConfig() {
        loadConfiguration();
        return this;
    }

    private void loadConfiguration() {
        getDataFolder().mkdir();
        if (configFile == null) {
            configFile = new File("modules/" + moduleConfig.getName() + "/config.yml");
            if (!configFile.exists()) {
                try {
                    configFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the domain of this module.
     *
     * @return the cloud domain of this module
     */
    public abstract E getCloud();
}
