package de.dytanic.cloudnet.lib.serverselectors.mob;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.UUID;

/**
 * Created by Tareko on 09.07.2017.
 */
public class ServerMob implements Nameable {

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

    public ServerMob(UUID uniqueId, String display, String name, String type, String targetGroup, Integer itemId, String itemName, Boolean autoJoin, MobPosition position, String displayMessage, Document metaDataDoc) {
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

    public String getDisplay() {
        return display;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getTargetGroup() {
        return targetGroup;
    }

    public Boolean getAutoJoin() {
        return autoJoin;
    }

    public Document getMetaDataDoc() {
        return metaDataDoc;
    }

    public Integer getItemId() {
        return itemId;
    }

    public MobPosition getPosition() {
        return position;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void setAutoJoin(Boolean autoJoin) {
        this.autoJoin = autoJoin;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setMetaDataDoc(Document metaDataDoc) {
        this.metaDataDoc = metaDataDoc;
    }

    public void setPosition(MobPosition position) {
        this.position = position;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }
}