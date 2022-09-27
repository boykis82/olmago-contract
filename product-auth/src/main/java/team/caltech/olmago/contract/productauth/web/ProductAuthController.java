package team.caltech.olmago.contract.productauth.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.caltech.olmago.contract.productauth.dto.CompleteProductAuthDto;
import team.caltech.olmago.contract.productauth.dto.ExpireProductAuthDto;
import team.caltech.olmago.contract.productauth.dto.ProductAuthDto;
import team.caltech.olmago.contract.productauth.service.ProductAuthService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/olmago/api/v1/contract")
public class ProductAuthController {
  private final ProductAuthService productAuthService;
  
  @GetMapping("/{contractId}/product-subscriptions/auth")
  public ResponseEntity<List<ProductAuthDto>> getProductAuths(@RequestParam("contractId") long contractId) {
    return ResponseEntity.ok(productAuthService.getProductAuths(contractId));
  }
  
  @PutMapping("/{contractId}/product-subscriptions/{productSubscriptionId}/auth/complete")
  public ResponseEntity<Void> completeAuth(@RequestParam("contractId") long contractId,
                                           @RequestParam("productSubscriptionId") long productSubscriptionId,
                                           @RequestBody CompleteProductAuthDto dto) {
    productAuthService.completeAuth(dto);
    return ResponseEntity.ok().build();
  }
  
  @PutMapping("/{contractId}/product-subscriptions/{productSubscriptionId}/auth/expire")
  public ResponseEntity<Void> completeAuth(@RequestParam("contractId") long contractId,
                                           @RequestParam("productSubscriptionId") long productSubscriptionId,
                                           @RequestBody ExpireProductAuthDto dto) {
    productAuthService.expireAuth(dto);
    return ResponseEntity.ok().build();
  }
}
