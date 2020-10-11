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

package eu.cloudnetservice.cloudnet.v2.examples.module;

import eu.cloudnetservice.cloudnet.v2.event.EventListener;
import eu.cloudnetservice.cloudnet.v2.master.api.event.server.ServerAddEvent;

public class ExampleModuleListener implements EventListener<ServerAddEvent> {

    private final ModuleExample moduleExample;

    public ExampleModuleListener(final ModuleExample moduleExample) {
        this.moduleExample = moduleExample;
    }

    @Override
    public void onCall(final ServerAddEvent event) {
        this.moduleExample.getModuleLogger().info(String.format("Server %s was added", event.getMinecraftServer().getName()));
    }
}
