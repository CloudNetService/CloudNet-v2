/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol.packet;

/**
 * Created by Tareko on 25.07.2017.
 */
public final class PacketRC {

    public static final int TEST = -100;
    public static final int INTERNAL = 0;
    public static final int CN_WRAPPER = 100;
    public static final int CN_CORE = 200;
    public static final int PLAYER_HANDLE = 300;
    public static final int SERVER_HANDLE = 400;
    public static final int SERVER_SELECTORS = 500;
    public static final int DB = 600;
    public static final int CN_INTERNAL_CHANNELS = 700;
    public static final int API = 800;
    public static final int MODULE = 10000;

    private PacketRC() {
    }

}
