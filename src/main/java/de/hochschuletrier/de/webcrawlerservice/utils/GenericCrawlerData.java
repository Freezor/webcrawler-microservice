package de.hochschuletrier.de.webcrawlerservice.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the JSON Object, which the client may send to start a crawler
 *
 * @author: Oliver Fries
 */
public class GenericCrawlerData
{
    private String name;
    private List<String> deniedDomains;
    private List<String> allowedDomains;
    private List<String> seedURLs;
    private boolean revisiting;
    private Integer crawlingDelay;
    private Integer timeout;
    private boolean followRedirects;
    private Integer maxPages;

    public GenericCrawlerData()
    {
        this.name = "";
        this.allowedDomains = new ArrayList<>();
        this.deniedDomains = new ArrayList<>();
        this.seedURLs = new ArrayList<>();
        this.revisiting = false;
        this.followRedirects = true;
    }

    public List<String> getDeniedDomains()
    {
        return deniedDomains;
    }

    public void setDeniedDomains(List<String> deniedDomains)
    {
        this.deniedDomains = deniedDomains;
    }

    public List<String> getAllowedDomains()
    {
        return allowedDomains;
    }

    public void setAllowedDomains(List<String> allowedDomains)
    {
        this.allowedDomains = allowedDomains;
    }

    public List<String> getSeedURLs()
    {
        return seedURLs;
    }

    public void setSeedURLs(List<String> seedURLs)
    {
        this.seedURLs = seedURLs;
    }

    public boolean isRevisiting()
    {
        return revisiting;
    }

    public void setRevisiting(boolean revisiting)
    {
        this.revisiting = revisiting;
    }

    public Integer getCrawlingDelay()
    {
        return crawlingDelay;
    }

    public void setCrawlingDelay(Integer crawlingDelay)
    {
        this.crawlingDelay = crawlingDelay;
    }

    public Integer getTimeout()
    {
        return timeout;
    }

    public void setTimeout(Integer timeout)
    {
        this.timeout = timeout;
    }

    public boolean isFollowRedirects()
    {
        return followRedirects;
    }

    public void setFollowRedirects(boolean followRedirects)
    {
        this.followRedirects = followRedirects;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getMaxPages()
    {
        return maxPages;
    }

    public void setMaxPages(Integer maxPages)
    {
        this.maxPages = maxPages;
    }

    @Override
    public String toString()
    {
        return this.name + ", " + this.getSeedURLs().toString() + ", " + this.getAllowedDomains().toString();
    }
}
