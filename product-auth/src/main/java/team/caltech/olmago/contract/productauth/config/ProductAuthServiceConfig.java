package team.caltech.olmago.contract.productauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
public class ProductAuthServiceConfig {
  private final Integer threadPoolSize;
  private final Integer taskQueueSize;

  @Autowired
  public ProductAuthServiceConfig(
      @Value("${app.threadPoolSize:2}") Integer threadPoolSize,
      @Value("${app.taskQueueSize:2}") Integer taskQueueSize
  ) {
    this.threadPoolSize = threadPoolSize;
    this.taskQueueSize = taskQueueSize;
  }

  @Bean
  public Scheduler otherServiceCommScheduler() {
    return Schedulers.newBoundedElastic(threadPoolSize, taskQueueSize, "other-service-comm-pool");
  }
  
  @Bean
  public WebClient contractWebClient() {
    return WebClient.create("http://olmago/api/v1/contract");
  }
}
