package de.dytanic.cloudnet.lib.serverselectors.mob;

import java.util.Map;

/**
 * Created by Tareko on 22.07.2017.
 */
public class MobConfig {

    private int inventorySize;

    private int startPoint;

    private MobItemLayout itemLayout;

    private Map<Integer, MobItemLayout> defaultItemInventory;

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
