package team.caltech.olmago.contract.proxy.associatedcompany;

import java.time.LocalDateTime;
import java.util.List;

public interface AssociatedCompanyServiceProxy {
  void subscribe(long contractId, List<String> productCodes, LocalDateTime subDtm);
  void terminate(long contractId, List<String> productCodes, LocalDateTime termDtm);
}
