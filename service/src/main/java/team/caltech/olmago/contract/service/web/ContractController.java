package team.caltech.olmago.contract.service.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import team.caltech.olmago.contract.service.service.ContractService;

@RequiredArgsConstructor
@RestController
public class ContractController {
  private final ContractService contractService;
}
