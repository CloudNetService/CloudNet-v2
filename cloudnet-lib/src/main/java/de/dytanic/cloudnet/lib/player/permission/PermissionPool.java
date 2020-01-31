package de.dytanic.cloudnet.lib.player.permission;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.PlayerConnection;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tareko on 01.06.2017.
 */
public class PermissionPool {

    public static final Type TYPE = TypeToken.get(PermissionPool.class).getType();

    private boolean available = true;

    private final Map<String, PermissionGroup> groups = new HashMap<>();
    private PermissionGroup defaultGroup;

    public PermissionPool() {
        getDefaultGroup();
    }

    public PermissionGroup getDefaultGroup() {
        if (this.defaultGroup == null) {
            for (PermissionGroup group : groups.values()) {
                if (group.isDefaultGroup()) {
                    this.defaultGroup = group;
                }
            }
        }
        return this.defaultGroup;
    }

    public PermissionEntity getNewPermissionEntity(PlayerConnection playerWhereAmI) {
        return new PermissionEntity(playerWhereAmI.getUniqueId(),
                                    new HashMap<>(),
                                    null,
                                    null,
                                    Collections.singletonList(new GroupEntityData(getDefaultGroup().getName(), 0L)));
    }

    public PermissionEntity getNewPermissionEntity(OfflinePlayer playerWhereAmI) {
        return new PermissionEntity(playerWhereAmI.getUniqueId(),
                                    new HashMap<>(),
                                    null,
                                    null,
                                    Collections.singletonList(new GroupEntityData(getDefaultGroup().getName(), 0L)));
    }

    public Map<String, PermissionGroup> getGroups() {
        return groups;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
