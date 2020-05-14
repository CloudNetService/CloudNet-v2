package eu.cloudnetservice.cloudnet.v2.lib.server.version;

import eu.cloudnetservice.cloudnet.v2.lib.MultiValue;

/**
 * Created by Tareko on 27.08.2017.
 */
public enum ProxyVersion {

    TRAVERTINE,
    BUNGEECORD,
    WATERFALL,
    HEXACORD;

    public static MultiValue<String, String> url(ProxyVersion proxyVersion) {
        switch (proxyVersion) {
            case TRAVERTINE:
                return new MultiValue<>(
                    "https://papermc.io/ci/job/Travertine/lastSuccessfulBuild/artifact/Travertine-Proxy/bootstrap/target/Travertine.jar",
                    "Travertine.jar");
            case HEXACORD:
                return new MultiValue<>("https://github.com/HexagonMC/BungeeCord/releases/download/v258/BungeeCord.jar",
                                        "HexaCord.jar");
            case WATERFALL:
                return new MultiValue<>(
                    "https://ci.destroystokyo.com/job/Waterfall/lastSuccessfulBuild/artifact/Waterfall-Proxy/bootstrap/target/Waterfall.jar",
                    "Waterfall.jar");
            default:
                return new MultiValue<>("https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar",
                                        "BungeeCord.jar");
        }
    }

}
