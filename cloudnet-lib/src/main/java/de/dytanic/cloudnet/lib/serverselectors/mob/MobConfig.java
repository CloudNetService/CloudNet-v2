package de.dytanic.cloudnet.lib.serverselectors.mob;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * Created by Tareko on 22.07.2017.
 */
@Getter
@AllArgsConstructor
public class MobConfig {

    private int inventorySize;

    private int startPoint;

    private MobItemLayout itemLayout;

    private Map<Integer, MobItemLayout> defaultItemInventory;

}