package eu.cloudnetservice.v2.lib.serverselectors.mob;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by Tareko on 22.07.2017.
 */
public class MobConfig {

    public static final Type TYPE = TypeToken.get(MobConfig.class).getType();
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
