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

package eu.cloudnetservice.cloudnet.v2.wrapper.server.process;

import eu.cloudnetservice.cloudnet.v2.lib.interfaces.Executable;
import eu.cloudnetservice.cloudnet.v2.wrapper.screen.Screenable;

import java.io.IOException;

public interface ServerDispatcher extends Executable, Screenable {

    default void executeCommand(String consoleCommand) {
        if (!isAlive()) {
            return;
        }

        try {
            getInstance().getOutputStream().write((consoleCommand + '\n').getBytes());
            getInstance().getOutputStream().flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    Process getInstance();

    default boolean isAlive() {
        try {
            return getInstance() != null && getInstance().isAlive() && getInstance().getInputStream().available() != -1;
        } catch (IOException e) {
            return false;
        }
    }

}
