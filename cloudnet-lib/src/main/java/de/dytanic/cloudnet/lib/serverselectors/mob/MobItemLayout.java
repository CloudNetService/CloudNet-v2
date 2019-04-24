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

    private int itemId;
    private int subId;
    private String display;
    private List<String> lore;

    @Override
    public MobItemLayout clone() {
        return new MobItemLayout(itemId, subId, display, lore);
    }
}