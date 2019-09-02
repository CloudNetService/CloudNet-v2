package de.dytanic.cloudnet.lib.utility.document;

import com.google.gson.JsonElement;
import de.dytanic.cloudnet.lib.interfaces.Nameable;

import java.io.File;
import java.util.Set;

/**
 * Created by Tareko on 21.05.2017.
 */
public interface DocumentAbstract extends Nameable {

    <T extends DocumentAbstract> T append(String key, String value);

    <T extends DocumentAbstract> T append(String key, Number value);

    <T extends DocumentAbstract> T append(String key, Boolean value);

    <T extends DocumentAbstract> T append(String key, JsonElement value);

    @Deprecated
    <T extends DocumentAbstract> T append(String key, Object value);

    <T extends DocumentAbstract> T remove(String key);

    Set<String> keys();

    String getString(String key);

    int getInt(String key);

    long getLong(String key);

    double getDouble(String key);

    boolean getBoolean(String key);

    float getFloat(String key);

    short getShort(String key);

    String convertToJson();

    boolean saveAsConfig(File backend);

    boolean saveAsConfig(String path);

    <T extends DocumentAbstract> T getDocument(String key);

}
