/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol;

/**
 * Created by Tareko on 09.09.2017.
 */
public abstract class ProtocolStream {

    public ProtocolStream() {
    }

    public abstract void write(ProtocolBuffer out) throws Exception;

    public abstract void read(ProtocolBuffer in) throws Exception;

}
