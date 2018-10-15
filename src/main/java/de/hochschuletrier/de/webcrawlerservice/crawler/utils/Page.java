package de.hochschuletrier.de.webcrawlerservice.crawler.utils;

import de.hochschuletrier.de.webcrawlerservice.utils.HTMLExtractionUtils;
import org.jsoup.nodes.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The page represents the information of a single html page.
 * <p>
 * It consists of the htmlBody of a page, all headers, the lastModified Timestamp of the page, the http status code and
 * the extracted text of the page
 *
 * @author: Oliver Fries
 */
public class Page
{
    private Date lastModified;
    private Integer statusCode;
    private Document htmlBody;
    private Map<String, String> headerMap;
    private String text;
    private String url;
    private Date crawlingDateTime;

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

    public Integer getStatusCode()
    {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode)
    {
        this.statusCode = statusCode;
    }

    public Document getHtmlBody()
    {
        return htmlBody;
    }

    public void setHtmlBody(Document htmlBody)
    {
        this.htmlBody = htmlBody;
    }

    public Map<String, String> getHeaderMap()
    {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap)
    {
        this.headerMap = headerMap;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public Page()
    {
    }

    public Page(String url)
    {
        this.url = url;
    }

    public Page(Date lastModified, Integer statusCode, Document htmlBody, Map<String, String> headerMap, String text)
    {
        this.lastModified = lastModified;
        this.statusCode = statusCode;
        this.htmlBody = htmlBody;
        this.headerMap = headerMap;
        this.text = text;
    }

    @Override
    public String toString()
    {
        return "Page: " + this.url + "; LastModified: " + this.lastModified + "; StatusCode: " + this.statusCode;
    }

    public List<String> getFileUrls(List<String> allowedFileExtensions)
    {
        return HTMLExtractionUtils.getFileLinksFromDocument(this.getHtmlBody(), allowedFileExtensions);
    }

    public void setCrawlingDateTime(Date currentDate)
    {
        this.crawlingDateTime = currentDate;
    }

    public Date getCrawlingDateTime()
    {
        return crawlingDateTime;
    }
}
