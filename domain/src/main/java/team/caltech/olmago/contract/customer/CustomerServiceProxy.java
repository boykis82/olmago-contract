package team.caltech.olmago.contract.customer;

public interface CustomerServiceProxy {
  LinkedMobilePhoneInfo findByCustomerId(long customerId);
}
