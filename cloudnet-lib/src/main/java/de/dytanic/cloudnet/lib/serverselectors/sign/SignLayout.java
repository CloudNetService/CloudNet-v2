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
    private int blockId;
    private int subId;

}