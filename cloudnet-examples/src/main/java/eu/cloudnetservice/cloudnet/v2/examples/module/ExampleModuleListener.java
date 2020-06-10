package eu.cloudnetservice.cloudnet.v2.examples.module;

import eu.cloudnetservice.cloudnet.v2.event.EventListener;
import eu.cloudnetservice.cloudnet.v2.master.api.event.server.ServerAddEvent;

public class ExampleModuleListener implements EventListener<ServerAddEvent> {

    private final ModuleExample moduleExample;

    public ExampleModuleListener(final ModuleExample moduleExample) {
        this.moduleExample = moduleExample;
    }

    @Override
    public void onCall(final ServerAddEvent event) {
        this.moduleExample.getModuleLogger().info(String.format("Server %s was added", event.getMinecraftServer().getName()));
    }
}
