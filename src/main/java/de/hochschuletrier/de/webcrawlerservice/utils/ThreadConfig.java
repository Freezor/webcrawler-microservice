package de.hochschuletrier.de.webcrawlerservice.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configuration class for the Thread execution of the Crawler
 *
 * @author: Oliver Fries
 */
@Configuration
public class ThreadConfig
{
    @Bean
    public TaskExecutor threadPoolTaskExecutor()
    {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setThreadNamePrefix("default_task_executor_thread");
        executor.initialize();

        return executor;
    }
}
