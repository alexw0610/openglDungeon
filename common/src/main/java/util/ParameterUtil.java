package util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class ParameterUtil {


    public static byte[] getParameterIfExistsDecoded(Map<String, String> parameters, String key) {
        if (parameters.containsKey(key)) {
            byte[] bytes = parameters.get(key).getBytes(StandardCharsets.UTF_8);
            return Base64.getDecoder().decode(bytes);
        }
        return null;
    }

    public static String getParameterIfExists(Map<String, String> parameters, String key) {
        if (parameters.containsKey(key)) {
            return parameters.get(key);
        }
        return null;
    }
}
