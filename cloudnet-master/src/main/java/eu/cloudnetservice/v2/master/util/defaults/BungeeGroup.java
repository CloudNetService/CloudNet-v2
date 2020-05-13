package eu.cloudnetservice.v2.master.util.defaults;

import eu.cloudnetservice.v2.lib.map.WrappedMap;
import eu.cloudnetservice.v2.lib.server.ProxyGroup;
import eu.cloudnetservice.v2.lib.server.ProxyGroupMode;
import eu.cloudnetservice.v2.lib.server.template.Template;
import eu.cloudnetservice.v2.lib.server.template.TemplateResource;
import eu.cloudnetservice.v2.lib.server.version.ProxyVersion;

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
