package team.caltech.olmago.contract.service.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.caltech.olmago.common.message.MessageEnvelope;
import team.caltech.olmago.common.message.MessageEnvelopeRepository;
import team.caltech.olmago.contract.domain.contract.CalculationResult;
import team.caltech.olmago.contract.service.dto.ContractDto;
import team.caltech.olmago.contract.service.service.ContractService;

import java.time.LocalDate;
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
  
  @GetMapping("/{id}/calculate")
  public ResponseEntity<List<CalculationResult>> calculate(@PathVariable("id") long id,
                                                           @RequestParam("withPackageOrOption") boolean withPackageOrOption,
                                                           @RequestParam("calculateDate") LocalDate calculateDate) {
    return ResponseEntity.ok(contractService.calculate(id, withPackageOrOption, calculateDate));
  }
  
  @GetMapping("/messages")
  public ResponseEntity<List<MessageEnvelope>> findAllMessageEnvelope() {
    return ResponseEntity.ok(messageEnvelopeRepository.findAll());
  }
}
