package team.caltech.olmago.contract;

import org.springframework.boot.autoconfigure.SpringBootApplication;

//-- bootjar = false이기 때문에 main쪽에 @SpringBootApplication이 정의되어 있지 않음. Test시 오류가 발생하므로 Test쪽에 임시 클래스 만들어서 정의
@SpringBootApplication
public class DomainTestMain {
  public void contextLoads() {}
}
