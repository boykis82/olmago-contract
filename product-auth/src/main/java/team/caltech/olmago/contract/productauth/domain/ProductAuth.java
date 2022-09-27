package team.caltech.olmago.contract.productauth.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
public class ProductAuth {
  @EmbeddedId
  ProductAuthId id;
  
  private String productCode;
  private LocalDateTime firstAuthReqDtm;
  private LocalDateTime lastAuthCompletedDtm;
  private LocalDateTime authExpiredDtm;
  
  @Builder
  public ProductAuth(ProductAuthId id, String productCode, LocalDateTime firstAuthReqDtm) {
    this.id = id;
    this.productCode = productCode;
    this.firstAuthReqDtm = firstAuthReqDtm;
  }
  
  public void completeAuth(LocalDateTime authDtm) {
    lastAuthCompletedDtm = authDtm;
  }
  
  public void expireAuth(LocalDateTime expireDtm) {
    authExpiredDtm = expireDtm;
  }
}
