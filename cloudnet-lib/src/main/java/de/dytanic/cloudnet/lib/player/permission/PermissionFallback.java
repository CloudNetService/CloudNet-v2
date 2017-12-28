package de.dytanic.cloudnet.lib.player.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 06.07.2017.
 */
@Getter
@AllArgsConstructor
public class PermissionFallback {

    private boolean enabled;
    private String fallback;

}