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

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;

public class ExampleModuleCommand extends Command {


    private final ModuleExample moduleExample;

    /**
     * Constructs a new command with a name, a needed permission and variable aliases.
     *
     * @param name       the name of this command
     * @param permission the permission a user has to have
     * @param aliases    other names of this command
     */
    public ExampleModuleCommand(ModuleExample moduleExample, String name, String permission, String... aliases) {
        super(name, permission, aliases);
        this.moduleExample = moduleExample;
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        this.moduleExample.getModuleLogger().warning(String.format("ExampleCommand Sender: %s",sender.getName()));
    }
}
