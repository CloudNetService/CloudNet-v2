package de.dytanic.cloudnet.lib.network.auth;

import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;

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

    public Auth(String serviceKey, String cloudNetId) {
        this.type = AuthType.CLOUD_NET;
        this.authData.append("key", serviceKey).append("id", cloudNetId);
    }

    public Auth(ServiceId serverId) {
        this.type = AuthType.GAMESERVER_OR_BUNGEE;
        this.authData.append("serviceId", serverId);
    }

    public Auth(User user) {
        this.type = AuthType.GAMESERVER_OR_BUNGEE;
        this.authData.append("user", user);
    }

    public AuthType getType() {
        return type;
    }

    public Document getAuthData() {
        return authData;
    }
}
