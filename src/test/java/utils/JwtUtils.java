package utils;

import java.util.Base64;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JwtUtils {
    public static String extractUserIdFromToken(String token) {
        try {
            String[] chunks = token.split("\\.");
            if (chunks.length < 2) {
                throw new IllegalArgumentException("Invalid JWT token format");
            }
            String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));
            
            JsonObject jsonObject = JsonParser.parseString(payload).getAsJsonObject();
            return jsonObject.get("_id").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
