package eu.cloudnetservice.cloudnet.v2.master.api.event.network;

import eu.cloudnetservice.cloudnet.v2.event.async.AsyncEvent;
import eu.cloudnetservice.cloudnet.v2.event.async.AsyncPosterAdapter;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;

/**
 * Calls if the CloudNet instance is successfully started but, the console is not online
 */
public class CloudInitEvent extends AsyncEvent<CloudInitEvent> {

    private final CloudNet cloudNet = CloudNet.getInstance();

    public CloudInitEvent() {
        super(new AsyncPosterAdapter<>());
    }

    public CloudNet getCloudNet() {
        return cloudNet;
    }
}
