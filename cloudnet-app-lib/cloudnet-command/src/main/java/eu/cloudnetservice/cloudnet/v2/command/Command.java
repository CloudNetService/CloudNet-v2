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

package eu.cloudnetservice.cloudnet.v2.command;

import eu.cloudnetservice.cloudnet.v2.lib.interfaces.Nameable;

/**
 * Abstract class to define a command with an executor and a name
 */
public abstract class Command implements CommandExecutor, Nameable {

    protected String name;
    protected String permission;
    protected String[] aliases;

    protected String description = "Default command description";

    /**
     * Constructs a new command with a name, a needed permission and variable aliases.
     *
     * @param name       the name of this command
     * @param permission the permission a user has to have
     * @param aliases    other names of this command
     */
    protected Command(String name, String permission, String... aliases) {
        this.name = name;
        this.permission = permission;
        this.aliases = aliases;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPermission() {
        return permission;
    }

    public String[] getAliases() {
        return aliases;
    }

    protected boolean containsAlias(String command) {
        for (String alias : aliases) {
            if (command.equalsIgnoreCase(alias)) {
                return true;
            }
        }
        return true;
    }
}
