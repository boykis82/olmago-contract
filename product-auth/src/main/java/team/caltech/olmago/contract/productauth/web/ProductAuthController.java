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
@RequestMapping("/olmago/api/v1/contract-auths")
public class ProductAuthController {
  private final ProductAuthService productAuthService;
  
  @GetMapping("/{contractId}")
  public ResponseEntity<List<ProductAuthDto>> getProductAuths(@RequestParam("contractId") long contractId) {
    return ResponseEntity.ok(productAuthService.getProductAuths(contractId));
  }
  
  @PutMapping("/{contractId}/{productCode}/complete")
  public ResponseEntity<Void> completeAuth(@PathVariable("contractId") long contractId,
                                           @PathVariable("productCode") long productCode,
                                           @RequestBody CompleteProductAuthDto dto) {
    productAuthService.completeAuth(dto);
    return ResponseEntity.ok().build();
  }
  
  @PutMapping("/{contractId}/{productCode}/expire")
  public ResponseEntity<Void> completeAuth(@PathVariable("contractId") long contractId,
                                           @PathVariable("productCode") long productCode,
                                           @RequestBody ExpireProductAuthDto dto) {
    productAuthService.expireAuth(dto);
    return ResponseEntity.ok().build();
  }
}
