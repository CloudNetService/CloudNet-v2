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

package eu.cloudnetservice.cloudnet.v2.wrapper.handlers;

import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;
import eu.cloudnetservice.cloudnet.v2.wrapper.server.BungeeCord;
import eu.cloudnetservice.cloudnet.v2.wrapper.server.GameServer;

public final class StopTimeHandler implements IWrapperHandler {

    @Override
    public void accept(CloudNetWrapper wrapper) {
        for (GameServer gameServer : CloudNetWrapper.getInstance().getServers().values()) {
            try {
                if (!gameServer.isAlive()) {
                    if (System.currentTimeMillis() > (gameServer.getStartupTimeStamp() + 1600)) {
                        gameServer.shutdown();
                    } else {
                        gameServer.restart();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        for (BungeeCord bungeeCord : CloudNetWrapper.getInstance().getProxies().values()) {
            try {
                if (!bungeeCord.isAlive()) {
                    bungeeCord.shutdown();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

}
