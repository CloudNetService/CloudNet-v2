/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.lib.serverselectors.sign;

import java.util.Collection;

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
