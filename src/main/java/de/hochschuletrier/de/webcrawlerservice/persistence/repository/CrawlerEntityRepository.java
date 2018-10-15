package de.hochschuletrier.de.webcrawlerservice.persistence.repository;

import de.hochschuletrier.de.webcrawlerservice.persistence.entity.CrawlerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Interface for Crawler Entity Queries
 *
 * @author: Oliver Fries
 */
public interface CrawlerEntityRepository extends MongoRepository<CrawlerEntity, String>
{
    public CrawlerEntity findByName(String name);

    public CrawlerEntity findByUniqueName(String uniqueName);

}