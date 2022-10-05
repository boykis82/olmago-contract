package team.caltech.olmago.contract.domain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import team.caltech.olmago.contract.domain.contract.ContractRepository;
import team.caltech.olmago.contract.domain.customer.CustomerServiceProxy;
import team.caltech.olmago.contract.domain.plm.discount.DiscountPolicyRepository;
import team.caltech.olmago.contract.domain.plm.product.ProductRepository;
import team.caltech.olmago.contract.domain.product.factory.AllProductsFactory;
import team.caltech.olmago.contract.domain.product.factory.ProductFactory;
import team.caltech.olmago.contract.domain.product.factory.ProductFactoryMap;
import team.caltech.olmago.contract.domain.product.factory.ProductRelationRepository;

@Configuration
@Lazy
public class ProductFactoryConfiguration {
  @Bean
  public ProductFactory uzooPassAllProductFactory(ContractRepository contractRepository,
                                                  CustomerServiceProxy customerServiceProxy,
                                                  ProductRepository productRepository,
                                                  DiscountPolicyRepository discountPolicyRepository,
                                                  ProductRelationRepository productRelationRepository
  ) {
    return AllProductsFactory.uzooPassAllProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository, productRelationRepository);
  }

  @Bean
  public ProductFactory uzooPassLifeProductFactory(ContractRepository contractRepository,
                                                   CustomerServiceProxy customerServiceProxy,
                                                   ProductRepository productRepository,
                                                   DiscountPolicyRepository discountPolicyRepository,
                                                   ProductRelationRepository productRelationRepository) {
    return AllProductsFactory.uzooPassLifeProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository, productRelationRepository);
  }
 
  @Bean
  public ProductFactory uzooPassMiniProductFactory(ContractRepository contractRepository,
                                                   ProductRepository productRepository,
                                                   DiscountPolicyRepository discountPolicyRepository,
                                                   ProductRelationRepository productRelationRepository) {
    return AllProductsFactory.uzooPassMiniProductFactory(contractRepository, productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  @Bean
  public ProductFactory uzooPassSlimProductFactory(ContractRepository contractRepository,
                                                   ProductRepository productRepository,
                                                   DiscountPolicyRepository discountPolicyRepository,
                                                   ProductRelationRepository productRelationRepository) {
    return AllProductsFactory.uzooPassSlimProductFactory(contractRepository, productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  @Bean
  public ProductFactory googleOneAllProductFactory(ProductRepository productRepository,
                                                   DiscountPolicyRepository discountPolicyRepository,
                                                   ProductRelationRepository productRelationRepository) {
    return AllProductsFactory.googleOneAllProductFactory(productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  
  @Bean
  public ProductFactory amazonFreeDeliveryProductFactory(ProductRepository productRepository,
                                                         DiscountPolicyRepository discountPolicyRepository,
                                                         ProductRelationRepository productRelationRepository) {
    return AllProductsFactory.amazonFreeDeliveryProductFactory(productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  @Bean
  public ProductFactory sevenElevenProductFactory(ProductRepository productRepository,
                                                  DiscountPolicyRepository discountPolicyRepository,
                                                  ProductRelationRepository productRelationRepository) {
    return AllProductsFactory.sevenElevenProductFactory(productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  @Bean
  public ProductFactory twosomePlaceProductFactory(ProductRepository productRepository,
                                                   DiscountPolicyRepository discountPolicyRepository,
                                                   ProductRelationRepository productRelationRepository) {
    return AllProductsFactory.twosomePlaceProductFactory(productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  @Bean
  public ProductFactory baeminProductFactory(ContractRepository contractRepository,
                                             ProductRepository productRepository,
                                             DiscountPolicyRepository discountPolicyRepository,
                                             ProductRelationRepository productRelationRepository) {
    return AllProductsFactory.baeminProductFactory(contractRepository, productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  @Bean
  public ProductFactory goobneProductFactory(ProductRepository productRepository,
                                             DiscountPolicyRepository discountPolicyRepository,
                                             ProductRelationRepository productRelationRepository) {
    return AllProductsFactory.goobneProductFactory(productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  @Bean
  public ProductFactory floAndDataProductFactory(ContractRepository contractRepository,
                                                 CustomerServiceProxy customerServiceProxy,
                                                 ProductRepository productRepository,
                                                 DiscountPolicyRepository discountPolicyRepository,
                                                 ProductRelationRepository productRelationRepository) {
    return AllProductsFactory.floAndDataProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  @Bean
  public ProductFactory floAndDataPlusProductFactory(ContractRepository contractRepository,
                                                     CustomerServiceProxy customerServiceProxy,
                                                     ProductRepository productRepository,
                                                     DiscountPolicyRepository discountPolicyRepository,
                                                     ProductRelationRepository productRelationRepository) {
    return AllProductsFactory.floAndDataPlusProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  @Bean
  public ProductFactory gamepassUltimateProductFactory(ContractRepository contractRepository,
                                                       ProductRepository productRepository,
                                                       DiscountPolicyRepository discountPolicyRepository,
                                                       ProductRelationRepository productRelationRepository) {
    return AllProductsFactory.gamepassUltimateProductFactory(contractRepository, productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  
  @Bean
  public ProductFactory googleOneMiniProductFactory(ProductRepository productRepository,
                                                    DiscountPolicyRepository discountPolicyRepository,
                                                    ProductRelationRepository productRelationRepository) {
    return AllProductsFactory.googleOneMiniProductFactory(productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  @Bean
  public ProductFactory yanoljaProductFactory(ProductRepository productRepository,
                                              DiscountPolicyRepository discountPolicyRepository,
                                              ProductRelationRepository productRelationRepository) {
    return AllProductsFactory.yanoljaProductFactory(productRepository, discountPolicyRepository, productRelationRepository);
  }

  @Bean
  public ProductFactoryMap productFactoryMap(ContractRepository contractRepository,
                                             CustomerServiceProxy customerServiceProxy,
                                             ProductRepository productRepository,
                                             DiscountPolicyRepository discountPolicyRepository,
                                             ProductRelationRepository productRelationRepository) {
    ProductFactoryMap productFactoryMap = new ProductFactoryMap();

    // package
    productFactoryMap.put(uzooPassAllProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository, productRelationRepository));
    
    productFactoryMap.put(uzooPassLifeProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository, productRelationRepository));
    productFactoryMap.put(uzooPassMiniProductFactory(contractRepository, productRepository, discountPolicyRepository, productRelationRepository));
    productFactoryMap.put(uzooPassSlimProductFactory(contractRepository, productRepository, discountPolicyRepository, productRelationRepository));
    
    // 기본혜택
    productFactoryMap.put(googleOneAllProductFactory(productRepository, discountPolicyRepository, productRelationRepository));
    productFactoryMap.put(amazonFreeDeliveryProductFactory(productRepository, discountPolicyRepository, productRelationRepository));
    productFactoryMap.put(sevenElevenProductFactory(productRepository, discountPolicyRepository, productRelationRepository));
    productFactoryMap.put(twosomePlaceProductFactory(productRepository, discountPolicyRepository, productRelationRepository));
    
    // 옵션 or 단품
    productFactoryMap.put(baeminProductFactory(contractRepository, productRepository, discountPolicyRepository, productRelationRepository));
    productFactoryMap.put(goobneProductFactory(productRepository, discountPolicyRepository, productRelationRepository));
    productFactoryMap.put(floAndDataProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository, productRelationRepository));
    productFactoryMap.put(floAndDataPlusProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository, productRelationRepository));
    productFactoryMap.put(gamepassUltimateProductFactory(contractRepository, productRepository, discountPolicyRepository, productRelationRepository));
    productFactoryMap.put(googleOneMiniProductFactory(productRepository, discountPolicyRepository, productRelationRepository));
    productFactoryMap.put(yanoljaProductFactory(productRepository, discountPolicyRepository, productRelationRepository));
    
    return productFactoryMap;
  }
  
 
}
