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

package eu.cloudnetservice.cloudnet.v2.help;

import eu.cloudnetservice.cloudnet.v2.lib.map.Maps;

/**
 * Class to organize help information and print it in a pretty way
 */
public final class HelpService {

    private final Maps.ArrayMap<String, ServiceDescription> descriptions = new Maps.ArrayMap<>();

    /**
     * Print the help directly to {@link System#out}
     */
    public void describe() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Help service of the Cloud:").append(Character.LINE_SEPARATOR);
        descriptions.forEach((key, value) -> {
            stringBuilder.append(key).append(':').append(Character.LINE_SEPARATOR);
            for (ServiceDescription description : value) {
                stringBuilder.append("Usage: ")
                             .append(description.getUsage())
                             .append(Character.LINE_SEPARATOR)
                             .append("Description: ")
                             .append(description.getDescription())
                             .append(Character.LINE_SEPARATOR)
                             .append(Character.LINE_SEPARATOR);
            }
        });
        return stringBuilder.toString();
    }

    public Maps.ArrayMap<String, ServiceDescription> getDescriptions() {
        return descriptions;
    }
}
