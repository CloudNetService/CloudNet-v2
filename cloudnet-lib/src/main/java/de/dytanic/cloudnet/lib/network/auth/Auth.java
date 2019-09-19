package de.dytanic.cloudnet.lib.network.auth;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.Random;

/**
 * Created by Tareko on 22.07.2017.
 */
public final class Auth {

    private AuthType type;
    private Document authData = new Document();

    public Auth(AuthType type, Document authData) {
        this.type = type;
        this.authData = authData;
    }

    public Auth(String servicekey, String cn_id) {
        this.type = AuthType.CLOUD_NET;
        this.authData.append("key", servicekey).append("id", cn_id);
    }

    public Auth(ServiceId serverId) {
        this.type = AuthType.GAMESERVER_OR_BUNGEE;
        this.authData.append("serviceId", serverId);
    }

    public Auth(User user) {
        this.type = AuthType.GAMESERVER_OR_BUNGEE;
        this.authData.append("user", user);
    }

    public Auth(ServiceId serverId, boolean external) {
        this.type = AuthType.GAMESERVER_OR_BUNGEE;
        this.authData.append("serviceId", serverId);
        if (external) {
            this.authData.append("external", "1805 4646");
        }
    }

    public Auth(String adminKey) {
        this.type = AuthType.USER_AUTH;
        this.authData.append("name", new Random().nextLong() + NetworkUtils.EMPTY_STRING).append("adminkey", adminKey);
    }

    public AuthType getType() {
        return type;
    }

    public Document getAuthData() {
        return authData;
    }
}
