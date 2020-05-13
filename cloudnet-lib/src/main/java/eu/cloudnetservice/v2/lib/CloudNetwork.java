package eu.cloudnetservice.v2.lib;

import eu.cloudnetservice.v2.lib.network.WrapperInfo;
import eu.cloudnetservice.v2.lib.server.ProxyGroup;
import eu.cloudnetservice.v2.lib.server.SimpleServerGroup;
import eu.cloudnetservice.v2.lib.utility.document.Document;

import java.util.*;

/**
 * Created by Tareko on 19.07.2017.
 */
public class CloudNetwork {
    private int registeredPlayerCount = 0;
    private int onlineCount = 0;
    private Map<String, SimpleServerGroup> serverGroups = new HashMap<>();
    private Map<String, ProxyGroup> proxyGroups = new HashMap<>();
    private Collection<WrapperInfo> wrappers = new LinkedList<>();
    private Document messages = new Document();
    private Document modules = new Document();
    private int webPort = 1420;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CloudNetwork)) {
            return false;
        }

        final CloudNetwork that = (CloudNetwork) o;

        if (registeredPlayerCount != that.registeredPlayerCount) {
            return false;
        }
        if (onlineCount != that.onlineCount) {
            return false;
        }
        if (webPort != that.webPort) {
            return false;
        }
        if (!Objects.equals(serverGroups, that.serverGroups)) {
            return false;
        }
        if (!Objects.equals(proxyGroups, that.proxyGroups)) {
            return false;
        }
        if (!Objects.equals(wrappers, that.wrappers)) {
            return false;
        }
        if (!Objects.equals(messages, that.messages)) {
            return false;
        }
        return Objects.equals(modules, that.modules);
    }

    @Override
    public int hashCode() {
        int result = registeredPlayerCount;
        result = 31 * result + onlineCount;
        result = 31 * result + (serverGroups != null ? serverGroups.hashCode() : 0);
        result = 31 * result + (proxyGroups != null ? proxyGroups.hashCode() : 0);
        result = 31 * result + (wrappers != null ? wrappers.hashCode() : 0);
        result = 31 * result + (messages != null ? messages.hashCode() : 0);
        result = 31 * result + (modules != null ? modules.hashCode() : 0);
        result = 31 * result + webPort;
        return result;
    }

    @Override
    public String toString() {
        return "de.dytanic.cloudnet.lib.CloudNetwork{" +
            "registeredPlayerCount=" + registeredPlayerCount +
            ", onlineCount=" + onlineCount +
            ", serverGroups=" + serverGroups +
            ", proxyGroups=" + proxyGroups +
            ", wrappers=" + wrappers +
            ", messages=" + messages +
            ", modules=" + modules +
            ", webPort=" + webPort +
            '}';
    }

    public int getOnlineCount() {
        return onlineCount;
    }

    public void setOnlineCount(int onlineCount) {
        this.onlineCount = onlineCount;
    }

    public Map<String, ProxyGroup> getProxyGroups() {
        return proxyGroups;
    }

    public void setProxyGroups(Map<String, ProxyGroup> proxyGroups) {
        this.proxyGroups = proxyGroups;
    }

    public int getWebPort() {
        return webPort;
    }

    public void setWebPort(int webPort) {
        this.webPort = webPort;
    }

    public Collection<WrapperInfo> getWrappers() {
        return wrappers;
    }

    public void setWrappers(Collection<WrapperInfo> wrappers) {
        this.wrappers = wrappers;
    }

    public Document getMessages() {
        return messages;
    }

    public void setMessages(Document messages) {
        this.messages = messages;
    }

    public Document getModules() {
        return modules;
    }

    public void setModules(Document modules) {
        this.modules = modules;
    }

    public int getRegisteredPlayerCount() {
        return registeredPlayerCount;
    }

    public void setRegisteredPlayerCount(int registeredPlayerCount) {
        this.registeredPlayerCount = registeredPlayerCount;
    }

    public Map<String, SimpleServerGroup> getServerGroups() {
        return serverGroups;
    }

    public void setServerGroups(Map<String, SimpleServerGroup> serverGroups) {
        this.serverGroups = serverGroups;
    }
}
