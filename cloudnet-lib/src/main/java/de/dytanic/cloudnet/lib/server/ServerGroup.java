package de.dytanic.cloudnet.lib.server;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.interfaces.Nameable;
import de.dytanic.cloudnet.lib.map.WrappedMap;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;
import de.dytanic.cloudnet.lib.server.priority.PriorityConfig;
import de.dytanic.cloudnet.lib.server.priority.PriorityService;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Tareko on 21.05.2017.
 */
public class ServerGroup implements Nameable {

    public static final Type TYPE = new TypeToken<ServerGroup>() {}.getType();

    protected String name;
    protected Collection<String> wrapper;

    protected boolean kickedForceFallback;
    protected ServerGroupType serverType;
    protected ServerGroupMode groupMode;
    protected Template globalTemplate;
    protected Collection<Template> templates;

    protected int memory;
    protected int dynamicMemory;
    protected int joinPower;
    protected boolean maintenance;
    protected int minOnlineServers;
    protected int maxOnlineServers;
    protected AdvancedServerConfig advancedServerConfig;

    protected int percentForNewServerAutomatically;
    protected PriorityService priorityService;

    protected WrappedMap settings;

    public ServerGroup(String name,
                       Collection<String> wrapper,
                       boolean kickedForceFallback,
                       int memory,
                       int dynamicMemory,
                       int joinPower,
                       boolean maintenance,
                       int startup,
                       int priority,
                       int groupPriority,
                       int priorityStopTime,
                       int onlineCountForPriority,
                       int priorityForGroupOnlineCount,
                       int percentForNewServerAutomatically,
                       ServerGroupType serverType,
                       ServerGroupMode groupMode,
                       Collection<Template> templates,
                       AdvancedServerConfig advancedServerConfig) {
        this.name = name;
        this.kickedForceFallback = kickedForceFallback;
        this.wrapper = wrapper;
        this.memory = memory;
        this.dynamicMemory = dynamicMemory;
        this.joinPower = joinPower;
        this.maintenance = maintenance;
        this.minOnlineServers = startup;
        this.maxOnlineServers = -1;
        this.serverType = serverType;
        this.groupMode = groupMode;
        this.advancedServerConfig = advancedServerConfig;
        this.globalTemplate = new Template("globaltemplate", TemplateResource.LOCAL, null, new String[] {}, new ArrayList<>());
        this.templates = templates;

        this.settings = new WrappedMap();

        this.percentForNewServerAutomatically = percentForNewServerAutomatically;

        this.priorityService = new PriorityService(priorityStopTime,
                                                   new PriorityConfig(priority, onlineCountForPriority),
                                                   new PriorityConfig(groupPriority, priorityForGroupOnlineCount));

        this.templates = new ArrayList<>(Collections.singletonList(new Template("default",
                                                                                TemplateResource.LOCAL,
                                                                                null,
                                                                                new String[] {},
                                                                                new ArrayList<>())));
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<String> getWrapper() {
        return wrapper;
    }

    public void setWrapper(Collection<String> wrapper) {
        this.wrapper = wrapper;
    }

    public int getPercentForNewServerAutomatically() {
        return percentForNewServerAutomatically;
    }

    public void setPercentForNewServerAutomatically(int percentForNewServerAutomatically) {
        this.percentForNewServerAutomatically = percentForNewServerAutomatically;
    }

    public int getJoinPower() {
        return joinPower;
    }

    public void setJoinPower(int joinPower) {
        this.joinPower = joinPower;
    }

    public AdvancedServerConfig getAdvancedServerConfig() {
        return advancedServerConfig;
    }

    public void setAdvancedServerConfig(AdvancedServerConfig advancedServerConfig) {
        this.advancedServerConfig = advancedServerConfig;
    }

    public WrappedMap getSettings() {
        return settings;
    }

    public void setSettings(WrappedMap settings) {
        this.settings = settings;
    }

    public ServerGroupMode getGroupMode() {
        return groupMode;
    }

    public void setGroupMode(ServerGroupMode groupMode) {
        this.groupMode = groupMode;
    }

    public Collection<Template> getTemplates() {
        return templates;
    }

    public void setTemplates(Collection<Template> templates) {
        this.templates = templates;
    }

    public int getDynamicMemory() {
        return dynamicMemory;
    }

    public void setDynamicMemory(int dynamicMemory) {
        this.dynamicMemory = dynamicMemory;
    }

    public int getMaxOnlineServers() {
        return maxOnlineServers;
    }

    public void setMaxOnlineServers(int maxOnlineServers) {
        this.maxOnlineServers = maxOnlineServers;
    }

    public int getMinOnlineServers() {
        return minOnlineServers;
    }

    public void setMinOnlineServers(int minOnlineServers) {
        this.minOnlineServers = minOnlineServers;
    }

    public PriorityService getPriorityService() {
        return priorityService;
    }

    public void setPriorityService(PriorityService priorityService) {
        this.priorityService = priorityService;
    }

    public ServerGroupType getServerType() {
        return serverType;
    }

    public void setServerType(ServerGroupType serverType) {
        this.serverType = serverType;
    }

    public Template getGlobalTemplate() {
        return globalTemplate;
    }

    public void setGlobalTemplate(Template globalTemplate) {
        this.globalTemplate = globalTemplate;
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }

    public boolean isKickedForceFallback() {
        return kickedForceFallback;
    }

    public void setKickedForceFallback(boolean kickedForceFallback) {
        this.kickedForceFallback = kickedForceFallback;
    }

    public SimpleServerGroup toSimple() {
        return new SimpleServerGroup(name,
                                     kickedForceFallback,
                                     joinPower,
                                     memory,
                                     groupMode,
                                     maintenance,
                                     percentForNewServerAutomatically,
                                     settings,
                                     advancedServerConfig);
    }
}
