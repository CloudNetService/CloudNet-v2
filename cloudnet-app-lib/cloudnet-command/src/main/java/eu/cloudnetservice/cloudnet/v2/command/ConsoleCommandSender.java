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

/**
 * Class that defines a command sender in a terminal.
 * An instance of this class has all permissions, a random UUID and the name {@code CONSOLE}
 */
public class ConsoleCommandSender implements CommandSender {

    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public void sendMessage(String... message) {
        for (final String s : message) {
            System.out.println(s);
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return true; // CONSOLE has all permissions
    }

}
