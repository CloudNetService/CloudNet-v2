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

package eu.cloudnetservice.cloudnet.v2.lib.serverselectors.mob;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class MobConfig {

    public static final Type TYPE = TypeToken.get(MobConfig.class).getType();
    private final int inventorySize;

    private final int startPoint;

    private final MobItemLayout itemLayout;

    private final Map<Integer, MobItemLayout> defaultItemInventory;

    public MobConfig(int inventorySize, int startPoint, MobItemLayout itemLayout, Map<Integer, MobItemLayout> defaultItemInventory) {
        this.inventorySize = inventorySize;
        this.startPoint = startPoint;
        this.itemLayout = itemLayout;
        this.defaultItemInventory = defaultItemInventory;
    }

    public int getInventorySize() {
        return inventorySize;
    }

    public int getStartPoint() {
        return startPoint;
    }

    public Map<Integer, MobItemLayout> getDefaultItemInventory() {
        return defaultItemInventory;
    }

    public MobItemLayout getItemLayout() {
        return itemLayout;
    }
}
