package eu.cloudnetservice.cloudnet.v2.lib.proxylayout;

public class AutoSlot {

    private final int dynamicSlotSize;

    private final boolean enabled;

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