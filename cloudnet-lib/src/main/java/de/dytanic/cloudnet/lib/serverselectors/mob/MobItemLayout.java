package de.dytanic.cloudnet.lib.serverselectors.mob;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Created by Tareko on 22.07.2017.
 */
@Getter
@AllArgsConstructor
public class MobItemLayout implements Cloneable {

    /**
     * itemIds are not supported in all versions, use {@link MobItemLayout#itemName} instead
     */

    @Deprecated
    private int itemId;
    private String itemName;
    private int subId;
    private String display;
    private List<String> lore;

    @Override
    public MobItemLayout clone()
    {
        return new MobItemLayout(itemId, itemName, subId, display, lore);
    }
}