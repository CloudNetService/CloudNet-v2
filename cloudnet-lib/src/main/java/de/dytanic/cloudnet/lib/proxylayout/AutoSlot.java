/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.proxylayout;

public class AutoSlot {

    private int dynamicSlotSize;

    private boolean enabled;

    public AutoSlot(int dynamicSlotSize, boolean enabled) {
        this.dynamicSlotSize = dynamicSlotSize;
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getDynamicSlotSize() {
        return dynamicSlotSize;
    }
}