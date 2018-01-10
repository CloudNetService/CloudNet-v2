package de.dytanic.cloudnet.lib.player.permission;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.PlayerConnection;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Tareko on 01.06.2017.
 */
@Getter
public class PermissionPool {

    public static final Type TYPE = new TypeToken<PermissionPool>(){}.getType();

    @Setter private boolean available = true;

    private java.util.Map<String, PermissionGroup> groups = new HashMap<>();

    public PermissionGroup getDefaultGroup()
    {
        for(PermissionGroup group : groups.values())
        {
            if(group.isDefaultGroup())
            {
                return group;
            }
        }
        return null;
    }

    public PermissionEntity getNewPermissionEntity(PlayerConnection playerWhereAmI)
    {
        return new PermissionEntity(playerWhereAmI.getUniqueId(), new HashMap<>(), null, null, Arrays.asList(new GroupEntityData(getDefaultGroup().getName(), 0L)));
    }

    public PermissionEntity getNewPermissionEntity(OfflinePlayer playerWhereAmI)
    {
        return new PermissionEntity(playerWhereAmI.getUniqueId(), new HashMap<>(), null, null, Arrays.asList(new GroupEntityData(getDefaultGroup().getName(), 0L)));
    }

}