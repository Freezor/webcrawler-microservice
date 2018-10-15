package de.hochschuletrier.de.webcrawlerservice.persistence.entity;

import com.sleepycat.persist.model.Entity;
import org.springframework.data.annotation.Id;

/**
 * Entity to store all fileLinks and the extension
 *
 * @author: Oliver Fries
 */
@Entity
public class FileLinkEntity
{
    @Id
    private String id;
    private String fileURL;
    private String extension;

    public FileLinkEntity(String fileURL, String extension)
    {
        this.fileURL = fileURL;
        this.extension = extension;
    }

    public FileLinkEntity()
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

    public String getFileURL()
    {
        return fileURL;
    }

    public void setFileURL(String fileURL)
    {
        this.fileURL = fileURL;
    }

    public String getExtension()
    {
        return extension;
    }

    public void setExtension(String extension)
    {
        this.extension = extension;
    }
}
