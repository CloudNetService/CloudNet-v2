package de.dytanic.cloudnet.lib.serverselectors.sign;

import de.dytanic.cloudnet.lib.interfaces.Nameable;

/**
 * Created by Tareko on 26.05.2017.
 */
public class SignLayout implements Nameable {

    /**
     * blockIds are not supported in all versions, use {@link SignLayout#blockName} instead
     */
    @Deprecated
    int blockId;
    private String name;
    private String[] signLayout;
    private String blockName;
    private int subId;

    public SignLayout(String name, String[] signLayout, int blockId, String blockName, int subId) {
        this.name = name;
        this.signLayout = signLayout;
        this.blockId = blockId;
        this.blockName = blockName;
        this.subId = subId;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getBlockId() {
        return blockId;
    }

    public int getSubId() {
        return subId;
    }

    public String getBlockName() {
        return blockName;
    }

    public String[] getSignLayout() {
        return signLayout;
    }
}
