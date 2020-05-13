package eu.cloudnetservice.v2.lib.network.auth;

import com.google.gson.reflect.TypeToken;
import eu.cloudnetservice.v2.lib.service.ServiceId;
import eu.cloudnetservice.v2.lib.user.User;
import eu.cloudnetservice.v2.lib.utility.document.Document;

import java.lang.reflect.Type;

/**
 * Created by Tareko on 22.07.2017.
 */
public final class Auth {

    public static final Type TYPE = TypeToken.get(Auth.class).getType();
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
