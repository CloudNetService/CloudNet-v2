package de.dytanic.cloudnet.lib.serverselectors.sign;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 22.07.2017.
 */
@Getter
@AllArgsConstructor
public class SignLayoutConfig {

    private boolean fullServerHide;

    private boolean knockbackOnSmallDistance;

    private double distance;

    private double strength;

    private Collection<SignGroupLayouts> groupLayouts;

    private SearchingAnimation searchingAnimation;

}