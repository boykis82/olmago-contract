package team.caltech.olmago.contract;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//-- 멀티 모듈이라고 해도 component scan을 위해 최상위 패키지로 정의
@SpringBootApplication
@EnableScheduling
public class MessageBusApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessageBusApplication.class, args);
	}

}
