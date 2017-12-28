/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.serverselectors.sign;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class SearchingAnimation {

    private int animations;

    private int animationsPerSecond;

    private Collection<SignLayout> searchingLayouts;

}