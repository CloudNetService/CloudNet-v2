package de.dytanic.cloudnet.lib.serverselectors.sign;

import java.util.Collection;

/**
 * Created by Tareko on 22.07.2017.
 */
public class SignLayoutConfig {

    private boolean fullServerHide;

    private boolean knockbackOnSmallDistance;

    private double distance;

    private double strength;

    private Collection<SignGroupLayouts> groupLayouts;

    private SearchingAnimation searchingAnimation;

    public SignLayoutConfig(boolean fullServerHide,
                            boolean knockbackOnSmallDistance,
                            double distance,
                            double strength,
                            Collection<SignGroupLayouts> groupLayouts,
                            SearchingAnimation searchingAnimation) {
        this.fullServerHide = fullServerHide;
        this.knockbackOnSmallDistance = knockbackOnSmallDistance;
        this.distance = distance;
        this.strength = strength;
        this.groupLayouts = groupLayouts;
        this.searchingAnimation = searchingAnimation;
    }

    public Collection<SignGroupLayouts> getGroupLayouts() {
        return groupLayouts;
    }

    public double getDistance() {
        return distance;
    }

    public double getStrength() {
        return strength;
    }

    public SearchingAnimation getSearchingAnimation() {
        return searchingAnimation;
    }

    public boolean isFullServerHide() {
        return fullServerHide;
    }

    public boolean isKnockbackOnSmallDistance() {
        return knockbackOnSmallDistance;
    }
}
