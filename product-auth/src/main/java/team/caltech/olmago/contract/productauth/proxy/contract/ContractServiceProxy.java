package team.caltech.olmago.contract.productauth.proxy.contract;


import reactor.core.publisher.Mono;

public interface ContractServiceProxy {
  Mono<ContractDto> getContract(long contractId);
}
