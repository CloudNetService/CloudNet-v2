package de.dytanic.cloudnet.lib.network.auth;

/**
 * Created by Tareko on 25.07.2017.
 */
public class AuthLoginResult {

    private boolean value;

    public AuthLoginResult(boolean value) {
        this.value = value;
    }

    public boolean isValue() {
        return value;
    }
}