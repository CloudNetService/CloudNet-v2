package de.dytanic.cloudnet.lib.proxylayout;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Set;

public class ProxyConfig {

    private boolean enabled;

    private boolean maintenance;

    private List<Motd> motdsLayouts;

    private Motd maintenanceMotdLayout;

    // 2.2.0 - Fixed a typo, keep old name in alternatives for backwards compatibility.
    @SerializedName(value = "maintenanceProtocol", alternate = {"maintenaceProtocol"})
    private String maintenanceProtocol;

    private int maxPlayers;

    private boolean customPayloadFixer;

    private AutoSlot autoSlot;

    private TabList tabList;

    private List<String> playerInfo;

    private Set<String> whitelist;

    private DynamicFallback dynamicFallback;

    public ProxyConfig(boolean enabled,
                       boolean maintenance,
                       List<Motd> motdsLayouts,
                       Motd maintenanceMotdLayout,
                       String maintenanceProtocol,
                       int maxPlayers,
                       Boolean customPayloadFixer,
                       AutoSlot autoSlot,
                       TabList tabList,
                       List<String> playerInfo,
                       Set<String> whitelist,
                       DynamicFallback dynamicFallback) {
        this.enabled = enabled;
        this.maintenance = maintenance;
        this.motdsLayouts = motdsLayouts;
        this.maintenanceMotdLayout = maintenanceMotdLayout;
        this.maintenanceProtocol = maintenanceProtocol;
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

    public boolean getCustomPayloadFixer() {
        return customPayloadFixer;
    }

    public void setCustomPayloadFixer(boolean customPayloadFixer) {
        this.customPayloadFixer = customPayloadFixer;
    }

    public Set<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(Set<String> whitelist) {
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

    public String getMaintenanceProtocol() {
        return maintenanceProtocol;
    }

    public void setMaintenanceProtocol(String maintenanceProtocol) {
        this.maintenanceProtocol = maintenanceProtocol;
    }

    public List<String> getPlayerInfo() {
        return playerInfo;
    }

    public void setPlayerInfo(List<String> playerInfo) {
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
