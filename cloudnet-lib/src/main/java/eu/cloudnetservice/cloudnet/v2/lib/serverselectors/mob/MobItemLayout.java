package eu.cloudnetservice.cloudnet.v2.lib.serverselectors.mob;

import java.util.List;

/**
 * Created by Tareko on 22.07.2017.
 */
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
