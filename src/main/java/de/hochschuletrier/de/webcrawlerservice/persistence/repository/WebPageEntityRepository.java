package de.hochschuletrier.de.webcrawlerservice.persistence.repository;

import de.hochschuletrier.de.webcrawlerservice.persistence.entity.WebPageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Interface for WebPage Queries
 *
 * @author: Oliver Fries
 */
public interface WebPageEntityRepository extends MongoRepository<WebPageEntity, String>
{
    public WebPageEntity findByUrl(String url);
}
