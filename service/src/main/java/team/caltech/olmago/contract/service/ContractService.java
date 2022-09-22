package team.caltech.olmago.contract.service;

import team.caltech.olmago.contract.dto.*;

import java.util.List;

public interface ContractService {
  /* begin of Order Command (from order service) */
  // 가입접수
  List<ContractDto> receiveContractSubscription(ReceiveContractSubscriptionDto dto);
  // 해지접수
  List<ContractDto> receiveContractTermination(ReceiveContractTerminationDto dto);
  // 해지접수취소
  List<ContractDto> cancelContractTerminationReceipt(CancelContractTerminationDto dto);
  // 변경접수
  List<ContractDto> receiveContractChange(ReceiveContractChangeDto dto);
  // 변경접수취소
  List<ContractDto> cancelContractChangeReceipt(CancelContractChangeDto dto);
  /* end of Order Command */
  
  /* begin of Billing Event */
  // 가입완료
  ContractDto completeContractSubscription(CompleteContractSubscriptionDto dto);
  // 정기결제완료
  ContractDto completeRegularPayment(CompleteRegularPaymentDto dto);
  // 해지완료
  ContractDto completeContractTermination(CompleteContractTerminationDto dto);
  /* end of Billing Event */
  
  /* begin of Coupon Command (from front) */
  // 쿠폰할인접수
  ContractDto receiveCouponDiscount(ReceiveCouponDiscountDto dto);
  // 쿠폰할인접수취소
  ContractDto releaseCouponDiscount(ReleaseCouponDiscountDto dto);
  /* end of Coupon Command */

  /* begin of Customer Event */
  // 이동전화연결/해제 또는 요금제 변경 시
  List<ContractDto> changeMobilePhoneRelatedDiscount(ChangeMobilePhoneRelatedDiscountDto dto);
  /* end of Customer Event */
}
