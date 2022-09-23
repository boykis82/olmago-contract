package team.caltech.olmago.contract.service.service;

import team.caltech.olmago.contract.service.dto.*;

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
  // 가입접수취소(최초 결제 후 환불 시)
  ContractDto cancelContractSubscriptionReceipt(CancelContractSubscriptionDto dto);
  // 정기결제완료 시 상품활성화 또는 비활성화
  ContractDto activateOrDeactivateProducts(ActivateOrDeactivateProductDto dto);
  // 정기결제환불완료 시 활성화 중단(?)
  ContractDto holdActivation(HoldActivationDto dto);
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
  
  /* 조회 */
  // 고객ID로 조회. 해지서비스포함여부
  List<ContractDto> findByCustomerId(long customerId, boolean includeTerminatedContract);
  // 계약ID로 조회. 패키지나 옵션일 경우 짝꿍도 같이 조회할지 여부
  List<ContractDto> findByContractId(long contractId, boolean withPackageOrOption);
}
