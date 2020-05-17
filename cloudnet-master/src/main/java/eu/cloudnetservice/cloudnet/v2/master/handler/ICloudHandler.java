package eu.cloudnetservice.cloudnet.v2.master.handler;

import eu.cloudnetservice.cloudnet.v2.master.CloudNet;

/**
 * Created by Tareko on 16.08.2017.
 */
public interface ICloudHandler extends Runnable {

    default void run() {
        onHandle(CloudNet.getInstance());
    }

    void onHandle(CloudNet cloudNet);

    int getTicks();

}
