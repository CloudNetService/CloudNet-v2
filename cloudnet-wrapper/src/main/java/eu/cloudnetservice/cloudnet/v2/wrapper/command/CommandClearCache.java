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

package eu.cloudnetservice.cloudnet.v2.wrapper.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.wrapper.util.FileUtility;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CommandClearCache extends Command {

    public CommandClearCache() {
        super("clearcache", "cloudnet.command.clearcache");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        try {
            FileUtility.deleteDirectory(new File("local/cache"));
            Files.createDirectories(Paths.get("local/cache/web_templates"));
            Files.createDirectories(Paths.get("local/cache/web_plugins"));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        sender.sendMessage("The Cache was cleared!");
    }
}
