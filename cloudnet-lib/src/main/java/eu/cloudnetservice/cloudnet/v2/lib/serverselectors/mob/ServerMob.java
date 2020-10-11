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
import eu.cloudnetservice.cloudnet.v2.lib.interfaces.Nameable;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

import java.lang.reflect.Type;
import java.util.UUID;

public class ServerMob implements Nameable {

    public static final Type TYPE = TypeToken.get(ServerMob.class).getType();

    protected UUID uniqueId;
    protected String display;
    protected String name;
    protected String type;
    protected String targetGroup;
    /**
     * itemIds are not supported in all versions, use {@link ServerMob#itemName} instead
     */
    @Deprecated
    protected Integer itemId;
    protected String itemName;
    protected Boolean autoJoin;
    protected MobPosition position;
    protected String displayMessage;
    protected Document metaDataDoc;

    public ServerMob(UUID uniqueId,
                     String display,
                     String name,
                     String type,
                     String targetGroup,
                     Integer itemId,
                     String itemName,
                     Boolean autoJoin,
                     MobPosition position,
                     String displayMessage,
                     Document metaDataDoc) {
        this.uniqueId = uniqueId;
        this.display = display;
        this.name = name;
        this.type = type;
        this.targetGroup = targetGroup;
        this.itemId = itemId;
        this.itemName = itemName;
        this.autoJoin = autoJoin;
        this.position = position;
        this.displayMessage = displayMessage;
        this.metaDataDoc = metaDataDoc;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }

    public Boolean getAutoJoin() {
        return autoJoin;
    }

    public void setAutoJoin(Boolean autoJoin) {
        this.autoJoin = autoJoin;
    }

    public Document getMetaDataDoc() {
        return metaDataDoc;
    }

    public void setMetaDataDoc(Document metaDataDoc) {
        this.metaDataDoc = metaDataDoc;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public MobPosition getPosition() {
        return position;
    }

    public void setPosition(MobPosition position) {
        this.position = position;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }
}
