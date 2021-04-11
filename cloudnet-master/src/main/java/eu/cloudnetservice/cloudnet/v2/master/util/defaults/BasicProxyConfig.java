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

package eu.cloudnetservice.cloudnet.v2.master.util.defaults;

import eu.cloudnetservice.cloudnet.v2.lib.proxylayout.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class BasicProxyConfig extends ProxyConfig {

    public BasicProxyConfig() {
        super(true,
              false,
              new ArrayList<>(Collections.singletonList(new Motd("   §b§lCloud§f§lNet§8■ §7your §bfree §7cloudsystem §8§l【§f%version%§8§l】",
                                                                 "         §aOnline §8» §7We are now §aavailable §7for §ball"))),
              new Motd("   §b§lCloud§f§lNet§8■ §7your §bfree §7cloudsystem §8§l【§f%version%§8§l】",
                       "         §bMaintenance §8» §7We are still in §bmaintenance"),
              "§8➜ §bMaintenance §8§l【§c✘§8§l】",
              1000,
              true,
              new AutoSlot(0, false),
              new TabList(true,
                          " \n§b§lCloud§f§lNet §8× §7your §bfree §7cloudsystem §8➜ §f%online_players%§8/§f%max_players%§f\n §8► §7Current server §8● §b%server% §8◄ \n ",
                          " \n §7Twitter §8» §f@CloudNetService §8▎ §7Discord §8» §fdiscord.cloudnetservice.eu \n §7powered by §8» §b§b§lCloud§f§lNet \n "),
              new ArrayList<>(Arrays.asList("",
                                            "§b§lCloud§f§lNet §8× §7your §bfree §7cloudsystem",
                                            "§7Twitter §8» §f@CloudNetService",
                                            "§7Discord §8» §fdiscord.cloudnetservice.eu",
                                            "")),
              new HashSet<>(),
              new DynamicFallback("Lobby", new ArrayList<>(Collections.singletonList(new ServerFallback("Lobby", null)))));
    }
}
