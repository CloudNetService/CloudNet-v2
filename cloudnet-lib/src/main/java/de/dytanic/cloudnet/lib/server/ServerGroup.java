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
import java.util.*;

/**
 * Created by Tareko on 21.05.2017.
 */
@Getter
@Setter
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

        this.templates = Arrays.asList(new Template("default", TemplateResource.LOCAL, null, new String[]{}, new ArrayList<>()));
    }

    public SimpleServerGroup toSimple()
    {
        return new SimpleServerGroup(name, kickedForceFallback, joinPower, memory, groupMode, maintenance, percentForNewServerAutomatically, settings, advancedServerConfig);
    }
}
