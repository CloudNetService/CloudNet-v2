package de.dytanic.cloudnet.lib.serverselectors.sign;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 26.05.2017.
 */
@AllArgsConstructor
@Getter
public class SignLayout
        implements Nameable {

    private String name;
    private String[] signLayout;
    /**
     * blockIds are not supported in all versions, use {@link SignLayout#blockName} instead
     */
    @Deprecated
    int blockId;
    private String blockName;
    private int subId;

}