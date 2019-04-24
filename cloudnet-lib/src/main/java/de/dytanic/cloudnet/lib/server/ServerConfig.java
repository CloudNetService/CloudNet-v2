package de.dytanic.cloudnet.lib.server;

import de.dytanic.cloudnet.lib.utility.document.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Tareko on 25.07.2017.
 */
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ServerConfig {

    private boolean hideServer;

    private String extra;

    private Document properties;

    private long startup;

}
