package de.hochschuletrier.de.webcrawlerservice.crawler;


import de.hochschuletrier.de.webcrawlerservice.crawler.utils.Page;
import de.hochschuletrier.de.webcrawlerservice.crawler.utils.PageValidation;
import de.hochschuletrier.de.webcrawlerservice.persistence.entity.CrawlerEntity;
import de.hochschuletrier.de.webcrawlerservice.persistence.entity.DeadLinkEntity;
import de.hochschuletrier.de.webcrawlerservice.persistence.repository.CrawlerEntityRepository;
import de.hochschuletrier.de.webcrawlerservice.persistence.repository.DeadLinkRepository;
import de.hochschuletrier.de.webcrawlerservice.utils.HTMLExtractionUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Basic CrawlerEntity class
 * Contains all options for crawling to the web in specified borders.
 *
 * Don't set the crawlingDelay to low, or your crawler might get blocked
 *
 * @author: Oliver Fries
 */
@Service
public abstract class Crawler
{
    // Crawler parameters
    //TODO: MaxDepth, page similarity
    private String crawlerName;
    private List<String> allowedDomains; // Domains that are initially allowed to crawl
    private List<String> deniedDomains; // Domains that are initially denied
    private List<String> seedUrls; // URLs where the crawler start it job
    private HashMap<String, Date> foundAndVisitedUrlsWithTimestamp; // visited URLs with timestamp
    private List<String> foundAndVisitedUrls; // list of all visited urls
    private List<String> foundLinks; // all found links, that are still open to crawl
    private List<String> deadLinks; // found links that are not valid
    private Integer crawlingDelay; // delay between single requests
    private Integer timeout; // maximum timeout for crawler requests to a single page
    private Integer maxPages; // max pages to crawl
    private boolean revisiting; // Set true if pages should be revisited, and checked for changes
    private String userAgent; // the user agent, which the crawler starts it requests with
    private boolean followRedirects; // set true for following http redirects

    // Logger parameter
    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);

    // Repositories
    private DeadLinkRepository deadLinkRepository;
    private CrawlerEntity crawlerEntity;

    @Autowired(required = true)
    private CrawlerEntityRepository crawlerEntityRepository;

    /**
     * @param crawlerName        Name of the CrawlerEntity
     * @param allowedDomains     contains all allowed domains for this crawler. Ex. 'google', 'spring'.
     *                           Do NOT user literals like 'www.news.google.de' use 'news.google' instead
     * @param seedUrls           URLS to start with
     * @param revisiting         revisit all URLs and crawl again, if changes appear
     * @param deniedDomains      List of all denied Domains
     * @param deadLinkRepository
     */
    public Crawler(String crawlerName, List<String> allowedDomains, List<String> seedUrls,
                   boolean revisiting, List<String> deniedDomains, CrawlerEntityRepository crawlerEntityRepository, DeadLinkRepository deadLinkRepository)
    {
        this.crawlerName = crawlerName;
        this.allowedDomains = allowedDomains;
        this.deniedDomains = deniedDomains;
        this.seedUrls = seedUrls;
        this.revisiting = revisiting;

        this.crawlingDelay = 3000;
        this.timeout = 30000;
        this.followRedirects = true;
        this.crawlerEntityRepository = crawlerEntityRepository;
        this.deadLinkRepository = deadLinkRepository;
        initObjects();
        saveCrawler();
    }

    /**
     * @param crawlerName    Name of the CrawlerEntity
     * @param allowedDomains contains all allowed domains for this crawler. Ex. 'google', 'spring'.
     *                       Do NOT user literals like 'www.news.google.de' use 'news.google' instead
     * @param seedUrls       URLS to start with
     * @param revisiting     revisit all URLs and crawl again, if changes appear
     * @param deniedDomains  List of all denied Domains
     * @param crawlingDelay  Set custom delay between a URl Request Minimum should be 2000 Milliseconds
     * @param timeout        Set the timeout after a request for a single page should be aborted
     */
    public Crawler(String crawlerName, List<String> allowedDomains, List<String> seedUrls, Integer crawlingDelay,
                   Integer timeout, boolean revisiting, List<String> deniedDomains, boolean followRedirects)
    {
        this.crawlerName = crawlerName;
        this.allowedDomains = allowedDomains;
        this.deniedDomains = deniedDomains;
        this.seedUrls = seedUrls;
        this.crawlingDelay = crawlingDelay;
        this.timeout = timeout;
        this.revisiting = revisiting;
        this.followRedirects = followRedirects;
        initObjects();
        saveCrawler();
    }

    /**
     * Initialise Objects which are not part of constructor Parameters
     */
    private void initObjects()
    {
        this.foundAndVisitedUrlsWithTimestamp = new HashMap<>();
        this.foundLinks = new ArrayList<>();
        this.deadLinks = new ArrayList<>();
        this.maxPages = 0;
        this.foundAndVisitedUrls = new ArrayList<>();
    }

    /**
     * Save the new created crawler to the database
     */
    private void saveCrawler()
    {
        this.crawlerEntity = new CrawlerEntity(this.crawlerName, "created", 1);

        validateUniqueName();

        crawlerEntityRepository.save(this.crawlerEntity);
    }

    /**
     * validate the unique name of the crawler and create new one of already exists
     */
    private void validateUniqueName()
    {
        String uniqueName = this.crawlerName + "-" + this.seedUrls.get(0);
        if (crawlerEntityRepository.findByUniqueName(uniqueName) != null)
        {
            String tmpName = uniqueName;
            do
            {
                this.crawlerEntity.setCycleNumber(this.crawlerEntity.getCycleNumber() + 1);
                tmpName = uniqueName + "-" + this.crawlerEntity.getCycleNumber();
            } while (crawlerEntityRepository.findByUniqueName(tmpName) != null);
            uniqueName = tmpName;
        }
        this.crawlerEntity.setUniqueName(uniqueName);

    }


    /**
     * Run the crawler
     *
     * @param userAgent will be used for Crawling requests use the UserAgent.class static Strings
     */
    public void run(String userAgent)
    {
        int pageCounter = 0;
        logger.info("Start CrawlerEntity: " + this.crawlerName);
        this.userAgent = userAgent;
        this.foundLinks = this.seedUrls;
        saveCrawlerStarted();
        while (!foundLinks.isEmpty())
        {
            try
            {
                String pageToCrawl = foundLinks.get(0);
                foundLinks.remove(0);
                logger.info("Crawling: " + this.crawlerName + " - " + pageToCrawl);
                if (crawlPage(pageToCrawl))
                {
                    pageCounter++;
                }
                // If maxPages is 0 then no maximum is set
                if (this.maxPages != 0)
                {
                    // Stop loop when maxPages is reached
                    if (pageCounter >= maxPages)
                    {
                        break;
                    } else
                    {
                        logger.info(this.crawlerName + " reached " + pageCounter + " of " + this.maxPages + " to crawl.");
                    }
                }
                logger.info(this.crawlerName + " found " + pageCounter + " Pages.");
                logger.info("URLs to crawl: " + this.foundLinks.size());
                Thread.sleep(this.crawlingDelay);
            } catch (InterruptedException e)
            {
                logger.warn(this.crawlerName + ": Crawling Interrupted - " + e.toString());
                e.printStackTrace();
            }
        }
        saveCrawlerFinished(pageCounter);
        logOutputCrawlerResult(pageCounter);
    }

    private void logOutputCrawlerResult(int pageCounter)
    {
        logger.info("Stopping crawler " + this.crawlerName + "\nCrawled " + pageCounter + " urls\nFound " + this.foundLinks.size() + " links");
        logger.info("#################################################");
        logger.info("Crawled pages: " + this.foundAndVisitedUrls.size());
        for (String s : this.foundAndVisitedUrls)
        {
            logger.info(s);
        }
        logger.info("#################################################");
        logger.info("Found Links");
        for (String s : this.foundLinks)
        {
            logger.info(s);
        }
        logger.info("#################################################");
        logger.info("Found and Visited Links with Timestamp");
        for (String s : foundAndVisitedUrlsWithTimestamp.keySet())
        {
            logger.info(s);
        }
        logger.info("#################################################");
        logger.info("Dead Links: " + this.deadLinks.size());
    }

    /**
     * Save the start time of the crawler into the database
     */
    private void saveCrawlerStarted()
    {
        Date dateTime = HTMLExtractionUtils.getCurrentDate();
        this.crawlerEntity.setStartedAt(dateTime);
        this.crawlerEntity.setStatus("running");
        this.crawlerEntity.setStatusId(2);
        crawlerEntityRepository.save(this.crawlerEntity);
    }


    private void saveCrawlerFinished(int pageCounter)
    {
        Date dateTime = HTMLExtractionUtils.getCurrentDate();
        this.crawlerEntity.setFinishedAt(dateTime);
        this.crawlerEntity.setStatus("finished");
        this.crawlerEntity.setStatusId(4);
        this.crawlerEntity.setCrawledPages(pageCounter);
        crawlerEntityRepository.save(this.crawlerEntity);
    }

    /**
     * Crawl page and extract all basic information
     *
     * @param url which will be crawled
     */
    private boolean crawlPage(String url)
    {
        if (PageValidation.urlAllowed(this.allowedDomains, this.foundAndVisitedUrlsWithTimestamp, this.deadLinks,
                this.deniedDomains, url))
        {
            Page page = getPage(url);
            if (foundAndVisitedUrlsWithTimestamp.containsKey(url) && revisiting)
            {
                // Extract the links if page is modified check by compare timestamps and Hash
                // TODO: valiadate page similarity
                // Delete changed Content detection, because it detects also little changes like changed ads on a new
                // page load
                // if (pageHasModifiedDate(page.getLastModified(), foundAndVisitedUrlsWithTimestamp.get(url)) && pageHasChangedContent(page))
                if (pageHasModifiedDate(page.getLastModified(), foundAndVisitedUrlsWithTimestamp.get(url)))
                {
                    extractLinks(page);
                    pagePipeline(page);
                }
            } else
            {
                logger.info(page.toString());
                if (page.getLastModified() != null)
                {
                    this.foundAndVisitedUrlsWithTimestamp.put(url, page.getLastModified());
                }
                extractLinks(page);
                pagePipeline(page);
                // TODO: only check files which are younger than 1/2 year & don't add after 4 times checking
                // if revisiting is true, add page to found Links again, to crawl the page again
                if (revisiting && !this.foundLinks.contains(url))
                {
                    this.foundLinks.add(url);
                }
            }
            return true;
        } else
        {
            logger.warn("Adding URL: " + url + " to list of denied links");
            validateAndSaveDeadLink(url);
            foundLinks.remove(url);
            return false;
        }
    }

    /**
     * Returns the html document from the url
     *
     * @param url this url will be downloaded
     * @return Page Object
     */
    private Page getPage(String url)
    {
        Page page = new Page(url);
        try
        {
            Connection.Response response = Jsoup.connect(url).ignoreContentType(true)
                    .userAgent(this.userAgent)
                    .referrer("http://www.google.com")
                    .timeout(this.timeout)
                    .followRedirects(true)
                    .followRedirects(this.followRedirects)
                    .execute();
            page.setHeaderMap(response.headers());
            page.setHtmlBody(response.parse());
            page.setLastModified(parseDateTime(response.header("last-modified"), page));
            page.setStatusCode(response.statusCode());
            page.setCrawlingDateTime(HTMLExtractionUtils.getCurrentDate());
            this.foundAndVisitedUrls.add(url);
            return page;
        } catch (IOException e)
        {
            this.foundAndVisitedUrls.add(url);
            validateAndSaveDeadLink(url);
            return page;
        }
    }

    /**
     * Validate if url really is a dead link and save to DB if true
     *
     * @param url
     */
    private void validateAndSaveDeadLink(String url)
    {
        if (!PageValidation.isUrlValidURI(url) || !PageValidation.isUrlValidURI(url))
        {
            logger.warn("Adding URL: " + url + " to list of dead links");
            deadLinks.add(url);
            this.deadLinkRepository.save(new DeadLinkEntity(url, HTMLExtractionUtils.getCurrentDate()));
        }
    }

    /**
     * Parse the crawling timestamp to java.util.Date
     *
     * @param timeStampString TimeStamp String which will be parsed into Date Object. Must have the following pattern:
     *                        EEE, d MMM yyyy HH:mm:ss Z for example: "Wed, 21 Oct 2015 07:28:00 GMT"
     * @return Date Object
     */
    private Date parseDateTime(String timeStampString, Page page)
    {
        try
        {
            return HTMLExtractionUtils.parseDateFromTimeStamp(timeStampString,
                    "EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH, page);
        } catch (Exception e)
        {
            e.printStackTrace();
            logger.error("Error while parsing timestamp: " + timeStampString + ", for page " + page.getUrl());
            logger.error(e.toString());
        }
        return null;
    }


    /**
     * Compare both date and validate if the new date is younger/newer then the old one.
     *
     * @param newDate Date Object
     * @param oldDate Date Object
     * @return boolean: True if new Date is younger
     */
    private boolean pageHasModifiedDate(Date newDate, Date oldDate)
    {
        if (newDate != null && oldDate != null)
        {
            if (newDate.after(oldDate))
            {
                return true;
            } else
            {
                logger.info("Not Modified: " + newDate.toString() + "|Old Date: " + oldDate.toString());
                return false;
            }
        } else
        {
            return false;
        }
    }

    /**
     * Extract all links from a page and add them to the local foundLinks param
     *
     * @param page Page object to search for links
     */
    private void extractLinks(Page page)
    {

        for (String l : HTMLExtractionUtils.getLinksFromDocument(page))
        {
            if (PageValidation.urlAllowed(this.allowedDomains, this.foundAndVisitedUrlsWithTimestamp, this.deadLinks,
                    this.deniedDomains, l))
            {
                //add link to list if it doesn't already exist in list and is not already declared as dead Link
                if (!foundLinks.contains(l) && !this.deadLinks.contains(l) && !this.foundAndVisitedUrls.contains(l))
                {
                    foundLinks.add(l);
                }
            } else
            {
                this.deadLinks.add(l);
            }
        }
    }


    public void setCrawlingDelay(Integer crawlingDelay)
    {
        this.crawlingDelay = crawlingDelay;
    }


    public Integer getCrawlingDelay()
    {
        return crawlingDelay;
    }


    public Integer getMaxPages()
    {
        return maxPages;
    }

    public void setMaxPages(Integer maxPages)
    {
        this.maxPages = maxPages;
    }

    public boolean isFollowRedirects()
    {
        return followRedirects;
    }

    public void setFollowRedirects(boolean followRedirects)
    {
        this.followRedirects = followRedirects;
    }

    public void setTimeout(Integer timeout)
    {
        this.timeout = timeout;
    }

    /**
     * Use this method to process Pages in own pipeline
     * Add links to super.foundLinks if you want to change the abstact class link management
     *
     * @param page Page Object
     */
    protected abstract void pagePipeline(Page page);

    public void setCrawlerEntityrepository(CrawlerEntityRepository crawlerEntityRepository)
    {
        this.crawlerEntityRepository = crawlerEntityRepository;
    }
}