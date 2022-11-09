package team.caltech.olmago;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//-- 멀티 모듈이라고 해도 component scan을 위해 최상위 패키지로 정의 (https://backtony.github.io/spring/2022-06-02-spring-module-1/)
@SpringBootApplication
public class OlmagoContractApplication {

	public static void main(String[] args) {
		SpringApplication.run(OlmagoContractApplication.class, args);
	}

}
