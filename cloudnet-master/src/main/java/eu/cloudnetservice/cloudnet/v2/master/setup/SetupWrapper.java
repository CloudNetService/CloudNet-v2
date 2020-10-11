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

package eu.cloudnetservice.cloudnet.v2.master.setup;

import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.WrapperMeta;
import eu.cloudnetservice.cloudnet.v2.setup.Setup;
import eu.cloudnetservice.cloudnet.v2.setup.SetupRequest;
import eu.cloudnetservice.cloudnet.v2.setup.responsetype.StringResponseType;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;

public class SetupWrapper {

    private final String name;
    private final Setup setup;

    public SetupWrapper(CommandSender commandSender, String name) {
        this.name = name;

        setup = new Setup().setupCancel(() -> System.out.println("Setup was cancelled"))
                           .setupComplete(data -> {
                               try {
                                   InetAddress host = InetAddress.getByName(data.getString("address"));
                                   String user = data.getString("user");

                                   WrapperMeta wrapperMeta = new WrapperMeta(name, host, user);
                                   CloudNet.getInstance().getConfig().createWrapper(wrapperMeta);
                                   commandSender.sendMessage(String.format("Wrapper [%s] was registered on CloudNet",
                                                                           wrapperMeta.getId()));
                               } catch (UnknownHostException exception) {
                                   CloudNet.getLogger().log(Level.SEVERE, "Could not resolve hostname!", exception);
                               }
                           });

        InetAddressValidator validator = new InetAddressValidator();

        setup.request(new SetupRequest("address",
                                       "What is the IP address of this wrapper?",
                                       "The specified IP address is invalid!",
                                       StringResponseType.getInstance(),
                                       validator::isValid));
        setup.request(new SetupRequest("user",
                                       "What is the user of the wrapper?",
                                       "The specified user does not exist!",
                                       StringResponseType.getInstance(),
                                       key -> CloudNet.getInstance().getUser(key) != null));
    }

    public String getName() {
        return name;
    }

    public void startSetup() {
        setup.start(CloudNet.getLogger().getReader());
    }
}
