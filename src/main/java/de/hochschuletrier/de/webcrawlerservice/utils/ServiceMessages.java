package de.hochschuletrier.de.webcrawlerservice.utils;

import org.codehaus.jettison.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains all success Messages which are needed to be sent to the Client
 *
 * @author: Oliver Fries
 */
public class ServiceMessages
{
    public static String requestSuccessful(String path, String message)
    {
        Map<String, String> jsonValues = new HashMap<>();
        jsonValues.put("status", "8001");
        jsonValues.put("result", "OK");
        jsonValues.put("message", message);
        jsonValues.put("path", path);
        return new JSONObject(jsonValues).toString();
    }
}
