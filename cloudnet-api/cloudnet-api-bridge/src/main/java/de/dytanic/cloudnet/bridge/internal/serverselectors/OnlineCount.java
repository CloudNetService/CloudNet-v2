package de.dytanic.cloudnet.bridge.internal.serverselectors;

public class OnlineCount {
    private final int onlineCount;
    private final int maxPlayers;

    public OnlineCount(final int onlineCount, final int maxPlayers) {
        this.onlineCount = onlineCount;
        this.maxPlayers = maxPlayers;
    }

    public int getOnlineCount() {
        return onlineCount;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public String toString() {
        return "de.dytanic.cloudnet.bridge.internal.serverselectors.OnlineCount{" +
            "onlineCount=" + onlineCount +
            ", maxPlayers=" + maxPlayers +
            '}';
    }
}
