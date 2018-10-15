package de.hochschuletrier.de.webcrawlerservice.persistence.entity;

import com.sleepycat.persist.model.Entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

/**
 * Database Entity which represents a crawler.
 * Does not contain all options for the running crawler but the main information
 *
 * @author: Oliver Fries
 */
@Entity
public class CrawlerEntity
{
    @Id
    private String id;
    private String name;
    @Indexed(unique = true)
    private String uniqueName;
    private String status;
    private int statusId;
    private Date startedAt;
    private Date finishedAt;
    private Integer crawledPages;
    private Integer cycleNumber;

    public CrawlerEntity()
    {

    }

    public CrawlerEntity(String name, String uniqueName, String status, Date startedAt, Date finishedAt, Integer crawledPages)
    {
        this.name = name;
        this.uniqueName = uniqueName;
        this.status = status;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.crawledPages = crawledPages;
        this.cycleNumber = 0;
    }


    public CrawlerEntity(String name, String uniqueName, String status, Date startedAt)
    {
        this.name = name;
        this.uniqueName = uniqueName;
        this.status = status;
        this.startedAt = startedAt;
        this.cycleNumber = 0;
    }

    public CrawlerEntity(String name, String uniqueName, String status)
    {
        this.name = name;
        this.uniqueName = uniqueName;
        this.status = status;
        this.cycleNumber = 0;
    }


    public CrawlerEntity(String name, String status, int statusId)
    {
        this.name = name;
        this.status = status;
        this.statusId = statusId;
        this.cycleNumber = 0;
    }

    public CrawlerEntity(String name, String uniqueName, String status, int statusId, Date startedAt, Date finishedAt, Integer crawledPages, Integer cycleNumber)
    {
        this.name = name;
        this.uniqueName = uniqueName;
        this.status = status;
        this.statusId = statusId;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.crawledPages = crawledPages;
        this.cycleNumber = cycleNumber;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUniqueName()
    {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName)
    {
        this.uniqueName = uniqueName;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getStartedAt()
    {
        return startedAt;
    }

    public void setStartedAt(Date startedAt)
    {
        this.startedAt = startedAt;
    }

    public Date getFinishedAt()
    {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt)
    {
        this.finishedAt = finishedAt;
    }

    public Integer getCrawledPages()
    {
        return crawledPages;
    }

    public void setCrawledPages(Integer crawledPages)
    {
        this.crawledPages = crawledPages;
    }

    public Integer getCycleNumber()
    {
        return cycleNumber;
    }

    public void setCycleNumber(Integer cycleNumber)
    {
        this.cycleNumber = cycleNumber;
    }

    public int getStatusId()
    {
        return statusId;
    }

    public void setStatusId(int statusId)
    {
        this.statusId = statusId;
    }
}
