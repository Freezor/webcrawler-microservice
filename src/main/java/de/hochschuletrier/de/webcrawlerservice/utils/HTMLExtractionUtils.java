package de.hochschuletrier.de.webcrawlerservice.utils;

import de.hochschuletrier.de.webcrawlerservice.crawler.utils.Page;
import de.hochschuletrier.de.webcrawlerservice.crawler.utils.PageValidation;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains Utils to extract specific information from URLS or HTML Documents
 *
 * @author: Oliver Fries
 */
public class HTMLExtractionUtils
{

    private static final Logger logger = LoggerFactory.getLogger(HTMLExtractionUtils.class);


    /**
     * Returns the file extension type of a url in upper case
     *
     * @param fileLink string of a file url
     * @return String
     */
    public static String getFileExtensionFromFileLink(String fileLink)
    {
        return fileLink.substring(fileLink.lastIndexOf(".")).toUpperCase();
    }


    /**
     * Validate the url if it is a relative URL of the domain
     *
     * @param link String of a link
     * @return True if link is relative path
     */
    public static boolean isRelativeUrl(String link)
    {
        Pattern p = Pattern.compile("(?!www\\.|(?:http|ftp)s?://|[A-Za-z]:\\\\|//).*");
        Matcher matcher = p.matcher(link);
        return matcher.matches();
    }


    /**
     * Create an absolute path URL String from relative link
     *
     * @param link String relative link
     * @param page Page object to get host from
     * @return absolute link String
     */
    public static String createUrlFromRelativeLink(String link, Page page)
    {
        String host = page.getHeaderMap().get("Host");
        return "http://www." + host + link;
    }


    /**
     * Get all links from a html Document and return an array of links as string
     *
     * @param page Page object to extract the links
     * @return String List with all found links
     */
    public static ArrayList<String> getLinksFromDocument(Page page)
    {
        Elements elements = new Elements();
        ArrayList<String> linkArrayList = new ArrayList<>();
        //Check if htmlBody is null. Should no longer happen
        if (page.getHtmlBody() != null)
        {
            try
            {
                elements = page.getHtmlBody().select("a");
            } catch (Exception e)
            {
                //catch if any errors occured while extracting links
                e.printStackTrace();
                logger.error("Could not find links for page " + page.getUrl());
                logger.error(e.toString());
            }
            for (Element element : elements)
            {
                String link = element.attr("abs:href");
                if (!link.isEmpty())
                {
                    if (PageValidation.isUrlValidURI(link))
                    {
                        linkArrayList.add(link);
                    } else if (HTMLExtractionUtils.isRelativeUrl(link))
                    {
                        linkArrayList.add(HTMLExtractionUtils.createUrlFromRelativeLink(link, page));
                    }
                }
            }
        } else
        {
            logger.warn(page.getUrl() + " has empty document body.");
        }
        return linkArrayList;
    }


    /**
     * Extract a Date Object from given String timestamp, pattern and localisation
     *
     * @param timestamp TimeStamp String which will be parsed into Date Object
     * @param pattern   must be valid pattern like "EEE, d MMM yyyy HH:mm:ss Z"
     * @param locale    localisation like Locale.ENGLISH
     * @return Date Object
     */
    public static Date parseDateFromTimeStamp(String timestamp, String pattern, Locale locale, Page page)
    {
        DateFormat format;
        Date currentTimeStamp = null;
        format = new SimpleDateFormat(pattern, locale);
        try
        {
            if (timestamp == null || timestamp.isEmpty())
            {
                //TODO: generate similarity
            } else
            {
                currentTimeStamp = format.parse(timestamp);
            }
        } catch (ParseException e)
        {
            e.printStackTrace();
            logger.error(e.toString());
        }
        return currentTimeStamp;
    }


    /**
     * Create the current Date with timezone Berlin
     *
     * @return Date object
     */
    @NotNull
    public static Date getCurrentDate()
    {
        TimeZone tz = TimeZone.getTimeZone("GMT-03:00");
        Calendar cal = Calendar.getInstance(tz);
        return cal.getTime();
    }

    /**
     * Extract all File Links from a html Document
     *
     * @param htmlBody a HTML Document Object
     * @return List of file Links
     */
    public static List<String> getFileLinksFromDocument(Document htmlBody, List<String> allowedFileExtensions)
    {
        List<String> fileLinks = new ArrayList<>();
        Elements elements = htmlBody.select("a");
        for (Element element : elements)
        {
            String link = element.attr("abs:href");
            if (!link.isEmpty())
            {
                if (PageValidation.isUrlValidURI(link) && allowedFileExtensions.contains(link))
                {
                    fileLinks.add(link);
                }
            }
        }
        return fileLinks;
    }


    /**
     * get domain name
     *
     * @param url
     * @return
     * @throws URISyntaxException
     */
    public static String getDomainName(String url) throws URISyntaxException
    {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

}
