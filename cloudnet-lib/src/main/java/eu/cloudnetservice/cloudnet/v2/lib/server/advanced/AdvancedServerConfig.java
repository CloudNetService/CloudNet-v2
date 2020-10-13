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

package eu.cloudnetservice.cloudnet.v2.lib.server.advanced;

public class AdvancedServerConfig {

    private final boolean notifyPlayerUpdatesFromNoCurrentPlayer;

    private final boolean notifyProxyUpdates;

    private final boolean notifyServerUpdates;

    private final boolean disableAutoSavingForWorlds;

    public AdvancedServerConfig(boolean notifyPlayerUpdatesFromNoCurrentPlayer,
                                boolean notifyProxyUpdates,
                                boolean notifyServerUpdates,
                                boolean disableAutoSavingForWorlds) {
        this.notifyPlayerUpdatesFromNoCurrentPlayer = notifyPlayerUpdatesFromNoCurrentPlayer;
        this.notifyProxyUpdates = notifyProxyUpdates;
        this.notifyServerUpdates = notifyServerUpdates;
        this.disableAutoSavingForWorlds = disableAutoSavingForWorlds;
    }

    public boolean isDisableAutoSavingForWorlds() {
        return disableAutoSavingForWorlds;
    }

    public boolean isNotifyPlayerUpdatesFromNoCurrentPlayer() {
        return notifyPlayerUpdatesFromNoCurrentPlayer;
    }

    public boolean isNotifyProxyUpdates() {
        return notifyProxyUpdates;
    }

    public boolean isNotifyServerUpdates() {
        return notifyServerUpdates;
    }
}
