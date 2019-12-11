/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.proxylayout;

import java.util.Collection;
import java.util.List;

public class ProxyConfig {

    private boolean enabled;

    private boolean maintenance;

    private List<Motd> motdsLayouts;

    private Motd maintenanceMotdLayout;

    private String maintenaceProtocol;

    private int maxPlayers;

    private Boolean customPayloadFixer;

    private AutoSlot autoSlot;

    private TabList tabList;

    private String[] playerInfo;

    private Collection<String> whitelist;

    private DynamicFallback dynamicFallback;

    public ProxyConfig(boolean enabled,
                       boolean maintenance,
                       List<Motd> motdsLayouts,
                       Motd maintenanceMotdLayout,
                       String maintenaceProtocol,
                       int maxPlayers,
                       Boolean customPayloadFixer,
                       AutoSlot autoSlot,
                       TabList tabList,
                       String[] playerInfo,
                       Collection<String> whitelist,
                       DynamicFallback dynamicFallback) {
        this.enabled = enabled;
        this.maintenance = maintenance;
        this.motdsLayouts = motdsLayouts;
        this.maintenanceMotdLayout = maintenanceMotdLayout;
        this.maintenaceProtocol = maintenaceProtocol;
        this.maxPlayers = maxPlayers;
        this.customPayloadFixer = customPayloadFixer;
        this.autoSlot = autoSlot;
        this.tabList = tabList;
        this.playerInfo = playerInfo;
        this.whitelist = whitelist;
        this.dynamicFallback = dynamicFallback;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public AutoSlot getAutoSlot() {
        return autoSlot;
    }

    public void setAutoSlot(AutoSlot autoSlot) {
        this.autoSlot = autoSlot;
    }

    public Boolean getCustomPayloadFixer() {
        return customPayloadFixer;
    }

    public void setCustomPayloadFixer(Boolean customPayloadFixer) {
        this.customPayloadFixer = customPayloadFixer;
    }

    public Collection<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(Collection<String> whitelist) {
        this.whitelist = whitelist;
    }

    public DynamicFallback getDynamicFallback() {
        return dynamicFallback;
    }

    public void setDynamicFallback(DynamicFallback dynamicFallback) {
        this.dynamicFallback = dynamicFallback;
    }

    public List<Motd> getMotdsLayouts() {
        return motdsLayouts;
    }

    public void setMotdsLayouts(List<Motd> motdsLayouts) {
        this.motdsLayouts = motdsLayouts;
    }

    public Motd getMaintenanceMotdLayout() {
        return maintenanceMotdLayout;
    }

    public void setMaintenanceMotdLayout(Motd maintenanceMotdLayout) {
        this.maintenanceMotdLayout = maintenanceMotdLayout;
    }

    public String getMaintenaceProtocol() {
        return maintenaceProtocol;
    }

    public void setMaintenaceProtocol(String maintenaceProtocol) {
        this.maintenaceProtocol = maintenaceProtocol;
    }

    public String[] getPlayerInfo() {
        return playerInfo;
    }

    public void setPlayerInfo(String[] playerInfo) {
        this.playerInfo = playerInfo;
    }

    public TabList getTabList() {
        return tabList;
    }

    public void setTabList(TabList tabList) {
        this.tabList = tabList;
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
