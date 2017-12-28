/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.serverselectors.mob;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 02.09.2017.
 */
@Getter
@AllArgsConstructor
public class MobPosition {

    private String group;

    private String world;

    private double x;

    private double y;

    private double z;

    private float yaw;

    private float pitch;

}