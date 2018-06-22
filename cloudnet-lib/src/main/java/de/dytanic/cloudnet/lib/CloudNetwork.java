package de.dytanic.cloudnet.lib;

import de.dytanic.cloudnet.lib.network.WrapperInfo;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.SimpleServerGroup;
import de.dytanic.cloudnet.lib.utility.document.Document;
import lombok.Data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

/**
 * Created by Tareko on 19.07.2017.
 */
@Data
public class CloudNetwork {

    private int onlineCount = 0;
    private java.util.Map<String, SimpleServerGroup> serverGroups = new HashMap<>();
    private java.util.Map<String, ProxyGroup> proxyGroups = new HashMap<>();
    private java.util.Collection<WrapperInfo> wrappers = new LinkedList<>();
    private Document messages = new Document();
    private Document modules = new Document();
    private int webPort = 1420;

}