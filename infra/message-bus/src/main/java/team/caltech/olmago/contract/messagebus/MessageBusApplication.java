package team.caltech.olmago.contract.messagebus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MessageBusApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessageBusApplication.class, args);
	}

}
