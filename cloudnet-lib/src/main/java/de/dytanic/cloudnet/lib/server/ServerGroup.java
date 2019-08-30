package de.dytanic.cloudnet.lib.server;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.interfaces.Nameable;
import de.dytanic.cloudnet.lib.map.WrappedMap;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;
import de.dytanic.cloudnet.lib.server.priority.PriorityConfig;
import de.dytanic.cloudnet.lib.server.priority.PriorityService;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Tareko on 21.05.2017.
 */
public class ServerGroup
        implements Nameable {

    public static final Type TYPE = new TypeToken<ServerGroup>() {
    }.getType();

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

    public ServerGroup(String name, Collection<String> wrapper, boolean kickedForceFallback, int memory, int dynamicMemory, int joinPower, boolean maintenance, int startup,
                       int priority, int groupPriority, int priorityStopTime, int onlineCountForPriority, int priorityForGroupOnlineCount, int percentForNewServerAutomatically,
                       ServerGroupType serverType, ServerGroupMode groupMode, Collection<Template> templates, AdvancedServerConfig advancedServerConfig)
    {
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
        this.globalTemplate = new Template("globaltemplate", TemplateResource.LOCAL, null, new String[]{}, new ArrayList<>());
        this.templates = templates;

        this.settings = new WrappedMap();

        this.percentForNewServerAutomatically = percentForNewServerAutomatically;

        this.priorityService = new PriorityService(
                priorityStopTime,
                new PriorityConfig(priority, onlineCountForPriority),
                new PriorityConfig(groupPriority, priorityForGroupOnlineCount)
        );

        this.templates = new ArrayList<>(Collections.singletonList(new Template("default", TemplateResource.LOCAL, null, new String[]{}, new ArrayList<>())));
    }

    public int getMemory() {
        return memory;
    }

    @Override
    public String getName() {
        return name;
    }

    public Collection<String> getWrapper() {
        return wrapper;
    }

    public int getPercentForNewServerAutomatically() {
        return percentForNewServerAutomatically;
    }

    public int getJoinPower() {
        return joinPower;
    }

    public AdvancedServerConfig getAdvancedServerConfig() {
        return advancedServerConfig;
    }

    public WrappedMap getSettings() {
        return settings;
    }

    public ServerGroupMode getGroupMode() {
        return groupMode;
    }

    public Collection<Template> getTemplates() {
        return templates;
    }

    public int getDynamicMemory() {
        return dynamicMemory;
    }

    public int getMaxOnlineServers() {
        return maxOnlineServers;
    }

    public int getMinOnlineServers() {
        return minOnlineServers;
    }

    public PriorityService getPriorityService() {
        return priorityService;
    }

    public ServerGroupType getServerType() {
        return serverType;
    }

    public Template getGlobalTemplate() {
        return globalTemplate;
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public boolean isKickedForceFallback() {
        return kickedForceFallback;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTemplates(Collection<Template> templates) {
        this.templates = templates;
    }

    public void setAdvancedServerConfig(AdvancedServerConfig advancedServerConfig) {
        this.advancedServerConfig = advancedServerConfig;
    }

    public void setDynamicMemory(int dynamicMemory) {
        this.dynamicMemory = dynamicMemory;
    }

    public void setGlobalTemplate(Template globalTemplate) {
        this.globalTemplate = globalTemplate;
    }

    public void setGroupMode(ServerGroupMode groupMode) {
        this.groupMode = groupMode;
    }

    public void setJoinPower(int joinPower) {
        this.joinPower = joinPower;
    }

    public void setKickedForceFallback(boolean kickedForceFallback) {
        this.kickedForceFallback = kickedForceFallback;
    }

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }

    public void setMaxOnlineServers(int maxOnlineServers) {
        this.maxOnlineServers = maxOnlineServers;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public void setMinOnlineServers(int minOnlineServers) {
        this.minOnlineServers = minOnlineServers;
    }

    public void setServerType(ServerGroupType serverType) {
        this.serverType = serverType;
    }

    public void setPercentForNewServerAutomatically(int percentForNewServerAutomatically) {
        this.percentForNewServerAutomatically = percentForNewServerAutomatically;
    }

    public void setWrapper(Collection<String> wrapper) {
        this.wrapper = wrapper;
    }

    public void setPriorityService(PriorityService priorityService) {
        this.priorityService = priorityService;
    }

    public void setSettings(WrappedMap settings) {
        this.settings = settings;
    }

    public SimpleServerGroup toSimple()
    {
        return new SimpleServerGroup(name, kickedForceFallback, joinPower, memory, groupMode, maintenance, percentForNewServerAutomatically, settings, advancedServerConfig);
    }
}
