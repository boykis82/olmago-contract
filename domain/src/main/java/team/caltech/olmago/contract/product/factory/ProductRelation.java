package team.caltech.olmago.contract.product.factory;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
public class ProductRelation {
  public enum ProductRelationType {
    PACKAGE_AND_BASIC_BENEFIT,
    PACKAGE_AND_OPTION
  }
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(nullable = false)
  private String mainProductCode;
  
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ProductRelationType productRelationType;
  
  @Column(nullable = false)
  private String subProductCode;
  
  @Column(nullable = false)
  private LocalDate startDt;
  
  @Column(nullable = false)
  private LocalDate endDt;
}
