package team.caltech.olmago.contract.configuration;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.caltech.olmago.contract.common.message.MessageEnvelope;
import team.caltech.olmago.contract.domain.contract.Contract;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

@RequiredArgsConstructor
@Configuration
public class JobConfiguration {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final EntityManagerFactory entityManagerFactory;

  private final int chunkSize = 10;

  @AllArgsConstructor
  static class DomainEventWithEntity {
    Contract contract;
    Object event;
  }

  @Bean
  public Job job() {
    return jobBuilderFactory.get("contractTerminatorJob")
        .start(passoverPaymentDateTerminatorStep())
        .next(terminationReservedTerminatorStep())
        .build();
  }

  /*
    결제 실패하거나 취소했을 시, 이전과금종료일로부터 이틀 경과하면 해지하는 step
   */
  @Bean
  public Step passoverPaymentDateTerminatorStep() {
    return stepBuilderFactory.get("passoverPaymentDateTerminatorStep")
        .<Contract, Contract>chunk(chunkSize)
        .reader(passoverPaymentDateTerminatorReader(null))
        .processor(passoverPaymentDateTerminatorProcessor())
        .writer(terminatorWriter())
        .build();
  }

  @Bean
  @StepScope
  public JpaPagingItemReader<Contract> passoverPaymentDateTerminatorReader(@Value("#{jobParameters['strdDate']}") LocalDate strdDate) {
    /*
      ex) 과금주기가 0807~0906이면 0906 야간에 결제가 일어나야 함
        결제가 실패하면 0908 야간에 해지됨
     */
    return new JpaPagingItemReaderBuilder<Contract>()
        .name("passoverPaymentDateTerminatorReader")
        .entityManagerFactory(entityManagerFactory)
        .pageSize(chunkSize)
        .queryString(
            "SELECT DISTINCT c " +
            "FROM Contract c " +
            "     JOIN FETCH ProductSubscription ps " +
            "     JOIN FETCH DiscountSubscription ds " +
            "WHERE c.lifeCycle.terminationCompletedDateTime IS NULL " +
            "AND c.lifeCycle.terminationReceivedDateTime IS NULL " +
            "AND c.billCycle.currentBillEndDate <= :strdDate "
        )
        .parameterValues(Collections.singletonMap("strdDate", strdDate.minusDays(2L)))
        .build();
  }

  @Bean
  @StepScope
  public ItemProcessor<Contract, DomainEventWithEntity> passoverPaymentDateTerminatorProcessor() {
    return contract -> new DomainEventWithEntity(contract, contract.completeTermination(LocalDateTime.now()));
  }

  /*
  해지 예약한 계약을 과금종료일 야간에 해지하는 배치
 */
  @Bean
  public Step terminationReservedTerminatorStep() {
    return stepBuilderFactory.get("terminationReservedTerminatorStep")
        .<Contract, Contract>chunk(chunkSize)
        .reader(terminationReservedTerminatorReader(null))
        .processor(terminationReservedTerminatorProcessor())
        .writer(terminatorWriter())
        .build();
  }

  @Bean
  @StepScope
  public JpaPagingItemReader<Contract> terminationReservedTerminatorReader(@Value("#{jobParameters['strdDate']}") LocalDate strdDate) {
        /*
      ex) 과금주기가 0807~0906이면 0906 야간에 결제가 일어나야 함
        0901에 해지예약했으면
        0906 야간에 결제 안 일어나고 해지
     */
    return new JpaPagingItemReaderBuilder<Contract>()
        .name("passoverPaymentDateTerminatorReader")
        .entityManagerFactory(entityManagerFactory)
        .pageSize(chunkSize)
        .queryString(
            "SELECT DISTINCT c " +
            "FROM Contract c " +
            "     JOIN FETCH ProductSubscription ps " +
            "     JOIN FETCH DiscountSubscription ds " +
            "WHERE c.lifeCycle.terminationCompletedDateTime IS NULL " +
            "AND c.lifeCycle.terminationReceivedDateTime IS NOT NULL " +
            "AND c.billCycle.currentBillEndDate = :strdDate "
        )
        .parameterValues(Collections.singletonMap("strdDate", strdDate))
        .build();
  }

  @Bean
  @StepScope
  public ItemProcessor<Contract, Contract> terminationReservedTerminatorProcessor() {

    return contract -> {
      contract.completeTermination(LocalDateTime.now());
      return contract;
    };
  }

  @Bean
  public CompositeItemWriter<DomainEventWithEntity> domainAndEventWriter() {
    CompositeItemWriter<DomainEventWithEntity> writer = new CompositeItemWriter<>();
    writer.setDelegates();
  }

  @Bean
  public JpaItemWriter<Contract> terminatorWriter() {
    JpaItemWriter<Contract> itemWriter = new JpaItemWriter<>();
    itemWriter.setEntityManagerFactory(entityManagerFactory);
    return itemWriter;
  }

  @Bean
  public JpaItemWriter<MessageEnvelope> domainEventWriter() {
    JpaItemWriter<MessageEnvelope> itemWriter = new JpaItemWriter<>();
    itemWriter.setEntityManagerFactory(entityManagerFactory);
    return itemWriter;
  }
}
