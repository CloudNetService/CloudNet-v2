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

import java.util.List;

public class MobItemLayout implements Cloneable {

    /**
     * itemIds are not supported in all versions, use {@link MobItemLayout#itemName} instead
     */

    @Deprecated
    private final int itemId;
    private final String itemName;
    private final int subId;
    private final String display;
    private final List<String> lore;

    public MobItemLayout(int itemId, String itemName, int subId, String display, List<String> lore) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.subId = subId;
        this.display = display;
        this.lore = lore;
    }

    public int getSubId() {
        return subId;
    }

    public int getItemId() {
        return itemId;
    }

    public List<String> getLore() {
        return lore;
    }

    public String getDisplay() {
        return display;
    }

    public String getItemName() {
        return itemName;
    }

    @Override
    public MobItemLayout clone() {
        return new MobItemLayout(itemId, itemName, subId, display, lore);
    }
}
