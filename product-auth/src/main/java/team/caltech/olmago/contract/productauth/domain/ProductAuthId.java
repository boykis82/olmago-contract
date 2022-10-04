package team.caltech.olmago.contract.productauth.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Embeddable
public class ProductAuthId implements Serializable {
  @Column(name = "contract_id")
  private Long contractId;
  @Column(name = "prod_cd")
  private String productCode;
}
