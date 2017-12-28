/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol.packet.result;

import de.dytanic.cloudnet.lib.utility.document.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by Tareko on 26.07.2017.
 */
@Getter
@AllArgsConstructor
public class Result {

    private UUID uniqueId;

    private Document result;

}