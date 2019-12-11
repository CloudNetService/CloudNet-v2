/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.util.defaults;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.proxylayout.*;

import java.util.ArrayList;
import java.util.Arrays;

public class BasicProxyConfig extends ProxyConfig {

    public BasicProxyConfig() {
        super(true,
              false,
              Arrays.asList(new Motd("   §b§lCloud§f§lNet§8■ §7your §bfree §7cloudsystem §8§l【§f%version%§8§l】",
                                     "         §aOnline §8» §7We are now §aavailable §7for §ball")),
              new Motd("   §b§lCloud§f§lNet§8■ §7your §bfree §7cloudsystem §8§l【§f%version%§8§l】",
                       "         §bMaintenance §8» §7We are still in §bmaintenance"),
              "§8➜ §bMaintenance §8§l【§c✘§8§l】",
              1000,
              true,
              new AutoSlot(0, false),
              new TabList(true,
                          " \n§b§lCloud§f§lNet §8× §7your §bfree §7cloudsystem §8➜ §f%online_players%§8/§f%max_players%§f\n §8► §7Current server §8● §b%server% §8◄ \n ",
                          " \n §7Twitter §8» §f@Dytanic §8▎ §7Discord §8» §fdiscord.gg/UNQ4wET \n §7powered by §8» §b§b§lCloud§f§lNet \n "),
              new String[] {NetworkUtils.SPACE_STRING, "§b§lCloud§f§lNet §8× §7your §bfree §7cloudsystem", "§7Twitter §8» §f@CloudNetService", "§7Discord §8» §fdiscord.gg/UNQ4wET", NetworkUtils.SPACE_STRING},
              new ArrayList<>(),
              new DynamicFallback("Lobby", Arrays.asList(new ServerFallback("Lobby", null))));
    }
}
