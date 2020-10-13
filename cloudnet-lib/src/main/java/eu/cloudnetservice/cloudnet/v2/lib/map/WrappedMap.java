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

package eu.cloudnetservice.cloudnet.v2.lib.map;

import java.util.HashMap;

public final class WrappedMap extends HashMap<String, Object> {

    public WrappedMap append(String key, Object value) {
        put(key, value);
        return this;
    }

    public Integer getInt(String key) {
        if (!containsKey(key)) {
            return null;
        }
        return Integer.parseInt(get(key).toString());
    }

    public Boolean getBoolean(String key) {
        if (!containsKey(key)) {
            return null;
        }
        return Boolean.parseBoolean(get(key).toString());
    }

    public String getString(String key) {
        if (!containsKey(key)) {
            return null;
        }
        return get(key).toString();
    }

    public Double getDouble(String key) {
        if (!containsKey(key)) {
            return null;
        }
        return Double.parseDouble(get(key).toString());
    }

    public Float getFloat(String key) {
        if (!containsKey(key)) {
            return null;
        }
        return Float.parseFloat(get(key).toString());
    }
}
