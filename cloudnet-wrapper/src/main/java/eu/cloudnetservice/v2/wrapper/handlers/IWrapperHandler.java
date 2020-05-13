package eu.cloudnetservice.v2.wrapper.handlers;

import eu.cloudnetservice.v2.wrapper.CloudNetWrapper;

import java.util.function.Consumer;

public interface IWrapperHandler extends Consumer<CloudNetWrapper> {

    default Runnable toExecutor() {
        return () -> accept(CloudNetWrapper.getInstance());
    }
}
