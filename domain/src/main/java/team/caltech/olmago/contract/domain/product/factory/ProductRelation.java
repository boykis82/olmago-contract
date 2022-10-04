package team.caltech.olmago.contract.domain.product.factory;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "prod_rel")
public class ProductRelation {
  public enum ProductRelationType {
    PACKAGE_AND_BASIC_BENEFIT,
    PACKAGE_AND_OPTION
  }
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(name = "main_prod_cd", length = 10, nullable = false)
  private String mainProductCode;
  
  @Enumerated(EnumType.STRING)
  @Column(name = "prod_rel_typ", length = 40, nullable = false)
  private ProductRelationType productRelationType;
  
  @Column(name = "sub_prod_cd", length = 10, nullable = false)
  private String subProductCode;
  
  @Column(name = "sta_dt", nullable = false)
  private LocalDate startDt;
  
  @Column(name = "end_dt", nullable = false)
  private LocalDate endDt;
}
