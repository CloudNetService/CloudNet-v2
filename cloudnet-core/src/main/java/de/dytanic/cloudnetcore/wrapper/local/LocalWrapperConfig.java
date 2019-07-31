package de.dytanic.cloudnetcore.wrapper.local;
/*
 * Created by derrop on 04.06.2019
 */

import net.md_5.bungee.config.Configuration;

class LocalWrapperConfig {

    private long loadTime;
    private Configuration configuration;

    public LocalWrapperConfig(Configuration configuration) {
        this.configuration = configuration;
        this.loadTime = System.currentTimeMillis();
    }

    public boolean isOutdated() {
        return System.currentTimeMillis() - this.loadTime >= 30000;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}