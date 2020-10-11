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

package eu.cloudnetservice.cloudnet.v2.lib.proxylayout;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DynamicFallback {

    private final String defaultFallback;

    private final List<ServerFallback> fallbacks;

    public DynamicFallback(String defaultFallback, List<ServerFallback> fallbacks) {
        this.defaultFallback = defaultFallback;
        this.fallbacks = fallbacks;
    }

    public List<ServerFallback> getFallbacks() {
        return fallbacks;
    }

    public String getDefaultFallback() {
        return defaultFallback;
    }

    public ServerFallback getDefault() {
        return fallbacks.stream()
                        .filter(fallback -> fallback.getGroup().equals(defaultFallback))
                        .findFirst().orElseThrow(() -> new IllegalStateException("No default fallback defined!"));
    }

    public Collection<String> getNamedFallbacks() {
        return this.fallbacks.stream()
                             .map(ServerFallback::getGroup)
                             .collect(Collectors.toList());
    }

}
