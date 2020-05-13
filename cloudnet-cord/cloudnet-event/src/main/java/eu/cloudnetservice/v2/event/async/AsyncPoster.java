package eu.cloudnetservice.v2.event.async;

/**
 * Interface for calling events before and after an event has been posted.
 *
 * @param <E> the type of the event
 */
public interface AsyncPoster<E extends AsyncEvent> {

    /**
     * Method that is called before an event is posted.
     *
     * @param event the event that will be posted
     */
    void onPreCall(E event);

    /**
     * Method that is called after an event has been posted and handled.
     *
     * @param event the event that has been posted
     */
    void onPostCall(E event);

}
