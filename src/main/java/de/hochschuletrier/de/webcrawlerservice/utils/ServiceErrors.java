package de.hochschuletrier.de.webcrawlerservice.utils;

import org.codehaus.jettison.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains all occurring Error messages, which must be send to the Client as JSON
 *
 * @author: Oliver Fries
 */
public class ServiceErrors
{
    public static String missingCrawlerValues(String path, GenericCrawlerData crawlerData)
    {
        Map<String, String> jsonValues = new HashMap<>();
        jsonValues.put("status", "9001");
        jsonValues.put("result", "ERROR");
        jsonValues.put("path", path);
        jsonValues.put("message", "CrawlerEntity lacks required values");
        jsonValues.put("name", crawlerData.getName());
        jsonValues.put("crawlerdata", crawlerData.toString());
        return new JSONObject(jsonValues).toString();
    }

    public static String urlNotValid(String path, String url, GenericCrawlerData crawlerData)
    {
        Map<String, String> jsonValues = new HashMap<>();
        jsonValues.put("status", "9002");
        jsonValues.put("result", "ERROR");
        jsonValues.put("path", path);
        jsonValues.put("url", url);
        jsonValues.put("message", "given URL is not valid");
        jsonValues.put("name", crawlerData.getName());
        jsonValues.put("crawlerdata", crawlerData.toString());
        return new JSONObject(jsonValues).toString();
    }
}
