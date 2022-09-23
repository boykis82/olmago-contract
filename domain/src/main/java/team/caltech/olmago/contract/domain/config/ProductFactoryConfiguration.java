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
//@Lazy
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
                                                   DiscountPolicyRepository discountPolicyRepository) {
    return AllProductsFactory.uzooPassSlimProductFactory(contractRepository, productRepository, discountPolicyRepository);
  }
  
  @Bean
  public ProductFactory googleOneAllProductFactory(ProductRepository productRepository, DiscountPolicyRepository discountPolicyRepository) {
    return AllProductsFactory.googleOneAllProductFactory(productRepository, discountPolicyRepository);
  }
  
  
  @Bean
  public ProductFactory amazonFreeDeliveryProductFactory(ProductRepository productRepository, DiscountPolicyRepository discountPolicyRepository) {
    return AllProductsFactory.amazonFreeDeliveryProductFactory(productRepository, discountPolicyRepository);
  }
  
  @Bean
  public ProductFactory sevenElevenProductFactory(ProductRepository productRepository, DiscountPolicyRepository discountPolicyRepository) {
    return AllProductsFactory.sevenElevenProductFactory(productRepository, discountPolicyRepository);
  }
  
  @Bean
  public ProductFactory twosomePlaceProductFactory(ProductRepository productRepository, DiscountPolicyRepository discountPolicyRepository) {
    return AllProductsFactory.twosomePlaceProductFactory(productRepository, discountPolicyRepository);
  }
  
  @Bean
  public ProductFactory baeminProductFactory(ContractRepository contractRepository,
                                             ProductRepository productRepository,
                                             DiscountPolicyRepository discountPolicyRepository) {
    return AllProductsFactory.baeminProductFactory(contractRepository, productRepository, discountPolicyRepository);
  }
  
  @Bean
  public ProductFactory goobneProductFactory(ProductRepository productRepository, DiscountPolicyRepository discountPolicyRepository) {
    return AllProductsFactory.goobneProductFactory(productRepository, discountPolicyRepository);
  }
  
  @Bean
  public ProductFactory floAndDataProductFactory(ContractRepository contractRepository,
                                                 CustomerServiceProxy customerServiceProxy,
                                                 ProductRepository productRepository,
                                                 DiscountPolicyRepository discountPolicyRepository) {
    return AllProductsFactory.floAndDataProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository);
  }
  
  @Bean
  public ProductFactory floAndDataPlusProductFactory(ContractRepository contractRepository,
                                                     CustomerServiceProxy customerServiceProxy,
                                                     ProductRepository productRepository,
                                                     DiscountPolicyRepository discountPolicyRepository) {
    return AllProductsFactory.floAndDataPlusProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository);
  }
  
  @Bean
  public ProductFactory gamepassUltimateProductFactory(ContractRepository contractRepository,
                                                       ProductRepository productRepository,
                                                       DiscountPolicyRepository discountPolicyRepository) {
    return AllProductsFactory.gamepassUltimateProductFactory(contractRepository, productRepository, discountPolicyRepository);
  }
  
  
  @Bean
  public ProductFactory googleOneMiniProductFactory(ProductRepository productRepository, DiscountPolicyRepository discountPolicyRepository) {
    return AllProductsFactory.googleOneMiniProductFactory(productRepository, discountPolicyRepository);
  }
  
  @Bean
  public ProductFactory yanoljaProductFactory(ProductRepository productRepository,DiscountPolicyRepository discountPolicyRepository) {
    return AllProductsFactory.yanoljaProductFactory(productRepository, discountPolicyRepository);
  }

  @Bean
  public ProductFactoryMap productFactoryMap(ContractRepository contractRepository,
                                             CustomerServiceProxy customerServiceProxy,
                                             ProductRepository productRepository,
                                             DiscountPolicyRepository discountPolicyRepository,
                                             ProductRelationRepository productRelationRepository) {
    ProductFactoryMap productFactoryMap = new ProductFactoryMap();

    // cache화
    discountPolicyRepository.findAll();

    // package
    productFactoryMap.put(uzooPassAllProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository, productRelationRepository));
    
    productFactoryMap.put(uzooPassLifeProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository, productRelationRepository));
    productFactoryMap.put(uzooPassMiniProductFactory(contractRepository, productRepository, discountPolicyRepository, productRelationRepository));
    productFactoryMap.put(uzooPassSlimProductFactory(contractRepository, productRepository, discountPolicyRepository));
    
    // 기본혜택
    productFactoryMap.put(googleOneAllProductFactory(productRepository, discountPolicyRepository));
    productFactoryMap.put(amazonFreeDeliveryProductFactory(productRepository, discountPolicyRepository));
    productFactoryMap.put(sevenElevenProductFactory(productRepository, discountPolicyRepository));
    productFactoryMap.put(twosomePlaceProductFactory(productRepository, discountPolicyRepository));
    
    // 옵션 or 단품
    productFactoryMap.put(baeminProductFactory(contractRepository, productRepository, discountPolicyRepository));
    productFactoryMap.put(goobneProductFactory(productRepository, discountPolicyRepository));
    productFactoryMap.put(floAndDataProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository));
    productFactoryMap.put(floAndDataPlusProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository));
    productFactoryMap.put(gamepassUltimateProductFactory(contractRepository, productRepository, discountPolicyRepository));
    productFactoryMap.put(googleOneMiniProductFactory(productRepository, discountPolicyRepository));
    productFactoryMap.put(yanoljaProductFactory(productRepository, discountPolicyRepository));
    
    return productFactoryMap;
  }
  
 
}
