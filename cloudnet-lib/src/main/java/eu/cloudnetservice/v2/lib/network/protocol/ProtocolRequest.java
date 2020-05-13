package eu.cloudnetservice.v2.lib.network.protocol;

/**
 * Created by Tareko on 09.09.2017.
 */
public class ProtocolRequest {

    private final int id;

    private final Object element;

    public ProtocolRequest(int id, Object element) {
        this.id = id;
        this.element = element;
    }

    public int getId() {
        return id;
    }

    public Object getElement() {
        return element;
    }
}