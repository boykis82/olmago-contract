package team.caltech.olmago.contract;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//-- 멀티 모듈이라고 해도 component scan을 위해 최상위 패키지로 정의
@SpringBootApplication
@EnableScheduling
public class ProductAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductAuthApplication.class, args);
	}

}

/*

  가입요청일시, 가입완료일시
  해지요청일시, 해지완료일시
  
  1. contract subscription completed event받으면
     1) contract의 상품 정보 조회 api호출
     2) contract id & 상품 id를 key로 하여 persist
        있으면 -> 오류
        없으면 -> 최초인증 (제휴사로 인증 정보 전송. 계약ID+상품가입ID)
        
  2. activateordeactivate event받으면
     1) payload에 이미 해지,가입대상 존재
     2) contract id & 상품 id를 key로 하여 persist
        가입된것만 인증 정보 전송
        해지된건 인증 해지 정보 전송 자체가 불필요할듯..
  
  
  인증정보 조회 api
    contract id 단위
    contract id + 상품가입id 단위
    
  제휴사에 인증 종료 api 제공
    contract id + 상품가입ID
 
 */