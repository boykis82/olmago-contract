package team.caltech.olmago.contract.productauth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Embeddable
public class ProductAuthId implements Serializable {
  private Long contractId;
  private Long productSubscriptionId;

}
