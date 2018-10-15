package de.hochschuletrier.de.webcrawlerservice.persistence.entity;


import com.sleepycat.persist.model.Entity;
import de.hochschuletrier.de.webcrawlerservice.crawler.utils.Page;
import de.hochschuletrier.de.webcrawlerservice.utils.HTMLExtractionUtils;
import org.springframework.data.annotation.Id;

import java.net.URISyntaxException;
import java.util.Date;

/**
 * Entity which stores all information about a single html page in the DB
 *
 * @author: Oliver Fries
 */
@Entity
public class WebPageEntity
{
    @Id
    private String id;
    private String url;
    private Date lastModified;
    private Date crawlingDateTime;
    private String title;
    private String htmlDoc;
    private Integer statusCode;
    private String domain;

    public WebPageEntity(String url, Date lastModified, Date crawlingDateTime, String title, String htmlDoc, Integer statusCode, String domain)
    {
        this.url = url;
        this.lastModified = lastModified;
        this.crawlingDateTime = crawlingDateTime;
        this.title = title;
        this.htmlDoc = htmlDoc;
        this.statusCode = statusCode;
        this.domain = domain;
    }

    public WebPageEntity()
    {
    }

    public WebPageEntity(Page page)
    {
        this.url = page.getUrl();
        this.lastModified = page.getLastModified();
        this.crawlingDateTime = page.getCrawlingDateTime();
        this.title = page.getHtmlBody().title();
        this.htmlDoc = page.getHtmlBody().toString();
        this.statusCode = page.getStatusCode();
        try
        {
            this.domain = HTMLExtractionUtils.getDomainName(page.getUrl());
        } catch (URISyntaxException e)
        {
            this.domain = "";
            e.printStackTrace();
        }
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public Date getLastModified()
    {
        return lastModified;
    }

    public void setLastModified(Date lastModified)
    {
        this.lastModified = lastModified;
    }

    public Date getCrawlingDateTime()
    {
        return crawlingDateTime;
    }

    public void setCrawlingDateTime(Date crawlingDateTime)
    {
        this.crawlingDateTime = crawlingDateTime;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getHtmlDoc()
    {
        return htmlDoc;
    }

    public void setHtmlDoc(String htmlDoc)
    {
        this.htmlDoc = htmlDoc;
    }

    public Integer getStatusCode()
    {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode)
    {
        this.statusCode = statusCode;
    }

    public String getDomain()
    {
        return domain;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }


}
