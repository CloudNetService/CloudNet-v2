package de.dytanic.cloudnet.lib.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by Tareko on 29.06.2017.
 */
@Getter
@AllArgsConstructor
@ToString
public class WrapperInfo {

    private String serverId;
    private String hostName;
    private boolean ready;
    private int availableProcessors;
    private int startPort;
    private int process_queue_size;
    private int memory;

}
