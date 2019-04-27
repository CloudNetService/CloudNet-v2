/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.proxylayout;

import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProxyConfig {

    private boolean enabled;

    private boolean maintenance;

    private List<Motd> motdsLayouts;

    private Motd maintenanceMotdLayout;

    private String maintenaceProtocol;

    private int maxPlayers;

    private boolean fastConnect;

    private Boolean customPayloadFixer;

    private AutoSlot autoSlot;

    private TabList tabList;

    private String[] playerInfo;

    private Collection<String> whitelist;

    private DynamicFallback dynamicFallback;

}