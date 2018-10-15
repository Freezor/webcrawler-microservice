package de.hochschuletrier.de.webcrawlerservice.persistence.repository;

import de.hochschuletrier.de.webcrawlerservice.persistence.entity.DeadLinkEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Interface for DeadLink Queries
 *
 * @author: Oliver Fries
 */
public interface DeadLinkRepository extends MongoRepository<DeadLinkEntity, String>
{
    public DeadLinkEntity findByUrl(String url);
}
