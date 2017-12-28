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

/**
 * Created by Tareko on 23.07.2017.
 */
@Data
public abstract class Module<E> extends EventKey {

    private File dataFolder;

    private File configFile;

    private File utilFile;

    private ModuleConfig moduleConfig;

    private ModuleClassLoader classLoader;

    private Configuration configuration;

    private ModuleLoader moduleLoader;

    public void onLoad()
    {
    }

    public void onBootstrap()
    {
    }

    public void onShutdown()
    {
    }

    public String getName()
    {
        return moduleConfig != null ? moduleConfig.getName() : "some_plugin-" + NetworkUtils.RANDOM.nextLong();
    }

    public File getDataFolder()
    {
        if (dataFolder == null)
        {
            dataFolder = new File("modules/" + moduleConfig.getName());
        }
        return dataFolder;
    }

    public String getPluginName()
    {
        return moduleConfig.getName();
    }

    public String getVersion()
    {
        return moduleConfig.getVersion();
    }

    public String getAuthor()
    {
        return moduleConfig.getAuthor();
    }

    public Configuration getConfig()
    {
        getDataFolder().mkdir();
        if (configFile == null)
        {
            configFile = new File("modules/" + moduleConfig.getName() + "/config.yml");
            if (!configFile.exists())
            {
                try
                {
                    configFile.createNewFile();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        if (configuration == null)
        {
            try
            {
                configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return configuration;
    }

    public Module<E> createUtils(Document document)
    {
        if (utilFile == null)
        {
            utilFile = new File("modules/" + moduleConfig.getName() + "/utils.json");
            if (!utilFile.exists())
            {
                document.saveAsConfig(utilFile);
            }
        }
        return this;
    }

    public Document getUtils()
    {
        if (utilFile == null)
        {
            utilFile = new File("modules/" + moduleConfig.getName() + "/utils.json");
            if (!utilFile.exists())
            {
                new Document().saveAsConfig(utilFile);
            }
        }
        return Document.loadDocument(utilFile);
    }

    public Module<E> saveUtils(Document document)
    {
        if (utilFile == null)
        {
            utilFile = new File("modules/" + moduleConfig.getName() + "/utils.json");
        }
        document.saveAsConfig(utilFile);
        return this;
    }

    public Module<E> saveConfig()
    {
        try
        {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(getConfig(), configFile);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    public Module<E> loadConfig()
    {
        getDataFolder().mkdir();
        if (configFile == null)
        {
            configFile = new File("modules/" + moduleConfig.getName() + "/config.yml");
            if (!configFile.exists())
            {
                try
                {
                    configFile.createNewFile();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        try
        {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    public abstract E getCloud();
}