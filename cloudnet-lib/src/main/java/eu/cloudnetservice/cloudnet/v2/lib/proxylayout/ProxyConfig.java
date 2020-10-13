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

package eu.cloudnetservice.cloudnet.v2.lib.proxylayout;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;
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

    @Override
    public int hashCode() {
        int result = (enabled ? 1 : 0);
        result = 31 * result + (maintenance ? 1 : 0);
        result = 31 * result + (motdsLayouts != null ? motdsLayouts.hashCode() : 0);
        result = 31 * result + (maintenanceMotdLayout != null ? maintenanceMotdLayout.hashCode() : 0);
        result = 31 * result + (maintenanceProtocol != null ? maintenanceProtocol.hashCode() : 0);
        result = 31 * result + maxPlayers;
        result = 31 * result + (customPayloadFixer ? 1 : 0);
        result = 31 * result + (autoSlot != null ? autoSlot.hashCode() : 0);
        result = 31 * result + (tabList != null ? tabList.hashCode() : 0);
        result = 31 * result + (playerInfo != null ? playerInfo.hashCode() : 0);
        result = 31 * result + (whitelist != null ? whitelist.hashCode() : 0);
        result = 31 * result + (dynamicFallback != null ? dynamicFallback.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProxyConfig)) {
            return false;
        }

        final ProxyConfig that = (ProxyConfig) o;

        if (enabled != that.enabled) {
            return false;
        }
        if (maintenance != that.maintenance) {
            return false;
        }
        if (maxPlayers != that.maxPlayers) {
            return false;
        }
        if (customPayloadFixer != that.customPayloadFixer) {
            return false;
        }
        if (!Objects.equals(motdsLayouts, that.motdsLayouts)) {
            return false;
        }
        if (!Objects.equals(maintenanceMotdLayout, that.maintenanceMotdLayout)) {
            return false;
        }
        if (!Objects.equals(maintenanceProtocol, that.maintenanceProtocol)) {
            return false;
        }
        if (!Objects.equals(autoSlot, that.autoSlot)) {
            return false;
        }
        if (!Objects.equals(tabList, that.tabList)) {
            return false;
        }
        if (!Objects.equals(playerInfo, that.playerInfo)) {
            return false;
        }
        if (!Objects.equals(whitelist, that.whitelist)) {
            return false;
        }
        return Objects.equals(dynamicFallback, that.dynamicFallback);
    }

    @Override
    public String toString() {
        return "ProxyConfig{" +
            "enabled=" + enabled +
            ", maintenance=" + maintenance +
            ", motdsLayouts=" + motdsLayouts +
            ", maintenanceMotdLayout=" + maintenanceMotdLayout +
            ", maintenanceProtocol='" + maintenanceProtocol + '\'' +
            ", maxPlayers=" + maxPlayers +
            ", customPayloadFixer=" + customPayloadFixer +
            ", autoSlot=" + autoSlot +
            ", tabList=" + tabList +
            ", playerInfo=" + playerInfo +
            ", whitelist=" + whitelist +
            ", dynamicFallback=" + dynamicFallback +
            '}';
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
