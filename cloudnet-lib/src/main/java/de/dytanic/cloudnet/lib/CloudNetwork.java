package de.dytanic.cloudnet.lib;

import de.dytanic.cloudnet.lib.network.WrapperInfo;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.SimpleServerGroup;
import de.dytanic.cloudnet.lib.utility.document.Document;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Tareko on 19.07.2017.
 */
@ToString
@EqualsAndHashCode
public class CloudNetwork {

    private int registeredPlayerCount = 0;
    private int onlineCount = 0;
    private java.util.Map<String, SimpleServerGroup> serverGroups = new HashMap<>();
    private java.util.Map<String, ProxyGroup> proxyGroups = new HashMap<>();
    private java.util.Collection<WrapperInfo> wrappers = new LinkedList<>();
    private Document messages = new Document();
    private Document modules = new Document();
    private int webPort = 1420;

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
