package de.hochschuletrier.de.webcrawlerservice.persistence.repository;

import de.hochschuletrier.de.webcrawlerservice.persistence.entity.FileLinkEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Interface for FileLink Queries
 *
 * @author: Oliver Fries
 */
public interface FileLinkRepository extends MongoRepository<FileLinkEntity, String>
{
    public FileLinkEntity findByFileURL(String fileUrl);

    public List<FileLinkEntity> findByExtension(String extension);
}
