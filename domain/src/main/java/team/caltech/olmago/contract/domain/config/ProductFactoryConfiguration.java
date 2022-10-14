package team.caltech.olmago.contract.domain.config;

import org.springframework.beans.factory.annotation.Autowired;
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
  private final ContractRepository contractRepository;
  private final CustomerServiceProxy customerServiceProxy;
  private final ProductRepository productRepository;
  private final DiscountPolicyRepository discountPolicyRepository;
  private final ProductRelationRepository productRelationRepository;
  
  @Autowired
  public ProductFactoryConfiguration(ContractRepository contractRepository,
                                     CustomerServiceProxy customerServiceProxy,
                                     ProductRepository productRepository,
                                     DiscountPolicyRepository discountPolicyRepository,
                                     ProductRelationRepository productRelationRepository) {
    this.contractRepository = contractRepository;
    this.customerServiceProxy = customerServiceProxy;
    this.productRepository = productRepository;
    this.discountPolicyRepository = discountPolicyRepository;
    this.productRelationRepository = productRelationRepository;
  }
  
  @Bean
  public ProductFactory uzooPassAllProductFactory() {
    return AllProductsFactory.uzooPassAllProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository, productRelationRepository);
  }

  @Bean
  public ProductFactory uzooPassLifeProductFactory() {
    return AllProductsFactory.uzooPassLifeProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository, productRelationRepository);
  }
 
  @Bean
  public ProductFactory uzooPassMiniProductFactory() {
    return AllProductsFactory.uzooPassMiniProductFactory(contractRepository, productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  @Bean
  public ProductFactory uzooPassSlimProductFactory() {
    return AllProductsFactory.uzooPassSlimProductFactory(contractRepository, productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  @Bean
  public ProductFactory googleOneAllProductFactory() {
    return AllProductsFactory.googleOneAllProductFactory(productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  
  @Bean
  public ProductFactory amazonFreeDeliveryProductFactory() {
    return AllProductsFactory.amazonFreeDeliveryProductFactory(productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  @Bean
  public ProductFactory sevenElevenProductFactory() {
    return AllProductsFactory.sevenElevenProductFactory(productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  @Bean
  public ProductFactory twosomePlaceProductFactory() {
    return AllProductsFactory.twosomePlaceProductFactory(productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  @Bean
  public ProductFactory baeminProductFactory() {
    return AllProductsFactory.baeminProductFactory(contractRepository, productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  @Bean
  public ProductFactory goobneProductFactory() {
    return AllProductsFactory.goobneProductFactory(productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  @Bean
  public ProductFactory floAndDataProductFactory() {
    return AllProductsFactory.floAndDataProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  @Bean
  public ProductFactory floAndDataPlusProductFactory() {
    return AllProductsFactory.floAndDataPlusProductFactory(contractRepository, customerServiceProxy, productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  @Bean
  public ProductFactory gamepassUltimateProductFactory() {
    return AllProductsFactory.gamepassUltimateProductFactory(contractRepository, productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  
  @Bean
  public ProductFactory googleOneMiniProductFactory() {
    return AllProductsFactory.googleOneMiniProductFactory(productRepository, discountPolicyRepository, productRelationRepository);
  }
  
  @Bean
  public ProductFactory yanoljaProductFactory() {
    return AllProductsFactory.yanoljaProductFactory(productRepository, discountPolicyRepository, productRelationRepository);
  }

  @Bean
  public ProductFactoryMap productFactoryMap() {
    ProductFactoryMap productFactoryMap = new ProductFactoryMap();

    // package
    productFactoryMap.put(uzooPassAllProductFactory());
    
    productFactoryMap.put(uzooPassLifeProductFactory());
    productFactoryMap.put(uzooPassMiniProductFactory());
    productFactoryMap.put(uzooPassSlimProductFactory());
    
    // 기본혜택
    productFactoryMap.put(googleOneAllProductFactory());
    productFactoryMap.put(amazonFreeDeliveryProductFactory());
    productFactoryMap.put(sevenElevenProductFactory());
    productFactoryMap.put(twosomePlaceProductFactory());
    
    // 옵션 or 단품
    productFactoryMap.put(baeminProductFactory());
    productFactoryMap.put(goobneProductFactory());
    productFactoryMap.put(floAndDataProductFactory());
    productFactoryMap.put(floAndDataPlusProductFactory());
    productFactoryMap.put(gamepassUltimateProductFactory());
    productFactoryMap.put(googleOneMiniProductFactory());
    productFactoryMap.put(yanoljaProductFactory());
    
    return productFactoryMap;
  }
  
 
}
