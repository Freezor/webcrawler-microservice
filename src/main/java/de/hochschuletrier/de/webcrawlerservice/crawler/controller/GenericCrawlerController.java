package de.hochschuletrier.de.webcrawlerservice.crawler.controller;

import de.hochschuletrier.de.webcrawlerservice.crawler.Crawler;
import de.hochschuletrier.de.webcrawlerservice.crawler.spiders.GenericSpider;
import de.hochschuletrier.de.webcrawlerservice.crawler.utils.UserAgent;
import de.hochschuletrier.de.webcrawlerservice.persistence.repository.CrawlerEntityRepository;
import de.hochschuletrier.de.webcrawlerservice.persistence.repository.DeadLinkRepository;
import de.hochschuletrier.de.webcrawlerservice.persistence.repository.FileLinkRepository;
import de.hochschuletrier.de.webcrawlerservice.persistence.repository.WebPageEntityRepository;
import de.hochschuletrier.de.webcrawlerservice.utils.GenericCrawlerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Thread Controller for the Generic Spider
 *
 * @author: Oliver Fries
 */
@Component
@Scope("prototype")
public class GenericCrawlerController implements Runnable
{
    private Crawler crawler;

    private static final Logger logger = LoggerFactory.getLogger(GenericCrawlerController.class);

    public void init(GenericCrawlerData genericCrawlerData, CrawlerEntityRepository crawlerEntityRepository, DeadLinkRepository deadLinkRepository, WebPageEntityRepository webPageEntityRepository, FileLinkRepository fileLinkRepository)
    {
        logger.info("Initialise CrawlerEntity");
        this.crawler = new GenericSpider(genericCrawlerData.getName(), genericCrawlerData.getAllowedDomains(),
                genericCrawlerData.getSeedURLs(), genericCrawlerData.isRevisiting(),
                genericCrawlerData.getDeniedDomains(),crawlerEntityRepository,deadLinkRepository,fileLinkRepository, webPageEntityRepository);
        if (genericCrawlerData.getMaxPages() != null && genericCrawlerData.getMaxPages() != 0)
        {
            this.crawler.setMaxPages(genericCrawlerData.getMaxPages());
        }
        if (genericCrawlerData.getTimeout() != null && genericCrawlerData.getTimeout() != 0)
        {
            this.crawler.setTimeout(genericCrawlerData.getTimeout());
        }
        if (!genericCrawlerData.isFollowRedirects())
        {
            this.crawler.setFollowRedirects(false);
        }
        if (genericCrawlerData.getCrawlingDelay() != null)
        {
            this.crawler.setCrawlingDelay(genericCrawlerData.getCrawlingDelay());
        }
    }

    @Override
    public void run()
    {
        logger.info("Called from thread");
        this.crawler.run(UserAgent.Default);
    }

}
