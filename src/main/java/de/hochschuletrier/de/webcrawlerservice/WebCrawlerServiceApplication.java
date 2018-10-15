package de.hochschuletrier.de.webcrawlerservice;

import de.hochschuletrier.de.webcrawlerservice.crawler.controller.GenericCrawlerController;
import de.hochschuletrier.de.webcrawlerservice.crawler.utils.PageValidation;
import de.hochschuletrier.de.webcrawlerservice.persistence.entity.CrawlerEntity;
import de.hochschuletrier.de.webcrawlerservice.persistence.entity.DeadLinkEntity;
import de.hochschuletrier.de.webcrawlerservice.persistence.entity.FileLinkEntity;
import de.hochschuletrier.de.webcrawlerservice.persistence.repository.CrawlerEntityRepository;
import de.hochschuletrier.de.webcrawlerservice.persistence.repository.DeadLinkRepository;
import de.hochschuletrier.de.webcrawlerservice.persistence.repository.FileLinkRepository;
import de.hochschuletrier.de.webcrawlerservice.persistence.repository.WebPageEntityRepository;
import de.hochschuletrier.de.webcrawlerservice.utils.GenericCrawlerData;
import de.hochschuletrier.de.webcrawlerservice.utils.ServiceErrors;
import de.hochschuletrier.de.webcrawlerservice.utils.ServiceMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Main Spring application class which starts the service
 *
 * @author: Oliver Fries
 */
@SpringBootApplication
@EnableEurekaClient
@RestController
@EnableFeignClients
public class WebCrawlerServiceApplication
{
    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CrawlerEntityRepository crawlerEntityRepository;

    @Autowired
    private DeadLinkRepository deadLinkRepository;

    @Autowired
    private FileLinkRepository fileLinkRepository;

    @Autowired
    private WebPageEntityRepository webPageEntityRepository;

    private static final Logger logger = LoggerFactory.getLogger(WebCrawlerServiceApplication.class);

    @RequestMapping("/status")
    public String status()
    {
        return ServiceMessages.requestSuccessful("/status", "request successful");
    }


    @RequestMapping("/status/crawlers")
    public List<CrawlerEntity> crawlerStatus()
    {
        Sort sort = new Sort(Sort.Direction.ASC,"uniqueName");
        return crawlerEntityRepository.findAll(sort);
    }

    @RequestMapping(value = "/dead-links", method = RequestMethod.GET)
    public List<DeadLinkEntity> getDeadLinks()
    {
        Sort sort = new Sort(Sort.Direction.ASC,"url");
        return deadLinkRepository.findAll(sort);
    }

    @RequestMapping(value = "/file-links", method = RequestMethod.GET)
    public List<FileLinkEntity> getFileLinks()
    {
        Sort sort = new Sort(Sort.Direction.ASC,"fileURL");
        return fileLinkRepository.findAll(sort);
    }

    @RequestMapping(value = "/start/generic-crawler", method = RequestMethod.POST, consumes = {"application/json"})
    public String startGenericCrawler(@RequestBody GenericCrawlerData crawlerData)
    {
        String path = "/start/generic-crawler";
        if (crawlerData.getName().isEmpty() || crawlerData.getAllowedDomains().isEmpty() || crawlerData.getSeedURLs().isEmpty())
        {
            return ServiceErrors.missingCrawlerValues(path, crawlerData);
        } else
        {
            for (String url : crawlerData.getSeedURLs())
            {
                if (!PageValidation.isUrlValidURI(url) && !PageValidation.isUrlValidURI(url))
                {
                    return ServiceErrors.urlNotValid(path, url, crawlerData);
                }
            }
            GenericCrawlerController gcc = applicationContext.getBean(GenericCrawlerController.class);
            gcc.init(crawlerData, crawlerEntityRepository, deadLinkRepository, webPageEntityRepository, fileLinkRepository);
            taskExecutor.execute(gcc);
            return ServiceMessages.requestSuccessful(path, "CrawlerEntity started: " + crawlerData.getName());
        }
    }

    public static void main(String[] args)
    {
        SpringApplication.run(WebCrawlerServiceApplication.class, args);
    }
}
