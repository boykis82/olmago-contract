package team.caltech.olmago.contract.proxy.customer;

import team.caltech.olmago.contract.proxy.customer.dto.LinkedMobilePhoneInfo;

public interface CustomerServiceProxy {
  LinkedMobilePhoneInfo findByCustomerId(long customerId);
}
