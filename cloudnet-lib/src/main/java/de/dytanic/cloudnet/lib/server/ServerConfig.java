package de.dytanic.cloudnet.lib.server;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Created by Tareko on 25.07.2017.
 */
public class ServerConfig {

    public static final Type TYPE = TypeToken.get(ServerConfig.class).getType();

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
    public int hashCode() {
        int result = (hideServer ? 1 : 0);
        result = 31 * result + (extra != null ? extra.hashCode() : 0);
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        result = 31 * result + (int) (startup ^ (startup >>> 32));
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ServerConfig that = (ServerConfig) o;

        if (hideServer != that.hideServer) {
            return false;
        }
        if (startup != that.startup) {
            return false;
        }
        if (!Objects.equals(extra, that.extra)) {
            return false;
        }
        return Objects.equals(properties, that.properties);
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
            "hideServer=" + hideServer +
            ", extra='" + extra + '\'' +
            ", properties=" + properties +
            ", startup=" + startup +
            '}';
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

    public void setStartup(final long startup) {
        this.startup = startup;
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
