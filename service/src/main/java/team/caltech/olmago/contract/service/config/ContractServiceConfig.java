package team.caltech.olmago.contract.service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import team.caltech.olmago.contract.domain.customer.CustomerServiceProxy;

@SuppressWarnings("unused")
@Configuration
public class ContractServiceConfig {
  private final Integer threadPoolSize;
  private final Integer taskQueueSize;

  @Autowired
  public ContractServiceConfig(
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

}
