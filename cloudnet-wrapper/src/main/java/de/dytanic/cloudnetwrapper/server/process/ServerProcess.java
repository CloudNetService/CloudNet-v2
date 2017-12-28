/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.server.process;

import de.dytanic.cloudnet.lib.server.ServerProcessMeta;
import de.dytanic.cloudnetwrapper.server.ServerStage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class ServerProcess {

    private ServerProcessMeta meta;

    @Setter
    private ServerStage serverStage;

}