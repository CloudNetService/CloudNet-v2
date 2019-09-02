package de.dytanic.cloudnet.lib.server;

import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 25.07.2017.
 */
public class ServerConfig {

    private boolean hideServer;
    private String extra;
    private Document properties;
    private long startup;

    public ServerConfig(boolean hideServer, String extra, Document properties, long startup) {
        this.hideServer = hideServer;
        this.extra = extra;
        this.properties = properties;
        this.startup = startup;
    }

    @Override
    public String toString() {
        return "ServerConfig{" + "hideServer=" + hideServer + ", extra='" + extra + '\'' + ", properties=" + properties + ", startup=" + startup + '}';
    }

    public Document getProperties() {
        return properties;
    }

    public void setProperties(Document properties) {
        this.properties = properties;
    }

    public long getStartup() {
        return startup;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public boolean isHideServer() {
        return hideServer;
    }

    public void setHideServer(boolean hideServer) {
        this.hideServer = hideServer;
    }
}
