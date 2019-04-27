package de.dytanic.cloudnet.lib.serverselectors.mob;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import de.dytanic.cloudnet.lib.utility.document.Document;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Tareko on 09.07.2017.
 */
@Getter
@Setter
@AllArgsConstructor
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

}