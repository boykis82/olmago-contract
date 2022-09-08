package team.caltech.olmago.contract.product.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductFactoryMap {
  Map<String, ProductFactory> productFactoryMap = new HashMap<>();
  
  public void put(ProductFactory productFactory) {
    productFactoryMap.put(productFactory.productCode(), productFactory);
  }
  
  public ProductFactory get(String productCode) {
    return productFactoryMap.get(productCode);
  }
}
