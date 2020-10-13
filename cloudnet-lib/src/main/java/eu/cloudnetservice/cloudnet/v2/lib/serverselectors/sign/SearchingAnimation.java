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
