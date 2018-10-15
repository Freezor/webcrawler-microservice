package de.hochschuletrier.de.webcrawlerservice.persistence.entity;

import com.sleepycat.persist.model.Entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

/**
 * Entity to store Dead Links (Url & crawlingDateTime) in the database
 * @author: Oliver Fries
 */
@Entity
public class DeadLinkEntity
{

    @Id
    private String id;
    @Indexed(unique = true)
    private String url;
    private Date lastCrawledAt;

    public DeadLinkEntity(String url, Date lastCrawledAt)
    {
        this.url = url;
        this.lastCrawledAt = lastCrawledAt;
    }

    public DeadLinkEntity(String url)
    {
        this.url = url;
    }

    public DeadLinkEntity()
    {
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

    public Date getLastCrawledAt()
    {
        return lastCrawledAt;
    }

    public void setLastCrawledAt(Date lastCrawledAt)
    {
        this.lastCrawledAt = lastCrawledAt;
    }
}
