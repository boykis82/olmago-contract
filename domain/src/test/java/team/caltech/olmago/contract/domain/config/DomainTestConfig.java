package team.caltech.olmago.contract.domain.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

//-- jpaQueryFacetory는 DataJpaTest에서는 못 가져온다.
@TestConfiguration
public class DomainTestConfig {
  @PersistenceContext
  private EntityManager em;
  
  @Bean
  public JPAQueryFactory jpaQueryFactory() {
    return new JPAQueryFactory(em);
  }
}
