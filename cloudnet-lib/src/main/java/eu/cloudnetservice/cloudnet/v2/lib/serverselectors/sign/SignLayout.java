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

import eu.cloudnetservice.cloudnet.v2.lib.interfaces.Nameable;

public class SignLayout implements Nameable {

    /**
     * blockIds are not supported in all versions, use {@link SignLayout#blockName} instead
     */
    @Deprecated
    int blockId;
    private final String name;
    private final String[] signLayout;
    private final String blockName;
    private final int subId;

    public SignLayout(String name, String[] signLayout, int blockId, String blockName, int subId) {
        this.name = name;
        this.signLayout = signLayout;
        this.blockId = blockId;
        this.blockName = blockName;
        this.subId = subId;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getBlockId() {
        return blockId;
    }

    public int getSubId() {
        return subId;
    }

    public String getBlockName() {
        return blockName;
    }

    public String[] getSignLayout() {
        return signLayout;
    }
}
