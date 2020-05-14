package eu.cloudnetservice.cloudnet.v2.lib.utility;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Class containing commonly used types for deserialization.
 */
public class CommonTypes {
    public static final Type LIST_STRING_TYPE = TypeToken.getParameterized(List.class, String.class).getType();
}
