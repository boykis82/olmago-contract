package team.caltech.olmago.contract.service.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
public class AppConfig {
  private final Integer threadPoolSize;
  private final Integer taskQueueSize;

  @Autowired
  public AppConfig(
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
  
  @PersistenceContext
  private EntityManager em;
  
  @Bean
  public JPAQueryFactory jpaQueryFactory() {
    return new JPAQueryFactory(em);
  }
  
}
