package eu.cloudnetservice.cloudnet.v2.lib.serverselectors.sign;

import java.util.Collection;

/**
 * Created by Tareko on 22.07.2017.
 */
public class SignLayoutConfig {

    private final boolean fullServerHide;

    private final boolean knockbackOnSmallDistance;

    private final double distance;

    private final double strength;

    private final Collection<SignGroupLayouts> groupLayouts;

    private final SearchingAnimation searchingAnimation;

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
