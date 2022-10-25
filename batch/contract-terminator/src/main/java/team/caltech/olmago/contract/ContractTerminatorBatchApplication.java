package team.caltech.olmago.contract;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class ContractTerminatorBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContractTerminatorBatchApplication.class, args);
	}

}
