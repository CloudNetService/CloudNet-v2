/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.util.defaults;

import de.dytanic.cloudnet.lib.map.WrappedMap;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ProxyGroupMode;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.server.version.ProxyVersion;

import java.util.ArrayList;
import java.util.Collections;

public class BungeeGroup extends ProxyGroup {

    public static final String[] EMPTY_STRING_ARRAY = {};

    public BungeeGroup() {
        super("Bungee",
              Collections.singletonList("Wrapper-1"),
              new Template("default", TemplateResource.LOCAL, null, EMPTY_STRING_ARRAY, new ArrayList<>()),
              ProxyVersion.BUNGEECORD,
              25565,
              1,
              128,
              new BasicProxyConfig(),
              ProxyGroupMode.DYNAMIC,
              new WrappedMap());
    }
}
