package team.caltech.olmago.contract.service.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.caltech.olmago.contract.common.message.MessageEnvelope;
import team.caltech.olmago.contract.common.message.MessageEnvelopeRepository;
import team.caltech.olmago.contract.service.dto.ContractDto;
import team.caltech.olmago.contract.service.service.ContractService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/olmago/api/v1/contracts")
public class ContractController {
  private final ContractService contractService;
  private final MessageEnvelopeRepository messageEnvelopeRepository;
  
  @GetMapping
  public ResponseEntity<List<ContractDto>> findByCustomerId(@RequestParam("customerId") long customerId, @RequestParam("includeTerminatedContract") boolean includeTerminatedContract) {
    return ResponseEntity.ok(contractService.findByCustomerId(customerId, includeTerminatedContract));
  }
  
  @GetMapping("/{id}")
  public ResponseEntity<List<ContractDto>> findByContractId(@PathVariable("id") long id,
                                                            @RequestParam("withPackageOrOption") boolean withPackageOrOption,
                                                            @RequestParam("includeProductAndDiscount") boolean includeProductAndDiscount) {
    return ResponseEntity.ok(contractService.findByContractId(id, withPackageOrOption, includeProductAndDiscount));
  }
  
  @GetMapping("/messages")
  public ResponseEntity<List<MessageEnvelope>> findAllMessageEnvelope() {
    return ResponseEntity.ok(messageEnvelopeRepository.findAll());
  }
}
