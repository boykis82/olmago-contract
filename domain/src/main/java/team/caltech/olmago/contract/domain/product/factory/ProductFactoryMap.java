package team.caltech.olmago.contract.domain.product.factory;

import java.util.HashMap;
import java.util.Map;

public class ProductFactoryMap {
  Map<String, ProductFactory> productFactoryMap = new HashMap<>();
  
  public void put(ProductFactory productFactory) {
    productFactoryMap.put(productFactory.productCode(), productFactory);
  }
  
  public ProductFactory get(String productCode) {
    return productFactoryMap.get(productCode);
  }
}
