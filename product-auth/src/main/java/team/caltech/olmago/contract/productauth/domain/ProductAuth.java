package team.caltech.olmago.contract.productauth.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
public class ProductAuth {
  @EmbeddedId
  ProductAuthId id;
  
  @Column(name = "product_code", nullable = false)
  private String productCode;
  
  @Column(name = "first_auth_req_dtm", nullable = false)
  private LocalDateTime firstAuthReqDtm;
  
  @Column(name = "last_auth_completed_dtm")
  private LocalDateTime lastAuthCompletedDtm;
  
  @Column(name = "auth_expired_dtm")
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
