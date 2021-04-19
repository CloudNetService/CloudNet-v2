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

import eu.cloudnetservice.cloudnet.v2.command.CommandManager;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.lib.utility.Constants;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.WrapperMeta;
import eu.cloudnetservice.cloudnet.v2.setup.Setup;
import eu.cloudnetservice.cloudnet.v2.setup.SetupRequest;
import eu.cloudnetservice.cloudnet.v2.setup.responsetype.StringResponseType;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;

/**
 * Created by Tareko on 21.10.2017.
 */
public class SetupWrapper extends Setup {

    private final String name;

    public SetupWrapper(CommandSender commandSender, String name) {
        super(CloudNet.getInstance().getConsoleManager());
        this.name = name;

        this.setupCancel(() -> {
                System.out.println("Setup was cancelled");
                CloudNet.getInstance().getConsoleManager().changeConsoleInput(CommandManager.class);
                CloudNet.getInstance().getConsoleRegistry().unregisterInput(SetupWrapper.class);
            })
            .setupComplete(data -> {
                String host = data.getString("address");
                String user = data.getString("user");
                try {
                    InetAddressValidator validator = new InetAddressValidator();
                    if (!validator.isValid(host)) {
                        throw new UnknownHostException("No valid InetAddress found!");
                    }
                    InetAddress hostInet = InetAddress.getByName(host);
                    WrapperMeta wrapperMeta = new WrapperMeta(name, hostInet, user);
                    CloudNet.getInstance().getConfig().createWrapper(wrapperMeta);
                    commandSender.sendMessage(String.format("Wrapper [%s] was registered on CloudNet",
                                                            wrapperMeta.getId()));

                    CloudNet.getInstance().getConsoleManager().changeConsoleInput(CommandManager.class);
                    CloudNet.getInstance().getConsoleRegistry().unregisterInput(SetupWrapper.class);
                } catch (UnknownHostException e) {
                    CloudNet.getLogger().log(Level.SEVERE, "Error create a new wrapper", e);
                }






            })
            .request(new SetupRequest("address",
                                      "What is the IP address of this wrapper?",
                                      "The specified IP address is invalid!",
                                      StringResponseType.getInstance(),
                                      key -> Constants.IPV4_PATTERN.matcher(key).matches()
                                          || Constants.IPV6_STD_PATTERN.matcher(key).matches()
                                          || Constants.IPV6_HEX_COMPRESSED_PATTERN.matcher(key).matches(), null
            ))
            .request(new SetupRequest("user",
                                      "What is the user of the wrapper?",
                                      "The specified user does not exist!",
                                      StringResponseType.getInstance(),
                                      key -> CloudNet.getInstance().getUser(key) != null, null
            ));
    }

    public String getName() {
        return name;
    }

}
