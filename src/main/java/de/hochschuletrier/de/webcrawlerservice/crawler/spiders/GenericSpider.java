package de.hochschuletrier.de.webcrawlerservice.crawler.spiders;

import de.hochschuletrier.de.webcrawlerservice.crawler.Crawler;
import de.hochschuletrier.de.webcrawlerservice.crawler.utils.Page;
import de.hochschuletrier.de.webcrawlerservice.persistence.entity.FileLinkEntity;
import de.hochschuletrier.de.webcrawlerservice.persistence.entity.WebPageEntity;
import de.hochschuletrier.de.webcrawlerservice.persistence.repository.CrawlerEntityRepository;
import de.hochschuletrier.de.webcrawlerservice.persistence.repository.DeadLinkRepository;
import de.hochschuletrier.de.webcrawlerservice.persistence.repository.FileLinkRepository;
import de.hochschuletrier.de.webcrawlerservice.persistence.repository.WebPageEntityRepository;
import de.hochschuletrier.de.webcrawlerservice.utils.HTMLExtractionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic Spider Class which only downloads each html file and saves it to the database
 * Additionally it saves all file Links to the database but does not download them
 *
 * @author: Oliver Fries
 */
public class GenericSpider extends Crawler
{
    private FileLinkRepository fileLinkRepository;

    private WebPageEntityRepository webPageEntityRepository;

    private List<String> allowedFileExtensions;

    private static final Logger logger = LoggerFactory.getLogger(GenericSpider.class);

    public GenericSpider(String crawlerName, List<String> allowedDomains, List<String> seedUrls, Integer crawlingDelay, Integer timeout, boolean revisiting, List<String> deniedDomains, boolean followRedirects, FileLinkRepository fileLinkRepository, WebPageEntityRepository webPageEntityRepository)
    {
        super(crawlerName, allowedDomains, seedUrls, crawlingDelay, timeout, revisiting, deniedDomains, followRedirects);
        initParameters(fileLinkRepository, webPageEntityRepository);
    }

    public GenericSpider(String name, List<String> allowedDomains, List<String> seedURLs, boolean revisiting, List<String> deniedDomains, CrawlerEntityRepository crawlerEntityRepository, DeadLinkRepository deadLinkRepository, FileLinkRepository fileLinkRepository, WebPageEntityRepository webPageEntityRepository)
    {
        super(name, allowedDomains, seedURLs, revisiting, deniedDomains, crawlerEntityRepository, deadLinkRepository);
        initParameters(fileLinkRepository, webPageEntityRepository);
    }

    private void initParameters(FileLinkRepository fileLinkRepository, WebPageEntityRepository webPageEntityRepository)
    {
        this.fileLinkRepository = fileLinkRepository;
        this.webPageEntityRepository = webPageEntityRepository;
        this.allowedFileExtensions = new ArrayList<>();

        this.allowedFileExtensions.add("pdf");
        this.allowedFileExtensions.add("xls");
        this.allowedFileExtensions.add("xlsx");
        this.allowedFileExtensions.add("doc");
        this.allowedFileExtensions.add("docx");
        this.allowedFileExtensions.add("zip");
        this.allowedFileExtensions.add("rar");
        this.allowedFileExtensions.add("txt");
        this.allowedFileExtensions.add("bin");
        this.allowedFileExtensions.add("csv");
        this.allowedFileExtensions.add("dat");
    }

    @Override
    protected void pagePipeline(Page page)
    {
        if (urlContainsValidHTML(page))
        {
            logger.info("Write page into database " + page.getUrl());
            this.webPageEntityRepository.save(new WebPageEntity(page));
            for (String fileLink : page.getFileUrls(this.allowedFileExtensions))
            {
                this.fileLinkRepository.save(new FileLinkEntity(fileLink, HTMLExtractionUtils.getFileExtensionFromFileLink(fileLink)));
            }
        } else
        {
            logger.warn("Not a valid page for Processing: " + page.getUrl());
        }

    }

    private boolean urlContainsValidHTML(Page page)
    {
        if (page.getUrl().contains("mailto") || page.getUrl().contains("@"))
        {
            return false;
        }
        if (!page.getHtmlBody().hasText())
        {
            return false;
        }
        return true;
    }
}
