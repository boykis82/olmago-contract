package team.caltech.olmago.contract.service;

import team.caltech.olmago.contract.dto.*;

import java.util.List;

public interface ContractService {
  List<ContractDto> receiveContractSubscription(ReceiveContractSubscriptionDto dto);
  ContractDto completeContractSubscription(CompleteContractSubscriptionDto dto);
  
  ContractDto completeRegularPayment(CompleteRegularPaymentDto dto);
  
  List<ContractDto> receiveContractTermination(ReceiveContractTerminationDto dto);
  List<ContractDto> cancelContractTerminationReceipt(CancelContractTerminationDto dto);
  ContractDto completeContractTermination(CompleteContractTerminationDto dto);
  
  List<ContractDto> receiveContractChange(ReceiveContractChangeDto dto);
  List<ContractDto> cancelContractChangeReceipt(CancelContractChangeDto dto);
  
  ContractDto receiveCouponDiscount(ReceiveCouponDiscountDto dto);
}
