package de.hochschuletrier.de.webcrawlerservice.crawler.utils;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * class contains all methods for validating a Page.class Object in a crawler
 *
 * @author Oliver Fries
 */
public class PageValidation
{
    private static final Logger logger = LoggerFactory.getLogger(PageValidation.class);

    /**
     * Validate if the given String is a valid URL
     *
     * @param url url string that will be validated against URI creation
     * @return boolean
     */
    public static boolean isUrlValidURI(String url)
    {
        /* Try creating a valid URL */
        try
        {
            new URL(url).toURI();
            return true;
        }

        // If there was an Exception
        // while creating URL object
        catch (Exception e)
        {
            logger.error(e.toString());
            logger.error("Url not Valid: " + url);
            return false;
        }
    }

    /**
     * Validate an url if its in the @allowedDomains List
     *
     * @param allowedDomains String List with allowed domain names
     * @param url            the url to check
     * @return boolean
     */
    private static boolean isUrlWithinAllowedDomains(List<String> allowedDomains, String url)
    {
        if (allowedDomains.isEmpty()) return true;
        for (String domain : allowedDomains)
        {
            // RegEx pattern that matches any link pattern, subdomain and Suffix of a link to the given domain
            Pattern p = Pattern.compile("^(?:https?:)?(?://)?(?:[^@\\n" +
                    "]+@)?(?:www\\.)?([a-z.])*(?:"
                    + domain
                    + "){1}(.[a-z]{2,64}/)?([-A-Za-z0-9+&@#/%=~_|$?!:,.])*");
            Matcher matcher = p.matcher(url);
            if (matcher.matches())
            {
                return true;
            }
        }
        logger.debug("URL not within Domain: " + url);
        return false;
    }

    /**
     * Validate if the url is already visited
     *
     * @param foundAndVisitedUrlsWithTimestamp HashMap with url strings as keys and a java.util.Date.class Object as key
     * @param url                              the URL to check
     * @return boolean
     */
    private static boolean isUrlVisited(HashMap<String, Date> foundAndVisitedUrlsWithTimestamp, String url)
    {
        if (foundAndVisitedUrlsWithTimestamp.containsKey(url))
        {
            logger.debug("URL visited: " + url);
            return true;
        } else
        {
            return false;
        }
    }


    /**
     * Check if the url is already declared as a dead link
     *
     * @param deadLinks String List of deadLinks
     * @param url       the url
     * @return boolean
     */
    private static boolean isUrlDeadLink(List<String> deadLinks, String url)
    {
        if (deadLinks.contains(url))
        {
            logger.debug("URL is deadlink: " + url);
            return true;
        } else
        {
            return false;
        }
    }

    /**
     * Validate an URL by checking if it fits in the allowedDomain scheme and if it hasn't been already visited or is
     * a dead link.
     *
     * @param allowedDomains                   String List of allowed Domains
     * @param foundAndVisitedUrlsWithTimestamp hashMap woth url and Date object
     * @param deadLinks                        string List of dead Links
     * @param deniedDomains                    String List of denied Domains
     * @param url                              the URL to check
     * @return boolean: True if url is Allowed & False if not
     */
    public static boolean urlAllowed(List<String> allowedDomains, HashMap<String, Date> foundAndVisitedUrlsWithTimestamp,
                                     List<String> deadLinks, List<String> deniedDomains, String url)
    {
        if (isUrlValidURI(url) && isUrlWithinAllowedDomains(allowedDomains, url) &&
                !isUrlVisited(foundAndVisitedUrlsWithTimestamp, url) &&
                !isUrlDeadLink(deadLinks, url)
                && !deniedDomains.contains(url) && urlContainsHTML(url))
        {
            return true;
        } else
        {
            //logger.info("URL not Allowed: " + url);
            return false;
        }
    }

    private static boolean urlContainsHTML(String url)
    {
        try
        {
            Document doc = Jsoup.connect(url).get();
            if (doc != null)
            {
                return true;
            } else
            {
                return false;
            }
        } catch (IOException e)
        {
            logger.warn("Non valid URL; HTML Document empty - " + url);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Generate a hash from the page body and put it ti the foundAndVisitedUrlsWithMD5Hash HashMap
     * key is the page url
     *
     * @param page a Page Object
     * @return byte Array SHA256 Hash
     */
    public static byte[] generateBodyHash(@NotNull Page page)
    {
        return generateSHA256HashFromString(page.getHtmlBody().text());
    }

    /**
     * Generate a SHA256 Hash from a String
     *
     * @param input String - Should be the html Body including all tags
     * @return byte Array SHA256 Hash
     */
    public static byte[] generateSHA256HashFromString(String input)
    {
        MessageDigest digest = null;
        try
        {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        if (digest != null)
        {
            return digest.digest(input.getBytes(StandardCharsets.UTF_8));
        } else
        {
            logger.error("Digest null for input");
            logger.error(input);
            return digest.digest();
        }
    }

}
