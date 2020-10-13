/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.lib.server;

import com.google.gson.reflect.TypeToken;
import eu.cloudnetservice.cloudnet.v2.lib.interfaces.Nameable;
import eu.cloudnetservice.cloudnet.v2.lib.map.WrappedMap;
import eu.cloudnetservice.cloudnet.v2.lib.server.advanced.AdvancedServerConfig;
import eu.cloudnetservice.cloudnet.v2.lib.server.priority.PriorityConfig;
import eu.cloudnetservice.cloudnet.v2.lib.server.priority.PriorityService;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.Template;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

public class ServerGroup implements Nameable {

    public static final Type TYPE = TypeToken.get(ServerGroup.class).getType();

    protected String name;
    protected Collection<String> wrapper;

    protected boolean kickedForceFallback;
    protected ServerGroupType serverType;
    protected ServerGroupMode groupMode;
    protected Template globalTemplate;
    protected List<Template> templates;

    protected int memory;
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
                       Template globalTemplate,
                       List<Template> templates,
                       AdvancedServerConfig advancedServerConfig) {
        this.name = name;
        this.kickedForceFallback = kickedForceFallback;
        this.wrapper = wrapper;
        this.memory = memory;
        this.joinPower = joinPower;
        this.maintenance = maintenance;
        this.minOnlineServers = startup;
        this.maxOnlineServers = -1;
        this.serverType = serverType;
        this.groupMode = groupMode;
        this.advancedServerConfig = advancedServerConfig;
        this.globalTemplate = globalTemplate;
        this.templates = templates;

        this.settings = new WrappedMap();

        this.percentForNewServerAutomatically = percentForNewServerAutomatically;

        this.priorityService = new PriorityService(priorityStopTime,
                                                   new PriorityConfig(priority, onlineCountForPriority),
                                                   new PriorityConfig(groupPriority, priorityForGroupOnlineCount));
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

    public List<Template> getTemplates() {
        return templates;
    }

    public void setTemplates(List<Template> templates) {
        this.templates = templates;
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
