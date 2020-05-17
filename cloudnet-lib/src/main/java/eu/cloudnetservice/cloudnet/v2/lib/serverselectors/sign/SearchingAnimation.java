package eu.cloudnetservice.cloudnet.v2.lib.serverselectors.sign;

import java.util.Collection;

public class SearchingAnimation {

    private final int animations;

    private final int animationsPerSecond;

    private final Collection<SignLayout> searchingLayouts;

    public SearchingAnimation(int animations, int animationsPerSecond, Collection<SignLayout> searchingLayouts) {
        this.animations = animations;
        this.animationsPerSecond = animationsPerSecond;
        this.searchingLayouts = searchingLayouts;
    }

    public Collection<SignLayout> getSearchingLayouts() {
        return searchingLayouts;
    }

    public int getAnimations() {
        return animations;
    }

    public int getAnimationsPerSecond() {
        return animationsPerSecond;
    }
}